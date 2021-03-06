package pl.piotrsukiennik.tuner.statement.impl;

import pl.piotrsukiennik.tuner.exception.DataRetrievalException;
import pl.piotrsukiennik.tuner.exception.QueryParsingNotSupportedException;
import pl.piotrsukiennik.tuner.model.DataSource;
import pl.piotrsukiennik.tuner.model.InterceptorDataSource;
import pl.piotrsukiennik.tuner.model.log.WriteQueryExecution;
import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.model.query.ReadQuery;
import pl.piotrsukiennik.tuner.model.query.WriteQuery;
import pl.piotrsukiennik.tuner.service.DataSourceManager;
import pl.piotrsukiennik.tuner.service.LoggableServiceHolder;
import pl.piotrsukiennik.tuner.service.QueryProviderService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Piotr Sukiennik
 * @date 15.02.14
 */
public class PSExecutionIntercepting<T extends PSParametersIntercepting<?>> extends PSWrapper<T> {

    protected DataSourceManager dataSourceManager;

    protected QueryProviderService queryService;

    protected String database;

    protected String schema;

    protected String sql;

    public PSExecutionIntercepting( T preparedStatement, DataSourceManager dataSourceManager, QueryProviderService queryService, String database, String schema, String sql ) {
        super( preparedStatement );
        this.dataSourceManager = dataSourceManager;
        this.queryService = queryService;
        this.database = database;
        this.schema = schema;
        this.sql = sql;
    }


    protected void proceed( WriteQuery query, boolean rowsAffected ) {
        proceed( query, rowsAffected ? 1 : 0 );
    }

    protected void proceed( WriteQuery query, int rowsAffected ) {
        WriteQueryExecution writeQueryExecution = new WriteQueryExecution();
        writeQueryExecution.setRowsAffected( rowsAffected );
        writeQueryExecution.setTimestamp( new Timestamp( System.currentTimeMillis() ) );
        writeQueryExecution.setQuery( query );
        if ( rowsAffected > 0 ) {
            dataSourceManager.delete( query );
        }
    }

    protected ResultSet getResultSet( ReadQuery readQuery, DataSource rootDataSource ) throws SQLException {
        try {
            return  dataSourceManager.execute(  rootDataSource, readQuery );
        }
        catch ( DataRetrievalException e ) {
            throw new SQLException( e );
        }
    }

    protected <T extends Query> T getQuery( String sql ) throws QueryParsingNotSupportedException {
        String sqlQueryWithParams = Statements.bind( sql, preparedStatement.getParameterSet() );
        return queryService.provide( database, schema, sqlQueryWithParams );
    }

    //WRITE EXECUTIONS
    @Override
    public int[] executeBatch() throws SQLException {
        return super.executeBatch();
    }


    @Override
    public boolean execute( String sql, String[] columnNames ) throws SQLException {
        boolean rowsAffected = super.execute( sql, columnNames );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return rowsAffected;
    }


    @Override
    public int executeUpdate( String sql, int autoGeneratedKeys ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, autoGeneratedKeys );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            LoggableServiceHolder.get().log( e );
        }
        return rowsAffected;
    }

    @Override
    public int executeUpdate( String sql, int[] columnIndexes ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, columnIndexes );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return rowsAffected;
    }

    @Override
    public int executeUpdate( String sql, String[] columnNames ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, columnNames );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return rowsAffected;
    }

    @Override
    public boolean execute( String sql, int autoGeneratedKeys ) throws SQLException {
        boolean affected = super.execute( sql, autoGeneratedKeys );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;
    }

    @Override
    public boolean execute( String sql, int[] columnIndexes ) throws SQLException {
        boolean affected = super.execute( sql, columnIndexes );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;
    }

    @Override
    public int executeUpdate() throws SQLException {
        int affected = super.executeUpdate();
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;
    }

    @Override
    public boolean execute( String sql ) throws SQLException {
        boolean affected = super.execute( sql );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;
    }


    @Override
    public int executeUpdate( String sql ) throws SQLException {
        int affected = super.executeUpdate( sql );
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;
    }

    @Override
    public boolean execute() throws SQLException {
        boolean affected = super.execute();
        try {
            WriteQuery query = getQuery( sql );
            proceed( query, affected );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
        }
        return affected;

    }
    //!WRITE EXECUTIONS

    //READ EXECUTIONS
    @Override
    public ResultSet executeQuery( final String sql ) throws SQLException {
        try {
            ReadQuery readQuery = getQuery( sql );
            DataSource rootDs = new InterceptorDataSource( this, readQuery ) {
                @Override
                protected ResultSet proceed() throws SQLException {
                    return PSExecutionIntercepting.super.executeQuery( sql );
                }
            };
            return getResultSet( readQuery, rootDs );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
            return super.executeQuery( sql );
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            ReadQuery readQuery = getQuery( sql );
            DataSource rootDs = new InterceptorDataSource( this, readQuery ) {
                @Override
                protected ResultSet proceed() throws SQLException {
                    return PSExecutionIntercepting.super.executeQuery();
                }
            };
            return getResultSet( readQuery, rootDs );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
            return super.executeQuery();
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            ReadQuery readQuery = getQuery( sql );
            DataSource rootDs = new InterceptorDataSource( this, readQuery ) {
                @Override
                protected ResultSet proceed() throws SQLException {
                    return PSExecutionIntercepting.super.getResultSet();
                }
            };
            return getResultSet( readQuery, rootDs );
        }
        catch ( QueryParsingNotSupportedException e ) {
             LoggableServiceHolder.get().log( e );
            return super.getResultSet();
        }

    }
    //!READ EXECUTIONS


}
