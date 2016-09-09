package io.mindmaps.etl;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.core.model.Concept;
import io.mindmaps.graql.*;

import java.util.Map;
import java.util.stream.Stream;

public class GraqlExecutor {

    public static void insert( Object graqlQuery){

        if (!(graqlQuery instanceof InsertQuery)){
            throw new RuntimeException("");
        }

        ((InsertQuery) graqlQuery).execute();
    }

    public static Stream<Map<String, Concept>> match( Object graqlQuery){
        if(!(graqlQuery instanceof MatchQuery)){
            throw new RuntimeException("");
        }

        return ((MatchQuery) graqlQuery).stream();
    }

    public static Object aggregate(Object graqlQuery){
        if(!(graqlQuery instanceof AggregateQuery)){
           throw new RuntimeException("");
        }

        return ((AggregateQuery) graqlQuery).execute();
    }

}
