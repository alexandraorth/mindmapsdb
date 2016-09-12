package io.mindmaps.trace;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.concept.Concept;
import io.mindmaps.concept.EntityType;
import io.mindmaps.exception.MindmapsValidationException;
import io.mindmaps.factory.MindmapsClient;
import io.mindmaps.graql.Graql;
import io.mindmaps.graql.QueryBuilder;
import io.mindmaps.graql.Var;
import io.mindmaps.util.Schema;
import javafx.util.Pair;
import org.apache.avro.ipc.trace.Trace;

import java.util.*;
import java.util.stream.Collectors;

import static io.mindmaps.graql.Graql.eq;
import static io.mindmaps.graql.Graql.insert;
import static io.mindmaps.graql.Graql.var;
import static java.util.stream.Collectors.toSet;


public class MindmapsTrace {

    private static final String TRACE_GRAPH = "trace";

    private static MindmapsTrace mindmapsTrace;
    private MindmapsGraph traceGraph;
    private QueryBuilder graql;

    private MindmapsTrace(){
        traceGraph = MindmapsClient.getGraph(TRACE_GRAPH);
        graql = Graql.withGraph(traceGraph);

        boolean ontologyExists = traceGraph.getMetaType().instances().size() > Schema.MetaType.values().length;
        if(!ontologyExists){
            TraceOntology.initialize(traceGraph);
        }
    }

    public static MindmapsTrace instance(){
        if(mindmapsTrace == null){
            mindmapsTrace = new MindmapsTrace();
        }
        return mindmapsTrace;
    }

    public void log(Collection<Pair<String, Concept>> concepts){
        log(null, concepts);
    }

    public void log(String messageContents, Collection<Pair<String, Concept>> concepts){
        Collection<Var> vars = new HashSet<>();

        vars.add(createMessage(messageContents));

        for(Pair<String, Concept> concept:concepts){
            Var node = createNode(concept.getKey(), concept.getValue());
            vars.add(createRelationToMessage(node));
            vars.add(node);
        }

        try {
            graql.insert(vars).execute();
            traceGraph.commit();
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }
    }

    public Collection<TraceMessage> retrieve(Concept... concepts){
        Collection<Var> patterns = new HashSet<>();
        patterns.add(var("message").isa(TraceOntology.Type.MESSAGE.getName()));

        for(Concept retrieve:concepts){
            patterns.add(
                    var()
                            .rel(TraceOntology.Type.MESSAGE_ROLE.getName(), var("message"))
                            .rel(TraceOntology.Type.NODE_ROLE.getName(),
                                    var().has(TraceOntology.Type.NODE_ID.getName(), eq(retrieve.getId())))
            );
        }

        return graql.match(patterns).stream().map(m -> new TraceMessage(m.get("message"))).collect(toSet());
    }

    private Var createMessage(String messageContents){
        Var message = var("message").isa(TraceOntology.Type.MESSAGE.getName());
        message.has(TraceOntology.Type.MESSAGE_TIMESTAMP.getName(), System.currentTimeMillis());

        if (messageContents != null){
            message.has(TraceOntology.Type.MESSAGE_CONTENTS.getName(), messageContents);
        }

        return message;
    }

    private Var createNode(String name, Concept concept){
        Var node = var().isa(TraceOntology.Type.NODE.getName());
        node.has(TraceOntology.Type.NODE_NAME.getName(), name);
        node.has(TraceOntology.Type.NODE_ID.getName(), concept.getId());
        node.has(TraceOntology.Type.NODE_TYPE.getName(), concept.type().getId());

        return node;
    }

    private Var createRelationToMessage(Var node){
        return var().isa(TraceOntology.Type.MESSAGE_NODE_REL.getName())
                .rel(TraceOntology.Type.NODE_ROLE.getName(), node)
                .rel(TraceOntology.Type.MESSAGE_ROLE.getName(), var("message"));
    }

    /**
     * Attach a resource type of the given name to the node type
     * @param resourceName name of resource to attach to the node type
     */
    private void registerNodeResource(String resourceName, String datatype){
        TraceOntology.registerNodeResource(traceGraph, resourceName, datatype);
    }
}
