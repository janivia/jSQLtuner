package pl.piotrsukiennik.tuner.service;

import pl.piotrsukiennik.tuner.model.schema.Column;
import pl.piotrsukiennik.tuner.model.schema.Table;

import java.util.Collection;

/**
 * @author Piotr Sukiennik
 * @date 16.02.14
 */
public interface ReadQueryTreeService<K> {

    Collection<K> getQueriesInvalidatedBy( Table table );

    Collection<K> getQueriesInvalidatedBy( Column column );

    Collection<K> getQueriesInvalidatedByStar( Table table );

    void putQuery( K query );

}