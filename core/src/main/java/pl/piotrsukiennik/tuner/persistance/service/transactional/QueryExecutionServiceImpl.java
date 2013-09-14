package pl.piotrsukiennik.tuner.persistance.service.transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piotrsukiennik.tuner.persistance.model.query.Query;
import pl.piotrsukiennik.tuner.persistance.model.query.ReadQuery;
import pl.piotrsukiennik.tuner.persistance.model.query.SelectQuery;
import pl.piotrsukiennik.tuner.persistance.model.query.execution.DataSource;
import pl.piotrsukiennik.tuner.persistance.model.query.execution.QueryForDataSource;
import pl.piotrsukiennik.tuner.persistance.service.AbstractService;
import pl.piotrsukiennik.tuner.persistance.service.IQueryExecutionService;
import pl.piotrsukiennik.tuner.persistance.service.IQueryService;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Author: Piotr Sukiennik
 * Date: 31.08.13
 * Time: 19:59
 */
@Service(value = "QueryExecutionService")
@Transactional(readOnly = false)
public class QueryExecutionServiceImpl extends AbstractService implements IQueryExecutionService {


    private @Resource IQueryService queryService;

    private class PredicateQueryDataSource implements  Predicate<QueryForDataSource>{


        private DataSource dataSource;

        private PredicateQueryDataSource(DataSource dataSource) {

            this.dataSource = dataSource;
        }

        @Override
        public boolean apply(QueryForDataSource queryForDataSource) {
            return  queryForDataSource.getDataSource().equals(dataSource);
        }
    }


    private Multimap<String,QueryForDataSource> sourceMultiValueMap = HashMultimap.create();

    @Override
    public DataSource getDataSourceForQuery(ReadQuery selectQuery) {
        float random= (float)Math.random();
        DataSource dataSource =  (DataSource) s().getNamedQuery(QueryForDataSource.GET_DATASOURCE_FOR_QUERY)
                .setString("queryHash", selectQuery.getHash())
                .setFloat("random", random).setMaxResults(1).uniqueResult();
        return dataSource;
    }
    @Override
    public DataSource getDataSource(Class clazz, String identifier) {
        Session session = s();
        String clazzStr = clazz.getName();
        DataSource dataSource =  (DataSource) session.createCriteria(DataSource.class)
                .add(Restrictions.eq("clazz",clazzStr))
                .add(Restrictions.eq("identifier",identifier)).setMaxResults(1).uniqueResult();
        if (dataSource==null){
            dataSource=new DataSource();
            dataSource.setIdentifier(identifier);
            dataSource.setClazz(clazzStr);
            session.save(dataSource);
            session.flush();
            session.refresh(dataSource);
        }
        return dataSource;
    }

    public void refreshRoulette(Query query){
       s().getNamedQuery(QueryForDataSource.UPDATE_QUERY_EXECUTION_SETTINGS).setLong("queryId", query.getId()).executeUpdate();
    }

    public void removeDataSourceForQuery(final ReadQuery query,final  DataSource dataSource){
      Query queryPersisted = queryService.submit(query);
      s().getNamedQuery(QueryForDataSource.REMOVE_DATASOURCE_FOR_QUERY).setEntity("query",queryPersisted).setEntity("dataSource", dataSource).executeUpdate();
        refreshRoulette(query);
    }
    public void removeDataSourcesForQueries(Collection<? extends  ReadQuery> queries, final Collection<DataSource> dataSources){
        if (dataSources==null || dataSources.isEmpty() || queries==null || queries.isEmpty()){
            return;
        }
        Collection<Query> queriesPersisted = new LinkedHashSet<Query>();
        for (ReadQuery readQuery:queries){
            queriesPersisted.add(queryService.submit(readQuery));
        }
        s().getNamedQuery(QueryForDataSource.REMOVE_DATASOURCES_FOR_QUERIES).setParameterList("queries",queriesPersisted).setParameterList("dataSources",dataSources).executeUpdate();
        for (Query query:queriesPersisted){
            refreshRoulette(query);
        }
    }
    public void removeDataSourcesForQuery(final ReadQuery query,final  Collection<DataSource> dataSources){
        if (dataSources==null || dataSources.isEmpty()){
            return;
        }
        Query queryPersisted = queryService.submit(query);
        s().getNamedQuery(QueryForDataSource.REMOVE_DATASOURCES_FOR_QUERY).setEntity("query",queryPersisted).setParameterList("dataSource",dataSources).executeUpdate();
        refreshRoulette(query);
    }
    public QueryForDataSource getQueryForDataSource(final ReadQuery q,final  DataSource dataSource){
        return (QueryForDataSource) s().createCriteria(QueryForDataSource.class)
                .add(Restrictions.eq("query", q)).add(Restrictions.eq("dataSource", dataSource)).uniqueResult();
    }

    protected QueryForDataSource initNew(QueryForDataSource queryForDataSource, ReadQuery q, DataSource dataSource){
        queryForDataSource.setExecutions(0);
        queryForDataSource.setAverageExecutionTimeNano(-1);
        queryForDataSource.setDataSource(dataSource);
        queryForDataSource.setQuery(q);
        return queryForDataSource;

    }
    public  QueryForDataSource submitNewDataSourceForQuery(final ReadQuery q,final  DataSource dataSource){
        Session session = s();
        ReadQuery persistedQuery = queryService.submit(q);
        QueryForDataSource queryForDataSource = getQueryForDataSource(persistedQuery,dataSource);
        if (queryForDataSource==null){
            queryForDataSource = new QueryForDataSource();
            queryForDataSource = initNew(queryForDataSource,persistedQuery,dataSource);
            session.save(queryForDataSource);
        }  else {
            queryForDataSource = initNew(queryForDataSource,persistedQuery,dataSource);
            session.merge(queryForDataSource);
        }
        session.flush();
        refreshRoulette(persistedQuery);
        sourceMultiValueMap.put(persistedQuery.getHash(),queryForDataSource);
        return queryForDataSource;
    }
    public  Collection<QueryForDataSource> submit(final ReadQuery q,final  DataSource dataSource, long executionTimeNano){
        ReadQuery persistedQuery = queryService.submit(q);
        Collection<QueryForDataSource> queryForDataSourceCollection =  sourceMultiValueMap.get(persistedQuery.getHash());
        Collection<QueryForDataSource> filtered = Collections2.filter(queryForDataSourceCollection,new PredicateQueryDataSource(dataSource));
        Session session = s();

        if (filtered.isEmpty()){
            QueryForDataSource queryForDataSource = new QueryForDataSource();
            queryForDataSource.setExecutions(1);
            queryForDataSource.setAverageExecutionTimeNano(executionTimeNano);
            queryForDataSource.setDataSource(dataSource);
            queryForDataSource.setQuery(persistedQuery);
            session.save(queryForDataSource);
            session.flush();
            refreshRoulette(persistedQuery);
            filtered.add(queryForDataSource);
            sourceMultiValueMap.put(persistedQuery.getHash(),queryForDataSource);
        } else {
            for (QueryForDataSource queryForDataSource : filtered) {
                long executions = queryForDataSource.getExecutions();
                float averageExecution = queryForDataSource.getAverageExecutionTimeNano();
                averageExecution = (averageExecution* executions + executionTimeNano)/(executions+1);
                queryForDataSource.setExecutions(executions+1);
                queryForDataSource.setAverageExecutionTimeNano(averageExecution);
                session.merge(queryForDataSource);
                session.flush();
                refreshRoulette(persistedQuery);
            }
        }



        return filtered;
    }



}
