package io.mindmaps.etl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.mindmaps.MindmapsGraph;
import io.mindmaps.core.model.Entity;
import io.mindmaps.engine.controller.CommitLogController;
import io.mindmaps.engine.controller.GraphFactoryController;
import io.mindmaps.engine.controller.TransactionController;
import io.mindmaps.engine.util.ConfigProperties;
import io.mindmaps.example.PokemonGraphFactory;
import io.mindmaps.factory.GraphFactory;
import io.mindmaps.graql.QueryParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;

public class EtlTest {

    private static final String RIGHT_GRAPH_NAME = "right";

    private static MindmapsGraph leftGraph;
    private static MindmapsGraph rightGraph;

    @BeforeClass
    public static void setup(){
        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        logger.setLevel(Level.INFO);

        System.setProperty(ConfigProperties.CONFIG_FILE_SYSTEM_PROPERTY,ConfigProperties.TEST_CONFIG_FILE);

        new TransactionController();
        new CommitLogController();
        new GraphFactoryController();

        // initialize left graph for this test
        PokemonGraphFactory.loadGraph(leftGraph);
        rightGraph = GraphFactory.getInstance().getGraph(RIGHT_GRAPH_NAME);
    }

    @AfterClass
    public static void shutdown(){
        leftGraph.clear();
        leftGraph.close();
        rightGraph.clear();
        rightGraph.close();
    }

    @Test
    public void testPokemonTypes() throws IOException {
        setupExample("pokemon");

        Collection<Entity> leftPokemonTypes = leftGraph.getEntityType("pokemon-type").instances();
        for(Entity leftPokemonType:leftPokemonTypes){
            assertNotNull(rightGraph.getEntity(leftPokemonType.getId()));
        }
    }

    private void setupExample(String dir) throws IOException {
        executeGraqlInsertFromFile(dir + "/rightgraph.gql", rightGraph);
    }

    private void executeGraqlInsertFromFile(String file, MindmapsGraph tx) throws IOException {
        String exampleContents = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8)
                .stream()
                .reduce("", (s1, s2) -> s1 + "\n" + s2);

        QueryParser.create(tx).parseInsertQuery(exampleContents).execute();
    }
}
