package pl.piotrsukiennik.tuner.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.piotrsukiennik.tuner.test.service.SQLQueryExecutionService;
import pl.piotrsukiennik.tuner.test.util.Utils;

import javax.annotation.Resource;

/**
 * @author Piotr Sukiennik
 * @date 22.01.14
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/root-context.xml" } )
public class QueriesTest {


    private
    @Resource
    SQLQueryExecutionService queryExecutionService;

    @Test
    @Repeat( 10 )
    public void runTestQueries() {
        Utils.processEachLine( "queries.sql", new Utils.StringProcessor() {
            @Override
            public void process( String line ) {
                try {
                    queryExecutionService.execute( line );
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }


}
