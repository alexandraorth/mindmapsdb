package io.mindmaps.graql.internal.query.aggregate;


import io.mindmaps.concept.Concept;

import java.util.Map;
import java.util.stream.Stream;

public class IdentityAggregate extends AbstractAggregate<Map<String, Concept>, Concept>{

    private final String varName;

    public IdentityAggregate(String varName){
        this.varName = varName;
    }

    @Override
    public Concept apply(Stream<? extends Map<String, Concept>> stream){
        return stream.map(result -> result.get(varName)).findFirst().get();
    }

}
