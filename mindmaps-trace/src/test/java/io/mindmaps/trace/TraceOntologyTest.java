package io.mindmaps.trace;

import io.mindmaps.MindmapsGraph;
import io.mindmaps.concept.*;
import io.mindmaps.engine.MindmapsEngineServer;
import io.mindmaps.engine.util.ConfigProperties;
import io.mindmaps.example.PokemonGraphFactory;
import io.mindmaps.exception.MindmapsValidationException;
import io.mindmaps.factory.MindmapsClient;
import io.mindmaps.factory.MindmapsTestGraphFactory;
import io.mindmaps.graql.Graql;
import io.mindmaps.graql.Var;
import javafx.util.Pair;
import org.apache.avro.ipc.trace.Trace;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static io.mindmaps.graql.Graql.insert;
import static io.mindmaps.graql.Graql.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TraceOntologyTest {

    private MindmapsTrace mindmapsTrace = MindmapsTrace.instance();
    private MindmapsGraph traceGraph = MindmapsClient.getGraph("trace");
    private static MindmapsGraph pokemonGraph = MindmapsTestGraphFactory.newEmptyGraph();

    @BeforeClass
    public static void setup() throws InterruptedException {
        System.setProperty(ConfigProperties.CONFIG_FILE_SYSTEM_PROPERTY, ConfigProperties.TEST_CONFIG_FILE);
        MindmapsEngineServer.start();

        // init supporting graph
        PokemonGraphFactory.loadGraph(pokemonGraph);
    }

    @After
    public void after(){
        traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances().forEach(Concept::delete);
        traceGraph.getEntityType(TraceOntology.Type.NODE.getName()).instances().forEach(Concept::delete);
    }

    @Test
    public void testInsertOneMessage(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        mindmapsTrace.log("test message", Collections.singleton(new Pair<>("bulb", bulbasaur)));

        Collection<Entity> messages = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances();
        assertEquals(messages.size(), 1);

        Entity message = messages.iterator().next();
        assertNotNull(message);

        assertResourceRelationExists(message, TraceOntology.Type.MESSAGE_CONTENTS, "test message");

        Collection<Entity> nodes = traceGraph.getEntityType(TraceOntology.Type.NODE.getName()).instances();
        assertEquals(nodes.size(), 1);

        Entity node = nodes.iterator().next();
        assertNotNull(node);

        // check node id, name, type
        assertResourceRelationExists(node, TraceOntology.Type.NODE_NAME, "bulb");
        assertResourceRelationExists(node, TraceOntology.Type.NODE_TYPE, "pokemon");
        assertResourceRelationExists(node, TraceOntology.Type.NODE_ID, "Bulbasaur");

        assertNodeMessageRelationExists(node, message);
    }

    @Test
    public void testInsertMultipleNodesOneMessage(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        Instance venusaur = pokemonGraph.getEntity("Venusaur");
        Instance ivysaur = pokemonGraph.getEntity("Ivysaur");

        Collection<Pair<String, Concept>> toLog = Arrays.asList(
                new Pair<>("bulb", bulbasaur),
                new Pair<>("venu", venusaur),
                new Pair<>("ivy", ivysaur));

        mindmapsTrace.log("bulbasaur evolution", toLog);

        Collection<Entity> messages = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances();
        assertEquals(messages.size(), 1);

        Entity message = messages.iterator().next();
        assertNotNull(message);
        assertResourceRelationExists(message, TraceOntology.Type.MESSAGE_CONTENTS, "bulbasaur evolution");

        Collection<Entity> nodes = traceGraph.getEntityType(TraceOntology.Type.NODE.getName()).instances();
        assertEquals(nodes.size(), 3);

        nodes.forEach(node -> assertNodeMessageRelationExists(node, message));
    }

    @Test
    public void testInsertMultipleMessages(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        Instance venusaur = pokemonGraph.getEntity("Venusaur");
        Instance ivysaur = pokemonGraph.getEntity("Ivysaur");

        mindmapsTrace.log("bulb message", Collections.singleton(new Pair<>("bulb", bulbasaur)));
        mindmapsTrace.log("venu message", Collections.singleton(new Pair<>("venu", venusaur)));
        mindmapsTrace.log("ivy message", Collections.singleton(new Pair<>("ivy", ivysaur)));

        Collection<Entity> messages = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances();
        assertEquals(messages.size(), 3);

        RoleType role = traceGraph.getRoleType(TraceOntology.Type.MESSAGE_ROLE.getName());

        // check each message is in only one rel
        messages.forEach(message -> assertEquals(message.relations(role).size(), 1));
    }

    @Test
    public void testInsertNoMessageContents(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        mindmapsTrace.log(Collections.singleton(new Pair<>("bulb", bulbasaur)));

        Collection<Entity> messages = traceGraph.getEntityType(TraceOntology.Type.MESSAGE.getName()).instances();
        assertEquals(messages.size(), 1);

        Entity message = messages.iterator().next();
        assertNotNull(message);

        assertEquals(message.resources().size(), 1);
    }

    @Test
    public void testRetrieveOneMessage(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        mindmapsTrace.log("test message", Collections.singleton(new Pair<>("bulb", bulbasaur)));

        Collection<TraceMessage> messages = mindmapsTrace.retrieve(bulbasaur);
        assertEquals(messages.size(), 1);

        TraceMessage message = messages.iterator().next();
        System.out.println(message);
    }

    @Test
    public void testRetrieveOneMessageMultipleNodes(){
        Instance bulbasaur = pokemonGraph.getEntity("Bulbasaur");
        Instance venusaur = pokemonGraph.getEntity("Venusaur");

        Collection<Pair<String, Concept>> toLog = Arrays.asList(
                new Pair<>("bulb", bulbasaur),
                new Pair<>("venu", venusaur));

        mindmapsTrace.log("test message", toLog);

        Collection<TraceMessage> messages = mindmapsTrace.retrieve(bulbasaur, venusaur);
        assertEquals(messages.size(), 1);

        TraceMessage message = messages.iterator().next();
        System.out.println(message);
    }

    @Ignore
    @Test
    public void testETLStyleTrace(){
        // test insert and retrieval

        // insert
        Concept x = null;
        Concept y = null;
        Concept z = null;

        Concept rule = null;

        Concept rhs = null;

        Collection<Pair<String, Concept>> toLog = Arrays.asList(
                new Pair<>("lhs", x),
                new Pair<>("lhs", y),
                new Pair<>("lhs", z),
                new Pair<>("rule", rule),
                new Pair<>("rhs", rhs));

        mindmapsTrace.log("rule1", toLog);

        // retrieve


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

    private void assertResourceRelationExists(Entity owner, TraceOntology.Type type, Object value){
        assertTrue(owner.resources().stream().anyMatch(resource ->
                resource.type().getId().equals(type.getName()) &&
                        resource.getValue().equals(value)));
    }

    private void assertNodeMessageRelationExists(Entity node, Entity message){

        RoleType messageRole = traceGraph.getRoleType(TraceOntology.Type.MESSAGE_ROLE.getName());
        RoleType nodeRole = traceGraph.getRoleType(TraceOntology.Type.NODE_ROLE.getName());
        RelationType rel = traceGraph.getRelationType(TraceOntology.Type.MESSAGE_NODE_REL.getName());

        Map<RoleType, Instance> roleMap = new HashMap<>();
        roleMap.put(messageRole, message);
        roleMap.put(nodeRole, node);

        assertNotNull(traceGraph.getRelation(rel, roleMap));
    }
}
