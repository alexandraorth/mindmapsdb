package io.mindmaps.trace;


import io.mindmaps.concept.Concept;

import java.util.Map;

public class TraceMessage {

    private long timestamp;
    private String message;

    private Map<String, Concept> nodeMap;

    public TraceMessage(Concept message){


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

    @Override
    public String toString() {
        return "TraceMessage{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", nodeMap=" + nodeMap +
                '}';
    }
}
