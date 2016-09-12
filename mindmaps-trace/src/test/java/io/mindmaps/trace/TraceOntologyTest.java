package io.mindmaps.trace;


import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import io.mindmaps.MindmapsGraph;
import io.mindmaps.engine.MindmapsEngineServer;
import io.mindmaps.engine.util.ConfigProperties;
import io.mindmaps.factory.MindmapsClient;
import io.mindmaps.factory.MindmapsTestGraphFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;
import static io.mindmaps.util.REST.WebPath.GRAPH_FACTORY_URI;

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
//        EntityType person = fakeGraph.putEntityType("person");
//        Instance cecilia = fakeGraph.putEntity("Cecilia", person);

//        mindmapsTrace.log("test message", Collections.singletonMap("person", cecilia));

//        System.out.println(traceGraph.getEntityType("message").instances());
    }

    // test ETL style traces

}
