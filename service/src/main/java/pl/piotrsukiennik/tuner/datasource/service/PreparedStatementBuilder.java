package pl.piotrsukiennik.tuner.datasource.service;

import java.sql.PreparedStatement;

/**
 * @author Piotr Sukiennik
 * @date 16.02.14
 */
public interface PreparedStatementBuilder extends StatementBuilder {
    PreparedStatement build( PreparedStatement preparedStatement, String sql );
}
