package pl.piotrsukiennik.tuner.statement.advisor;

import org.springframework.aop.Advisor;
import pl.piotrsukiennik.tuner.model.query.SelectQuery;

/**
 * Author: Piotr Sukiennik
 * Date: 28.07.13
 * Time: 16:16
 */
public class ReadQueryAdvisorBuilder<Q extends SelectQuery> extends QueryAdvisorBuilder<Q> {


    @Override
    public Advisor createAdvisor( Q readQuery ) {
        return super.createAdvisor( new ReadQueryAdvice( sharder, readQuery ) );
    }
}
