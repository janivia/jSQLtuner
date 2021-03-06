package pl.piotrsukiennik.tuner.service;

import pl.piotrsukiennik.tuner.exception.QueryParsingNotSupportedException;
import pl.piotrsukiennik.tuner.model.query.Query;

/**
 * Author: Piotr Sukiennik
 * Date: 30.06.13
 * Time: 17:44
 */
public interface QueryProviderService {
    <T extends Query> T provide( String databaseStr, String schemaStr, String queryStr ) throws QueryParsingNotSupportedException;
}
