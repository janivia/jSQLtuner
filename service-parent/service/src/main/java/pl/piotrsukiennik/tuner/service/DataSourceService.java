package pl.piotrsukiennik.tuner.service;

import pl.piotrsukiennik.tuner.DataSource;
import pl.piotrsukiennik.tuner.model.datasource.DataSourceIdentity;
import pl.piotrsukiennik.tuner.model.query.Query;

/**
 * @author Piotr Sukiennik
 * @date 13.01.14
 */
public interface DataSourceService {
    DataSource getDataSource( Query query, DataSourceIdentity dataSourceIdentity );

    DataSource getDefault( Query query );

    void setDefault( Query query, DataSource dataSource );
}