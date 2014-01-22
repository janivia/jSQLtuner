package pl.piotrsukiennik.tuner.service;

import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.model.query.SelectQuery;

import java.util.Collection;

/**
 * @author Piotr Sukiennik
 * @date 14.01.14
 */
public interface CacheManagerService {
    String STAR_PROJECTION_COLUMN_NAME = "*";

    void putCachedQuery( SelectQuery query );

    Collection<SelectQuery> getQueriesToInvalidate( Query query );
}