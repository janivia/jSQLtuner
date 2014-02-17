package pl.piotrsukiennik.tuner.service.parser.statement;

import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.service.QueryContext;
import pl.piotrsukiennik.tuner.service.parser.ElementParserService;


/**
 * Author: Piotr Sukiennik
 * Date: 26.07.13
 * Time: 22:53
 */
public abstract class ParsingVisitor<T extends Query> extends Visitor {
    protected T query;

    protected ParsingVisitor( ElementParserService elementParserService, QueryContext queryContext ) {
        super( elementParserService, queryContext );
    }

    public ParsingVisitor( ElementParserService elementParserService, QueryContext queryContext, T query ) {
        super( elementParserService, queryContext );
        this.query = query;
    }


    public QueryContext getQueryContext() {
        return queryContext;
    }

    public T getQuery() {
        return query;
    }

    protected void setQuery( T query ) {
        this.query = query;
    }


    protected <T extends Object> void init( T o ) {
        if ( query != null ) {
            query.setValue( o.toString() );
        }
    }

}
