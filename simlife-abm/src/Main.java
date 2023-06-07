
// TODO:
// * make a class called Friends group
//      - it should have a method to check if an agent is in the group
//      - will be used in BroadcastToFriend to send the messages to the rest

// TODO : load from the ontology
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import org.apache.jena.*;
import org.apache.jena.atlas.iterator.IteratorCloseable;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
//import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.AsyncParser;
import java.io.*;
public class Main {

    public static void main(String[] args) {
        // TODO: ADD VARIABLE FOR PRINT ENABLE, if its on then it'll print out (at different stages in the process), otherwise it doesn't
        final long startTime = System.currentTimeMillis();
        // OntologyParser takes care of retrieving all the data from the ontology, currently only retrieving data on Parameters and ParameterRelationships
        OntologyParser onto_parser = new OntologyParser("./substance-use.ttl");
        // Sim initializer will take care of creating everything for the agents given the data from the ontology
        SimInitializer sim_init = new SimInitializer(80000,onto_parser.getParameters(), onto_parser.getPerceptionRelationships(), onto_parser.getMovementActions(), onto_parser.getAcquireActions(), onto_parser.getConsumeActions());
        // Agent[] agents = sim_init.createAgents(onto_parser.getParameters(), onto_parser.getPerceptionRelationships(), onto_parser.getMovementActions(), onto_parser.getAcquireActions(), onto_parser.getConsumeActions());
        Agent[] agents = sim_init.getAgents();
        // Sim controller will take care of running the simulation
        for (Agent a : agents){
            System.out.println(a.toString());
        }
        SimController sim_control = new SimController(agents, 93);
        // TODO : run the simulation for 3 days
        sim_control.loop();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));

        // 1000 agents, 10 iterations
        // Total execution time: 3041

        // 1000 agents, 31 iterations
        // Total execution time: 4254

        // 10000 agents, 31 iterations
        // Total execution time: 25238

        // 10000 agents, 93 iterations
        // Total execution time: 68166




    }
}
