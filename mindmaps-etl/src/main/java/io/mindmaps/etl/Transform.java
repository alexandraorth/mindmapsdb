package io.mindmaps.etl;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.core.model.Concept;
import io.mindmaps.core.model.Rule;
import io.mindmaps.graql.QueryParser;

import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class Transform {

//    MindmapsTrace trace = MindmapsTrace.instance();

    public static void main(String[] args){



    }

    public void transform(MindmapsGraph left, MindmapsGraph right, Rule rule){

        Object lhs = QueryParser.create(left).parseQuery(rule.getLHS());
        Object rhs = QueryParser.create(right).parseQuery(rule.getRHS());

//        trace.

        for(Map<String, Concept> result:GraqlExecutor.match(lhs).collect(toSet())){



        }
    }
}
