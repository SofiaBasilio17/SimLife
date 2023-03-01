
// TODO:
// * make a class called Friends group
//      - it should have a method to check if an agent is in the group
//      - will be used in BroadcastToFriend to send the messages to the rest

// TODO : load from the ontology
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void creationTest(){
        // EXAMPLE OF CREATING PARAMETERS, PERCEPTIONS AND THEIR RELATIONSHIPS AND STATES
        Routine routineSchool = new Routine("SCHOOL", TimeOfDay.EARLY_MORNING, Constraints.HARD);
        Routine routineClass = new Routine("CLASS", TimeOfDay.MORNING, Constraints.HARD);
        Routine routineCafeteria = new Routine("CAFETERIA", TimeOfDay.NOON, Constraints.SOFT);
        Routine routineClassSecond = new Routine("CLASS", TimeOfDay.EARLY_NOON, Constraints.HARD);
        Routine routineSports = new Routine("SPORTS", TimeOfDay.LATE_NOON, Constraints.HARD);
        Routine routineHomeCurfew = new Routine("HOME", TimeOfDay.NIGHT, Constraints.SOFT);
        Routine[] routineTypeA = new Routine[]{ routineSchool, routineClass, routineCafeteria, routineClassSecond, routineSports, routineHomeCurfew };

        InteractionsMediator ii = new Interactions();
        BroadcastMediator bc = new BroadcastToFriend();

        System.out.println("EXAMPLE FOR PARAMETER-PARAMETER INTERACTIONS");
        // EXAMPLE FOR PARAMETER-PARAMETER INTERACTIONS
        // STEP 1. create the parameters (taken from the ontology)
        // we have one parameter in the ontology that is academic_grades
        Parameter exampleParam1 = new Parameter("academic_grades",1.0, 10.0);
        // we have one parameter in the ontology that is urge_to_smoke
        Parameter exampleParam2 = new Parameter("urge_to_smoke",0.0, 1.0);
        // STEP 2. create the parameter relationships (taken from the ontology)
        // using the example that One step increase in academic grades decreases the urge_to_smoke by 6%
        Parameter[] objects = new Parameter[1];
        // here our urge_to_smoke is added to the list of objects whose subject is academic_grades
        objects[0] = exampleParam2;
        String [] functions = new String[1];
        // the function is a string in post fix notation taken from the ontology
        functions[0] = "SCURRENT SPREVIOUS - -0.06 1.0 / * OCURRENT * OCURRENT +";
        // and so we create the Parameter Relationship
        ParameterRelationship pr = new ParameterRelationship(objects, functions);
        // and we set this parameter relationship for the Parameter: academic_grades
        exampleParam1.setParameterRelationship(pr);

        // STEP 3. create the parameter states, states are basically the instances of the Parameter attached to instances of Agent/Child
        // saying that the state of some child has a academic_grades (for example average) of 5.0
        ParameterState exampleState1 = new ParameterState(exampleParam1, 5.0, ii);
        // saying that the state of the same child's urge to smoke is initially 0.4
        ParameterState exampleState2 = new ParameterState(exampleParam2, 0.4, ii);

        // STEP 4. we then create the relationship between the ParameterStates for this agent (given the relationships before taken from the ontology)
        ParameterState[] stateObjects = new ParameterState[1];
        stateObjects[0] = exampleState2;
        // set the ParameterStateRelationship for academic_grades
        ParameterStateRelationship psr = new ParameterStateRelationship(stateObjects);
        exampleState1.setParameterStateRelationship(psr);
        // We test updating the academic grades for this child to 6.5
        // in turn it will update the urge_to_smoke through a mediator
        System.out.println("UPDATING ACADEMIC GRADES 5.0 --> 6.5");
        exampleState1.updateValue(6.5);

        System.out.println(exampleParam1.relationshipsToString());
        System.out.println(exampleParam2.relationshipsToString());


        System.out.println();
        System.out.println("EXAMPLE FOR PERCEPTION-PARAMETER INTERACTION");
        // EXAMPLE FOR PERCEPTION-PARAMETER INTERACTION
        // Use-case: A child perceives a friend smoking and in turn it updates their urge to smoke
        // STEP 1. create the perception
        Perception examplePercept = new Perception("SOMEONE_SMOKING");
        // STEP 2. create the parameter that is affected by this perception (has a connection in the ontology)
        // we already have the Parameter urge_to_smoke
        // STEP 3. create the relationship between the Perception and the Parameter (taken from the ontology)
        String[] pFunctions = new String[1];
        // the function will say that for every one time the child see's a peer smoke, it increases their urge_to_smoke by 85%
        pFunctions[0] = "OCURRENT 0.85 * OCURRENT +";
        // because the perception someone_smoking has the same objects as the academic_grades, we can use the same array
        PerceptionRelationship per = new PerceptionRelationship(examplePercept, objects, pFunctions);
        // STEP 4. create the ParameterState for a child that will be affected by this perception
        // we already have the ParameterState used previously for urge_to_smoke
        // STEP 5. create the PerceptionStateRelationship for this child's variable (ParameterState urge_to_smoke) and the Perception
        PerceptionStateRelationship pesr = new PerceptionStateRelationship(examplePercept, stateObjects, per);

        // create an agent and have it perceive someone_smoking
        // getting the parameter states all together in an array
        ParameterState[] childPS = new ParameterState[2];
        childPS[0] = exampleState1;
        childPS[1] = exampleState2;
        // creating the map for the perception relationships
        Map<Perception, PerceptionStateRelationship> perceptMap = new HashMap<Perception, PerceptionStateRelationship>();
        perceptMap.put(examplePercept, pesr);

        Agent childExample = new Agent(0, routineTypeA, false, bc, childPS, ii, perceptMap);
        exampleState2.getCurrentValue();
        childExample.perceive(examplePercept);
        exampleState2.getCurrentValue();
    }

    public static void main(String[] args) {
//        SimController sim = new SimController(7, 1, 2);
//        sim.initialize();
//        sim.loop();


        Model model = ModelFactory.createDefaultModel();
        model.read("./substance-use.ttl");
        String targetClass = "Child";
        InfModel imodel =  ModelFactory.createRDFSModel(model);

        // OntologyParser takes care of retrieving all the data from the ontology, currently only retrieving data on Parameters and ParameterRelationships
        OntologyParser onto_parser = new OntologyParser("./substance-use.ttl");
        SimInitializer sim_init = new SimInitializer(1, 0.5);
        sim_init.createAgents(onto_parser.getParameters(), onto_parser.getPerceptionRelationships());


        // STEP BY STEP HOW TO GET ALL THE PARAMETERS FOR CHILD
        // STEP 1: we need to retrieve the classes that child inherits from, with an inference model we can retrieve them without having to go up the inheritance tree manually
        // List<String> inheritsFrom = onto_parser.inferredInheritanceCheck(targetClass);
//        // STEP 2: we need to get all the parameters that those classes have, and create them
        // = onto_parser.retrieveParameters(inheritsFrom);
//        System.out.println(parameters);
        // onto_parser.retrieveRelationships(parameters);
//        for (Parameter p : parameters ){
//            System.out.println(p.relationshipsToString());
//        }

        // TODO : create all the Perceptions and PerceptionRelationships
        // TODO: initialize all the agents


        // TODO : run the simulation for 3 days







    }
}
