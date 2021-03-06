package pl.piotrsukiennik.tuner.servicepersistent.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import pl.piotrsukiennik.tuner.exception.DataRetrievalException;
import pl.piotrsukiennik.tuner.exception.QueryInterceptionNotSupportedException;
import pl.piotrsukiennik.tuner.exception.QueryParsingNotSupportedException;
import pl.piotrsukiennik.tuner.model.LoggableMessage;
import pl.piotrsukiennik.tuner.model.LoggableMessageEnum;
import pl.piotrsukiennik.tuner.model.ReadQueryExecutionResult;
import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.persistence.Dao;
import pl.piotrsukiennik.tuner.persistence.LogDao;
import pl.piotrsukiennik.tuner.service.LoggableService;
import pl.piotrsukiennik.tuner.service.LoggableServiceHolder;

import java.util.concurrent.TimeUnit;

/**
 * @author Piotr Sukiennik
 * @date 19.02.14
 */
@Service("pl.piotrsukiennik.tuner.LoggableService.impl")
public class LoggableServiceHolderImpl extends LoggableServiceHolder implements LoggableService {

    private static final Log LOG = LogFactory.getLog( LoggableServiceHolder.class );

    private static final String EXCEPTION_MESSAGE_FORMAT = "Query (%s) caused exception.";

    public LogDao getLogDao() {
        return Dao.getLog();
    }

    public LoggableServiceHolderImpl() {
        LoggableServiceHolder.setLogService( this );
    }

    @Override
    public void log( ReadQueryExecutionResult readQueryExecutionResult ) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info( LoggableMessageEnum.EXECUTION.getMessage( readQueryExecutionResult ) );
        }
    }

    @Override
    public void log( Query query, LoggableMessage loggableMessage, TimeUnit timeUnit, long duration ) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info( loggableMessage.getMessage( query, timeUnit, duration ) );
        }
    }


    private void log( String query, Throwable exception ) {
        if ( LOG.isWarnEnabled() ) {
            LOG.warn( String.format( EXCEPTION_MESSAGE_FORMAT, query ), exception );
        }
    }


    @Override
    public void log( DataRetrievalException exception ) {
        log( exception.getQuery(), exception );
        getLogDao().log( exception.getQuery(), exception );
    }

    @Override
    public void log( QueryInterceptionNotSupportedException exception ) {
        log( exception.getQuery(), exception );
    }

    @Override
    public void log( QueryParsingNotSupportedException exception ) {
        log( exception.getQuery(), exception );
    }
}
