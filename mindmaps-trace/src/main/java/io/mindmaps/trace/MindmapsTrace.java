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

import java.util.Map;

import static io.mindmaps.graql.Graql.insert;
import static io.mindmaps.graql.Graql.var;


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

    public void log(Map<String, Concept> concepts){
        log(null, concepts);
    }

    public void log(String messageContents, Map<String, Concept> concepts){

        Var message = createMessage(messageContents);
        graql.insert(message).execute();

        for(String name:concepts.keySet()){
            Concept concept = concepts.get(name);

            Var node = createNode(name, concept);
            Var rel = createRelation(message, node);
            graql.insert(node, rel).execute();
        }

        try {
            traceGraph.commit();

            System.out.println(traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances());
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }
    }

    public Var createMessage(String messageContents){
        Var message = var().isa(TraceOntology.Type.MESSAGE.getName());
        message.has(TraceOntology.Type.MESSAGE_TIMESTAMP.getName(), System.currentTimeMillis());

        if (messageContents != null){
            message.has(TraceOntology.Type.MESSAGE_CONTENTS.getName(), messageContents);
        }

        return message;
    }

    public Var createNode(String name, Concept concept){
        Var node = var().isa(TraceOntology.Type.NODE.getName());
        node.has(TraceOntology.Type.NODE_ID.getName(), name);
        node.has(TraceOntology.Type.NODE_ID.getName(), concept.getId());
        node.has(TraceOntology.Type.NODE_TYPE.getName(), concept.type().getId());

        return node;
    }

    public Var createRelation(Var message, Var node){
        return var().isa(TraceOntology.Type.MESSAGE_NODE_REL.getName())
                .rel(TraceOntology.Type.NODE_ROLE.getName(), node)
                .rel(TraceOntology.Type.MESSAGE_ROLE.getName(), message);
    }

    /**
     * Attach a resource type of the given name to the node type
     * @param resourceName name of resource to attach to the node type
     */
    private void registerNodeResource(String resourceName, String datatype){
        TraceOntology.registerNodeResource(traceGraph, resourceName, datatype);
    }
}
