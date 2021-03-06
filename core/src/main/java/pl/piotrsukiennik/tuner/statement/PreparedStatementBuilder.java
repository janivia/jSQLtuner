package pl.piotrsukiennik.tuner.statement;

import java.sql.PreparedStatement;

/**
 * @author Piotr Sukiennik
 * @date 16.02.14
 */
public interface PreparedStatementBuilder extends StatementBuilder {
    PreparedStatement build( PreparedStatement preparedStatement, String sql );
}
