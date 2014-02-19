package pl.piotrsukiennik.tuner.model.query.expression.projection;

import pl.piotrsukiennik.tuner.model.query.expression.Expression;
import pl.piotrsukiennik.tuner.model.query.other.SortableByPosition;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Author: Piotr Sukiennik
 * Date: 26.07.13
 * Time: 20:58
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Projection extends Expression implements SortableByPosition {
    private int position;

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public void setPosition( int position ) {
        this.position = position;
    }
}