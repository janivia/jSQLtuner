package pl.piotrsukiennik.tuner.model.query.impl;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pl.piotrsukiennik.tuner.cache.QueryInvalidatonVisitor;
import pl.piotrsukiennik.tuner.model.expression.Expression;
import pl.piotrsukiennik.tuner.model.expression.OperatorExpression;
import pl.piotrsukiennik.tuner.model.other.GroupByFragment;
import pl.piotrsukiennik.tuner.model.other.JoinFragment;
import pl.piotrsukiennik.tuner.model.other.OrderByFragment;
import pl.piotrsukiennik.tuner.model.query.ConditionQuery;
import pl.piotrsukiennik.tuner.model.query.JoinsAwareQuery;
import pl.piotrsukiennik.tuner.model.query.ProjectionsAwareQuery;
import pl.piotrsukiennik.tuner.model.query.ReadQuery;
import pl.piotrsukiennik.tuner.model.schema.Table;
import pl.piotrsukiennik.tuner.model.source.Source;

import javax.persistence.*;
import java.util.Set;

/**
 * Author: Piotr Sukiennik
 * Date: 26.07.13
 * Time: 20:54
 */
@Entity
public class SelectQuery extends ReadQuery implements ConditionQuery, ProjectionsAwareQuery, JoinsAwareQuery {


    private Set<Expression> projections;

    private Set<Source> sources;

    private Set<JoinFragment> joins;

    private Set<GroupByFragment> groups;

    private Set<OrderByFragment> orders;

    private pl.piotrsukiennik.tuner.model.query.Query parentQuery;

    private boolean distinctFragment;

    private OperatorExpression whereExpression;

    private OperatorExpression havingExpression;

    private Table intoTable;

    private long limitFrom;

    private long limitTo;


    public long getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom( long limitFrom ) {
        this.limitFrom = limitFrom;
    }

    public long getLimitTo() {
        return limitTo;
    }

    public void setLimitTo( long limitTo ) {
        this.limitTo = limitTo;
    }

    public boolean getDistinctFragment() {
        return distinctFragment;
    }

    public void setDistinctFragment( boolean distinctFragment ) {
        this.distinctFragment = distinctFragment;
    }


    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Expression> getProjections() {
        return projections;
    }

    public void setProjections( Set<Expression> projections ) {
        this.projections = projections;
    }

    @JoinTable(name = "SelectQuery_Joins")
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<Source> getSources() {
        return sources;
    }

    public void setSources( Set<Source> sources ) {
        this.sources = sources;
    }

    @JoinTable(name = "SelectQuery_Sources")
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<JoinFragment> getJoins() {
        return joins;
    }

    public void setJoins( Set<JoinFragment> joins ) {
        this.joins = joins;
    }

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @OrderBy(value = "position ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<GroupByFragment> getGroups() {
        return groups;
    }

    public void setGroups( Set<GroupByFragment> groups ) {
        this.groups = groups;
    }

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @OrderBy(value = "position ASC")
    @LazyCollection(LazyCollectionOption.FALSE)
    public Set<OrderByFragment> getOrders() {
        return orders;
    }

    public void setOrders( Set<OrderByFragment> orders ) {
        this.orders = orders;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = true)
    public pl.piotrsukiennik.tuner.model.query.Query getParentQuery() {
        return parentQuery;
    }

    public void setParentQuery( pl.piotrsukiennik.tuner.model.query.Query parentQuery ) {
        this.parentQuery = parentQuery;
    }


    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    public Table getIntoTable() {
        return intoTable;
    }

    public void setIntoTable( Table intoTable ) {
        this.intoTable = intoTable;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    public OperatorExpression getWhereExpression() {
        return whereExpression;
    }

    public void setWhereExpression( OperatorExpression whereExpression ) {
        this.whereExpression = whereExpression;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    public OperatorExpression getHavingExpression() {
        return havingExpression;
    }

    public void setHavingExpression( OperatorExpression havingExpression ) {
        this.havingExpression = havingExpression;
    }

    @Override
    public <R> R accept( QueryInvalidatonVisitor<R> invalidator ) {
        return invalidator.visit( this );
    }

}
