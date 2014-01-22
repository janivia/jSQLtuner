package pl.piotrsukiennik.tuner.model.query;

import pl.piotrsukiennik.tuner.model.query.source.Source;

import java.util.Set;

/**
 * Author: Piotr Sukiennik
 * Date: 28.07.13
 * Time: 19:30
 */
public interface SourcesAware {
    Set<Source> getSources();

    void setSources( Set<Source> sources );
}
