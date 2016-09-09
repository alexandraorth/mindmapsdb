package io.mindmaps.trace;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.core.model.Concept;
import io.mindmaps.factory.MindmapsClient;

import java.util.Map;

public class MindmapsTrace {

    private static final String TRACE_GRAPH = "trace";

    private static MindmapsTrace mindmapsTrace;
    private MindmapsGraph traceGraph;

    private MindmapsTrace(){
        traceGraph = MindmapsClient.getGraph(TRACE_GRAPH);

        boolean ontologyExists = traceGraph.getMetaType().instances().size() > 8;
        if(!ontologyExists){
            TraceOntology.initializeTraceOntology(traceGraph);
        }
    }

    public static MindmapsTrace instance(){
        if(mindmapsTrace == null){
            mindmapsTrace = new MindmapsTrace();
        }
        return mindmapsTrace;
    }

    public void log(Map<String, Concept> concepts){

    }

    public void log(String message, Map<String, Concept> concepts){

    }

    /**
     * Attach a resource type of the given name to the node type
     * @param resourceName name of resource to attach to the node type
     */
    private void registerNodeResource(String resourceName, String datatype){
        TraceOntology.registerNodeResource(traceGraph, resourceName, datatype);
    }
}
