package io.mindmaps.graql.internal.query.aggregate;

import io.mindmaps.core.model.Concept;
import io.mindmaps.graql.internal.query.aggregate.AbstractAggregate;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class IdAggregate extends AbstractAggregate<Map<String, Concept>, String> {

    private final String varName;

    public IdAggregate(String varName){
        this.varName = varName;
    }

    @Override
    public String apply(Stream<? extends Map<String, Concept>> stream){
        Supplier<Stream<? extends Map<String, Concept>>> streamSupplier = () -> { return stream; };

        // we need to guarantee there is only one object in this stream
//        assert streamSupplier.get().count() == 1;

        Concept concept = stream.map(result -> result.get(varName)).findFirst().get();
        return concept.getId();
    }

    @Override
    public String toString(){
        return "getid";
    }
}
