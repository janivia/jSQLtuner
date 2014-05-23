package pl.piotrsukiennik.tuner.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.piotrsukiennik.tuner.CompositeDataSource;
import pl.piotrsukiennik.tuner.DataSource;
import pl.piotrsukiennik.tuner.dto.ReadQueryExecutionResult;
import pl.piotrsukiennik.tuner.dto.ReadQueryExecutionResultBuilder;
import pl.piotrsukiennik.tuner.exception.DataRetrievalException;
import pl.piotrsukiennik.tuner.model.datasource.DataSourceIdentity;
import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.model.query.ReadQuery;
import pl.piotrsukiennik.tuner.service.*;
import pl.piotrsukiennik.tuner.util.RowSet;

import java.sql.ResultSet;
import java.util.*;

/**
 * Author: Piotr Sukiennik
 * Date: 31.08.13
 * Time: 18:58
 */
@Service
public class CompositeDataSourceImpl implements CompositeDataSource {

    @Autowired
    private DataSourceSelection dataSourceSelection;

    @Autowired
    private ReadQueryExecutionService readQueryExecutionService;

    @Autowired
    private ReadQueryInvalidatonService invalidatorService;

    @Autowired
    private DefaultDataSourceAwareService dataSourcesAwareService;

    @Override
    public DataSourceIdentity getDataSourceIdentity() {
        return new DataSourceIdentity( CompositeDataSourceImpl.class,"CompositeDataSource" );
    }

    @Override
    public ResultSet execute( DataSource defaultDataSource, ReadQuery query ) throws DataRetrievalException {
        dataSourcesAwareService.setDefaultDataSource( query, defaultDataSource );
        return execute( query );
    }

    @Override
    public ResultSet execute( ReadQuery query ) throws DataRetrievalException {
        return doExecute( query ).getResultSet();
    }


    protected  ReadQueryExecutionResult execute(DataSourceIdentity dataSourceIdentity, ReadQuery query)  throws DataRetrievalException{
        DataSource dataSource = null;
        ReadQueryExecutionResult readQueryExecutionResult = null;
        if (dataSourceIdentity == null || dataSourceIdentity.getDefaultDataSource()){
            //Get default data source
            dataSource =  dataSourcesAwareService.getDefaultDataSource( query );
            //Execute using default data source
            readQueryExecutionResult =  readQueryExecutionService.execute( dataSource, query );
        } else {
            dataSource = dataSourcesAwareService.getDataSource( dataSourceIdentity );
            try {
                //Get using selected data source identity by the selector
                readQueryExecutionResult = readQueryExecutionService.execute( dataSource,query );
            }
            catch ( DataRetrievalException dre ) {
                LoggableServiceHolder.get().log( dre );
                //If something went wrong while execution using the selecting data source - try default
                readQueryExecutionResult =  readQueryExecutionService.execute( dataSource, query );
            }
        }
        return readQueryExecutionResult;
    }


    protected ReadQueryExecutionResult doExecute( ReadQuery query ) throws DataRetrievalException {
        //Select the data source for query using dataSourceSelection
        DataSourceIdentity dataSourceIdentity = dataSourceSelection.selectDataSource( query );
        ReadQueryExecutionResult readQueryExecutionResult =  execute(dataSourceIdentity,query);
        //Propagate data if shardeable
        distribute( readQueryExecutionResult );
        //Log retrieval
        LoggableServiceHolder.get().log( readQueryExecutionResult );
        //Feedback retrieval to data source selector for learning
        dataSourceSelection.submitExecution( readQueryExecutionResult );
        return readQueryExecutionResult;
    }

    @Override
    public void distribute( ReadQueryExecutionResult data ) {
        dataSourceSelection.submitExecution( data );

        Collection<DataSourceIdentity> newIdentities =
             dataSourceSelection.getNewSupportingDataSources(
                  dataSourcesAwareService.getDataSourceIdentities(),
                  data
             );
        if (!newIdentities.isEmpty()){
            //Clone the result set
            ReadQueryExecutionResult readQueryExecutionResult = new ReadQueryExecutionResultBuilder( data )
             .withResultSet( RowSet.clone( data.getResultSet() ) )
             .build();
            distribute( newIdentities,readQueryExecutionResult );
        }

    }


    protected void distribute(  Collection<DataSourceIdentity> selectedNodes,  ReadQueryExecutionResult data ) {
        ReadQuery query = data.getReadQuery();
        //Clone data for dataSources
        for ( DataSourceIdentity identity : selectedNodes ) {
            DataSource dataSource = dataSourcesAwareService.getDataSource( identity );
            //Distribute data for node
            dataSource.distribute( data );
            //Schedule the selection using the node
            dataSourceSelection.scheduleSelection( query, identity );
        }
        //Inform invalidator service about new supported query
        invalidatorService.putCachedQuery( query );
    }


    @Override
    public void delete( Query query ) {
        //Get read queries that this query invalidates
        Collection<ReadQuery> queriesToInvalidate = query.invalidates( invalidatorService );
        //Remove option of selection
        for ( ReadQuery selectQuery : queriesToInvalidate ) {
            dataSourceSelection.removeSelectionOptions( selectQuery );
        }
    }

}
