
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
//        SimController sim = new SimController(7, 1, 2);
//        sim.initialize();
//        sim.loop();

        // OntologyParser takes care of retrieving all the data from the ontology, currently only retrieving data on Parameters and ParameterRelationships
        OntologyParser onto_parser = new OntologyParser("./substance-use.ttl");
        // Sim initializer will take care of creating everything for the agents given the data from the ontology
        SimInitializer sim_init = new SimInitializer(3, 0.5);
        Agent[] agents = sim_init.createAgents(onto_parser.getParameters(), onto_parser.getPerceptionRelationships(), onto_parser.getMovementActions(), onto_parser.getAcquireActions(), onto_parser.getConsumeActions() );
        // Sim controller will take care of running the simulation
        for (Agent a : agents){
            System.out.println(a.toString());
        }
        SimController sim_control = new SimController(agents, 1);
        // TODO : run the simulation for 3 days
        sim_control.loop();

//        Random r = new Random();
//        // nr * std * avg
//        double val = r.nextGaussian() * 0.2 + 0.5;
//        BigDecimal bdUp=new BigDecimal(val).setScale(2, RoundingMode.UP);
//        System.out.println(bdUp.doubleValue());







    }
}
