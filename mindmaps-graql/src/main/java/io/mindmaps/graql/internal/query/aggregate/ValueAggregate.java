package io.mindmaps.graql.internal.query.aggregate;

import io.mindmaps.core.model.Concept;
import io.mindmaps.graql.internal.query.aggregate.AbstractAggregate;

import java.util.Map;
import java.util.stream.Stream;

public class ValueAggregate extends AbstractAggregate<Map<String, Concept>, Object> {

    private final String varName;

    public ValueAggregate(String varName){
        this.varName = varName;
    }

    @Override
    public Object apply(Stream<? extends Map<String, Concept>> stream){
        Concept concept = stream.map(result -> result.get(varName)).findFirst().get();
        return concept.isResource() ? concept.asResource().getValue() : null;
    }

    @Override
    public String toString(){
        return "getvalue";
    }
}
