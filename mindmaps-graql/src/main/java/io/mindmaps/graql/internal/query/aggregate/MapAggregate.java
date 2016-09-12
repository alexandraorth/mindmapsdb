package io.mindmaps.graql.internal.query.aggregate;

import com.google.common.collect.Lists;
import io.mindmaps.concept.Concept;
import io.mindmaps.graql.Aggregate;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class MapAggregate<T> extends AbstractAggregate<Map<String, Concept>, Stream<T>>{

    private final Aggregate<? super Map<String, Concept>, T> innerAggregate;

    public MapAggregate(Aggregate<? super Map<String, Concept>, T> innerAggregate) {
        this.innerAggregate = innerAggregate;
    }

    @Override
    public Stream<T> apply(Stream<? extends Map<String, Concept>> stream){
        Function<Map<String, Concept>, T> applyAggregate =
                (input) -> innerAggregate.apply(Lists.newArrayList(input).stream());

        return stream.collect(mapping(applyAggregate, toSet())).stream();
    }
}
