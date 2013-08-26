package pl.piotrsukiennik.tuner.parser.jsqlqueryparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import org.springframework.stereotype.Component;
import pl.piotrsukiennik.tuner.parser.IQuery;
import pl.piotrsukiennik.tuner.parser.IQueryParser;
import pl.piotrsukiennik.tuner.parser.jsqlqueryparser.statement.StatementParserVisitor;
import pl.piotrsukiennik.tuner.persistance.model.query.Query;
import pl.piotrsukiennik.tuner.persistance.model.schema.Database;
import pl.piotrsukiennik.tuner.persistance.model.schema.Schema;
import pl.piotrsukiennik.tuner.persistance.service.ILogService;
import pl.piotrsukiennik.tuner.query.QueryContext;
import pl.piotrsukiennik.tuner.query.QueryContextManager;
import pl.piotrsukiennik.tuner.util.HashGenerators;
import pl.piotrsukiennik.tuner.util.ServicesHolder;


import javax.annotation.Resource;
import java.io.StringReader;

/**
 * Author: Piotr Sukiennik
 * Date: 05.07.13
 * Time: 19:05
 */
@Component
public class JSqlQueryParser implements IQueryParser {
    private @Resource ServicesHolder services;

    @Override
    public Query parse(String databaseStr, String schemaStr, String query)  {
        try{
            CCJSqlParserManager pm = new CCJSqlParserManager();
            QueryContextManager queryContextManager = new QueryContextManager(services.getSchemaService());
            Database database = queryContextManager.getDatabase(databaseStr);
            Schema schema = queryContextManager.getSchema(schemaStr);
            StatementParserVisitor statementParserVisitor = new StatementParserVisitor(queryContextManager,pm.parse(new StringReader(query)));
            return  statementParserVisitor.getQuery();
        }catch (JSQLParserException e){
            services.getLogService().logException(query, e);
        }
        return null;
    }

}