package pl.piotrsukiennik.tuner.wrapper;

import pl.piotrsukiennik.tuner.PreparedStatementBuilder;

import java.sql.*;

/**
 * @author Piotr Sukiennik
 * @date 20.02.14
 */
public class JsqlTunerConnection extends ConnectionWrapper {

    private PreparedStatementBuilder statementBuilder;


    public JsqlTunerConnection( Connection connection, PreparedStatementBuilder statementBuilder ) {
        super( connection );
        this.statementBuilder = statementBuilder;
    }

    //STATEMENTS

    @Override
    public Statement createStatement( int resultSetType, int resultSetConcurrency ) throws SQLException {
        return statementBuilder.build( super.createStatement( resultSetType, resultSetConcurrency ) );
    }


    @Override
    public Statement createStatement( int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws SQLException {
        return statementBuilder.build( super.createStatement( resultSetType, resultSetConcurrency, resultSetHoldability ) );
    }
    //!STATEMENTS

    //PREPARED STATEMENTS
    @Override
    public PreparedStatement prepareStatement( String sql ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql ), sql );
    }

    @Override
    public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql, resultSetType, resultSetConcurrency ), sql );
    }

    @Override
    public PreparedStatement prepareStatement( String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql, resultSetType, resultSetConcurrency, resultSetHoldability ), sql );
    }

    @Override
    public PreparedStatement prepareStatement( String sql, int autoGeneratedKeys ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql, autoGeneratedKeys ), sql );
    }

    @Override
    public PreparedStatement prepareStatement( String sql, int[] columnIndexes ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql, columnIndexes ), sql );
    }

    @Override
    public PreparedStatement prepareStatement( String sql, String[] columnNames ) throws SQLException {
        return statementBuilder.build( super.prepareStatement( sql, columnNames ), sql );
    }
    //!PREPARED STATEMENTS

    //CALLABLE STATEMENTS
    @Override
    public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability ) throws SQLException {
        return super.prepareCall( sql, resultSetType, resultSetConcurrency, resultSetHoldability );
    }

    @Override
    public CallableStatement prepareCall( String sql ) throws SQLException {
        return super.prepareCall( sql );
    }

    @Override
    public CallableStatement prepareCall( String sql, int resultSetType, int resultSetConcurrency ) throws SQLException {
        return super.prepareCall( sql, resultSetType, resultSetConcurrency );
    }
    //!CALLABLE STATEMENTS
}
