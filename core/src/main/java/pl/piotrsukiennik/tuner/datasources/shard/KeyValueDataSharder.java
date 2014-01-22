

package pl.piotrsukiennik.tuner.datasources.shard;

import pl.piotrsukiennik.tuner.IKeyValueService;
import pl.piotrsukiennik.tuner.datasources.AbstractDataSource;
import pl.piotrsukiennik.tuner.datasources.IdentifierMetaData;
import pl.piotrsukiennik.tuner.model.query.Query;
import pl.piotrsukiennik.tuner.model.query.ReadQuery;

import javax.sql.rowset.CachedRowSet;

/**
 * Author: Piotr Sukiennik
 * Date: 31.08.13
 * Time: 19:17
 */
public class KeyValueDataSharder extends AbstractDataSource {
    private IKeyValueService keyValueService;

    public KeyValueDataSharder( IKeyValueService keyValueService ) {
        super( new IdentifierMetaData( keyValueService.getIdentifier() ) );
        this.keyValueService = keyValueService;
    }

    @Override
    public CachedRowSet getData( ReadQuery query ) {
        String key = getKey( query.getHash() );
        CachedRowSet cachedRowSet = (CachedRowSet) keyValueService.get( key );
        return cachedRowSet;
    }

    @Override
    public void putData( ReadQuery query, CachedRowSet data ) {
        String key = getKey( query.getHash() );
        keyValueService.put( key, data );
    }

    @Override
    public void deleteData( Query query ) {
        String key = getKey( query.getHash() );
        keyValueService.delete( key );
    }


    protected String getKey( String suffix ) {
        return suffix;
    }
}
