package io.mindmaps.trace;


import io.mindmaps.concept.Concept;

import java.util.Map;

public class TraceMessage {

    private long timestamp;
    private String message;

    private Map<String, Concept> nodeMap;

    public TraceMessage(Map<String, Concept> nodeMap){
        this(null, nodeMap);
    }

    public TraceMessage(String message, Map<String, Concept> nodeMap){
        this.message = message;
        this.nodeMap = nodeMap;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Concept> getNodeMap() {
        return nodeMap;
    }
}
