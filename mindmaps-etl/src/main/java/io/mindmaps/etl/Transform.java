package io.mindmaps.etl;

import io.mindmaps.core.MindmapsGraph;
import io.mindmaps.core.model.Concept;
import io.mindmaps.core.model.Rule;
import io.mindmaps.graql.InsertQuery;
import io.mindmaps.graql.MatchQuery;
import io.mindmaps.graql.QueryParser;

import java.util.Map;
import java.util.stream.Stream;

public class Transform {

    public static void main(String[] args){

    }

    public void transform(MindmapsGraph left, MindmapsGraph right, Rule rule){

        MatchQuery lhs = QueryParser.create(left.getTransaction()).parseMatchQuery(rule.getLHS()).getMatchQuery();
        InsertQuery rhs = QueryParser.create(right.getTransaction()).parseInsertQuery(rule.getRHS());

        Stream<Map<String, Concept>> results = lhs.stream();

    }
}
