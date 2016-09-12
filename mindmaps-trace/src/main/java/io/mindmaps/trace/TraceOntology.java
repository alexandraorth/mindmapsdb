package io.mindmaps.trace;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.concept.EntityType;
import io.mindmaps.concept.ResourceType;
import io.mindmaps.concept.RoleType;
import io.mindmaps.exception.MindmapsValidationException;
import io.mindmaps.graql.internal.util.GraqlType;

public class TraceOntology {

    public enum Type {

        MESSAGE("message"),
        MESSAGE_TIMESTAMP("timestamp"),
        MESSAGE_CONTENTS("contents"),
        NODE("node"),
        NODE_NAME("name"),
        NODE_TYPE("node-type"),
        NODE_ID("node-id"),
        MESSAGE_NODE_REL("logged-node"),
        NODE_ROLE("node-of-message"),
        MESSAGE_ROLE("message-of-node");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Attach a resource type of the given name to the node type
     * @param resourceName name of resource to attach to the node type
     */
    public static void registerNodeResource(MindmapsGraph traceGraph, String resourceName, String datatype){
        insertResourceRelation(traceGraph, Type.NODE.getName(), resourceName, ResourceType.DataType.SUPPORTED_TYPES.get(datatype));
        try {
            traceGraph.commit();
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert the default trace ontology, as described in the README
     * to the graph.
     */
    public static void initialize(MindmapsGraph traceGraph){

        // message, it's resources and relations to those resources
        EntityType message = traceGraph.putEntityType(TraceOntology.Type.MESSAGE.getName());
        insertResourceRelation(traceGraph, Type.MESSAGE.getName(), Type.MESSAGE_CONTENTS.getName(), ResourceType.DataType.STRING);
        insertResourceRelation(traceGraph, Type.MESSAGE.getName(), Type.MESSAGE_TIMESTAMP.getName(), ResourceType.DataType.LONG);

        // entity, it's default resources and relations to those resources
        EntityType node = traceGraph.putEntityType(TraceOntology.Type.NODE.getName());
        insertResourceRelation(traceGraph, Type.NODE.getName(), Type.NODE_TYPE.getName(), ResourceType.DataType.STRING);
        insertResourceRelation(traceGraph, Type.NODE.getName(), Type.NODE_ID.getName(), ResourceType.DataType.STRING);
        insertResourceRelation(traceGraph, Type.NODE.getName(), Type.NODE_NAME.getName(), ResourceType.DataType.STRING);

        RoleType nodeRole = traceGraph.putRoleType(Type.NODE_ROLE.getName());
        RoleType messageRole = traceGraph.putRoleType(Type.MESSAGE_ROLE.getName());
        traceGraph.putRelationType(Type.MESSAGE_NODE_REL.getName()).hasRole(nodeRole).hasRole(messageRole);
        message.playsRole(messageRole);
        node.playsRole(nodeRole);

        try {
            traceGraph.commit();
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the default relation between an entityType and a resourceType
     * @param entityType
     * @param resourceType
     */
    public static void insertResourceRelation(MindmapsGraph traceGraph, String entityType, String resourceType, ResourceType.DataType dataType){
        traceGraph.putResourceType(resourceType, dataType);

        RoleType owner = traceGraph.putRoleType(GraqlType.HAS_RESOURCE_OWNER.getId(resourceType));
        RoleType value = traceGraph.putRoleType(GraqlType.HAS_RESOURCE_VALUE.getId(resourceType));
        traceGraph.putRelationType(GraqlType.HAS_RESOURCE.getId(resourceType))
                .hasRole(owner).hasRole(value);

        traceGraph.getEntityType(entityType).playsRole(owner);
        traceGraph.getResourceType(resourceType).playsRole(value);
    }

}
