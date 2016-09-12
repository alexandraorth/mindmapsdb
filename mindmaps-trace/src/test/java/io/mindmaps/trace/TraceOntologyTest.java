package io.mindmaps.trace;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.concept.Entity;
import io.mindmaps.concept.EntityType;
import io.mindmaps.concept.Instance;
import io.mindmaps.engine.MindmapsEngineServer;
import io.mindmaps.engine.util.ConfigProperties;
import io.mindmaps.exception.MindmapsValidationException;
import io.mindmaps.factory.MindmapsClient;
import io.mindmaps.factory.MindmapsTestGraphFactory;
import io.mindmaps.graql.Graql;
import io.mindmaps.graql.Var;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static io.mindmaps.graql.Graql.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TraceOntologyTest {

    private MindmapsTrace mindmapsTrace = MindmapsTrace.instance();
    private MindmapsGraph traceGraph = MindmapsClient.getGraph("trace");
    private MindmapsGraph fakeGraph = MindmapsTestGraphFactory.newEmptyGraph();

    @BeforeClass
    public static void setup(){
        System.setProperty(ConfigProperties.CONFIG_FILE_SYSTEM_PROPERTY, ConfigProperties.TEST_CONFIG_FILE);

        MindmapsEngineServer.start();
    }

    @Test
    public void testInsertConcept(){
        EntityType person = fakeGraph.putEntityType("person");
        Instance cecilia = fakeGraph.putEntity("Cecilia", person);

        mindmapsTrace.log("test message", Collections.singletonMap("person", cecilia));

        Collection<Entity> messages = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances();
        assertEquals(messages.size(), 1);

        Entity message = messages.iterator().next();
        assertNotNull(message);
        // check message test message
        // check message timestamp
//        assertTrue(message.resources().contains());

        Collection<Entity> nodes = traceGraph.getEntityType(TraceOntology.Type.NODE.getName()).instances();
        assertEquals(nodes.size(), 1);

        Entity node = nodes.iterator().next();
        assertNotNull(node);

        // check node id
        // check node name
        // check node type


    }


    @Test
    public void testGraqlInsertWorking(){

        // Graql
        Var message = var().isa(TraceOntology.Type.MESSAGE.getName());
        message.has(TraceOntology.Type.MESSAGE_TIMESTAMP.getName(), System.currentTimeMillis());

        Graql.withGraph(traceGraph).insert(message).execute();

        try {
            traceGraph.commit();
            assertEquals(traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances().size(), 1);
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }

        Graql.insert(message).withGraph(traceGraph).execute();

        try {
            traceGraph.commit();
            assertEquals(traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances().size(), 2);
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }


        // Core
        EntityType messageType = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName());
        traceGraph.putEntity("12345", messageType);

        try {
            traceGraph.commit();
            assertEquals(traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances().size(), 3);
        } catch (MindmapsValidationException e) {
            e.printStackTrace();
        }
    }



    // test ETL style traces

}
