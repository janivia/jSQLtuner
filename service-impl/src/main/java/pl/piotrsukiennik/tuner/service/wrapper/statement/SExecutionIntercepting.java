package pl.piotrsukiennik.tuner.service.wrapper.statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pl.piotrsukiennik.tuner.DataSource;
import pl.piotrsukiennik.tuner.ShardService;
import pl.piotrsukiennik.tuner.exception.QueryParsingNotSupportedException;
import pl.piotrsukiennik.tuner.model.query.ReadQuery;
import pl.piotrsukiennik.tuner.model.query.WriteQuery;
import pl.piotrsukiennik.tuner.service.ParserService;
import pl.piotrsukiennik.tuner.service.datasource.InterceptorDataSource;
import pl.piotrsukiennik.tuner.service.wrapper.WrapperUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Piotr Sukiennik
 * @date 17.02.14
 */
public class SExecutionIntercepting<T extends Statement> extends SWrapper<T> implements Statement {

    private static final Log LOG = LogFactory.getLog( SExecutionIntercepting.class );

    protected ShardService shardService;

    protected ParserService parserService;

    protected String database;

    protected String schema;


    public SExecutionIntercepting( T statement, ShardService shardService, ParserService parserService, String database, String schema ) {
        super( statement );
        this.shardService = shardService;
        this.parserService = parserService;
        this.database = database;
        this.schema = schema;
    }


  /*
    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            ReadQuery readQuery =  parserService.parse(database, schema, sql );
            DataSource rootDs = new InterceptorDataSource( this, readQuery ) {
                @Override
                protected ResultSet proceed() throws SQLException {
                    return SExecutionIntercepting.super.executeQuery( sql );
                }
            };
            return WrapperUtil.getResultSet( shardService, readQuery, rootDs );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
            return super.getResultSet();
        }
    }*/

    //READ
    @Override
    public ResultSet executeQuery( final String sql ) throws SQLException {
        try {
            ReadQuery readQuery = parserService.parse( database, schema, sql );
            DataSource rootDs = new InterceptorDataSource( this, readQuery ) {
                @Override
                protected ResultSet proceed() throws SQLException {
                    return SExecutionIntercepting.super.executeQuery( sql );
                }
            };
            return WrapperUtil.getResultSet( shardService, readQuery, rootDs );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
            return super.getResultSet();
        }
    }
    //!READ

    //WRITE
    @Override
    public int[] executeBatch() throws SQLException {
        return super.executeBatch();
    }

    @Override
    public int executeUpdate( String sql ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public boolean execute( String sql ) throws SQLException {
        boolean rowsAffected = super.execute( sql );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public int executeUpdate( String sql, int[] columnIndexes ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, columnIndexes );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public int executeUpdate( String sql, int autoGeneratedKeys ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, autoGeneratedKeys );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public int executeUpdate( String sql, String[] columnNames ) throws SQLException {
        int rowsAffected = super.executeUpdate( sql, columnNames );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public boolean execute( String sql, int autoGeneratedKeys ) throws SQLException {
        boolean rowsAffected = super.execute( sql, autoGeneratedKeys );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public boolean execute( String sql, int[] columnIndexes ) throws SQLException {
        boolean rowsAffected = super.execute( sql, columnIndexes );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }

    @Override
    public boolean execute( String sql, String[] columnNames ) throws SQLException {
        boolean rowsAffected = super.execute( sql, columnNames );
        try {
            WriteQuery query = parserService.parse( database, schema, sql );
            WrapperUtil.proceed( shardService, query, rowsAffected );
        }
        catch ( QueryParsingNotSupportedException e ) {
            WrapperUtil.log( LOG, e, sql );
        }
        return rowsAffected;
    }
    //!WRITE
}
