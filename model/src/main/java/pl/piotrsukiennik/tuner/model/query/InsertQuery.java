package pl.piotrsukiennik.tuner.model.query;

import pl.piotrsukiennik.tuner.model.schema.Column;

import javax.persistence.*;
import java.util.Set;

/**
 * Author: Piotr Sukiennik
 * Date: 26.07.13
 * Time: 20:55
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class InsertQuery extends WriteQuery {
    private pl.piotrsukiennik.tuner.model.schema.Table table;

    @ManyToOne(cascade = CascadeType.MERGE)
    public pl.piotrsukiennik.tuner.model.schema.Table getTable() {
        return table;
    }

    public void setTable( pl.piotrsukiennik.tuner.model.schema.Table table ) {
        this.table = table;
    }

    private Set<pl.piotrsukiennik.tuner.model.schema.Column> columns;


    @OneToMany(cascade = CascadeType.MERGE)
    public Set<Column> getColumns() {
        return columns;
    }

    public void setColumns( Set<Column> columns ) {
        this.columns = columns;
    }
}
