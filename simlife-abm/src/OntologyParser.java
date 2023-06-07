import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.exec.http.Params;

import java.sql.Time;
import java.util.*;

public class OntologyParser {
    // GLOBALS
    private static String base_prefix  = "concept:";
    private static String base_uri = "https://www.dictionary.com/browse/";
    private static String rdf_uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static String rdfs_uri = "http://www.w3.org/2000/01/rdf-schema#";
    private static String base_query_header = "" +
            "PREFIX rdfs: <" + rdfs_uri + ">\n" +
            "PREFIX rdf: <" + rdf_uri +">\n" +
            "PREFIX " + base_prefix + "<" + base_uri + ">\n";
    // CREATED
    private Model model;
    private InfModel imodel;
    private String fileName;
    public List<Parameter> parameters;

    public List<Perception> perceptions;
    public List<PerceptionRelationship> perceptionRelationships;

    public List<MoveTo> movementActions;
    public List<Acquire> acquireActions;
    public List<Consume> consumeActions;
    public String targetClass;
    public OntologyParser(String ontologyFileName){
        this.fileName = ontologyFileName;
        this.model = ModelFactory.createDefaultModel();
        model.read("./substance-use.ttl");
        this.imodel = ModelFactory.createRDFSModel(model);
        this.targetClass = "Child";
        this.parameters = new ArrayList<>();
        this.perceptions = new ArrayList<>();
        this.perceptionRelationships = new ArrayList<>();
        this.parse_data();

    }
    private Parameter getParameterByName(String name){
        for (Parameter p : this.parameters){
            if (p.getParameterName().equals(name)){
                return p;
            }
        }
        return null;
    }
    private List<String> inferredInheritanceCheck(String targetClass){
        System.out.println("Checking inheritance of class "+ targetClass);
        List<String> inheritsFrom = new ArrayList<String>();
        String parent = "";
        String queryString = base_query_header +
                "SELECT ?class\n" +
                "WHERE {\n" +
                base_prefix + targetClass +
                " rdfs:subClassOf ?class . \n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext(); ) {
                QuerySolution soln = results.next() ;
                parent = soln.get("class").toString();
                // we are only looking for classes which belong to "concept", there are other classes from which classes inherits, namely rdfs:Resource
                if (parent.contains(base_uri)){
                    parent = parent.replace(base_uri,"");
                    inheritsFrom.add(parent);
                    System.out.println( targetClass + " inherits from " + parent);
                }
            }
        }
        System.out.println("----------------");
        return inheritsFrom;
    }
    private void retrieveParameters(List<String> classes){
        System.out.println("Retrieving the Parameters for all of the classes inherited from");
        // input: the model (the rdf), the classes we want to retrieve the parameters from
        // take all the classes in the list of class and retrieve the full list of Parameters
        for (String cl : classes){
            String queryString = base_query_header +
                    "SELECT ?paramlist ?index ?param ?min ?max\n" +
                    "WHERE {\n" +
                    // where the variables belong to the agentParameters of the class
                    base_prefix + cl + " " + base_prefix + "agentParameters ?paramlist." +
                    // where the result we're looking for is a Parameter
                    "?param a " + base_prefix + "Parameter .\n" +
                    // and it belongs to this classes' agentParameters (it is itemized)
                    "?paramlist ?index ?param .\n" +
                    // we want to also retrieve the min and the max
                    "?param concept:min ?min .\n" +
                    "?param concept:max ?max .\n" +
                    "}";

            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
                ResultSet results = qexec.execSelect() ;
                if (results.hasNext()){
                    for ( ; results.hasNext() ; ) {
                        QuerySolution soln = results.next() ;
                        String param = soln.get("param").toString();
                        param = param.replace(base_uri, "");
                        System.out.println(" * " + param);
                        System.out.println(" ---- Minimum : "+ soln.get("min").toString());
                        System.out.println(" ---- Maximum : "+ soln.get("max").toString());
                        Parameter p = new Parameter(param,Double.valueOf(soln.get("min").toString()), Double.valueOf(soln.get("max").toString()));
                        this.parameters.add(p);
                    }
                }else {
                    System.out.println("Class "+ cl + " has no Parameters.");
                }

            }
        }
        System.out.println("----------------");
        this.retrieveParameterRelationships();
    }
    private void retrieveParameterRelationships(){
        // input: the model (the rdf), the classes we want to retrieve the parameters from
        // take all the classes in the list of class and retrieve the full list of Parameters
        System.out.println("Retrieving the Parameter Relationships for all of the Parameters");
        for (Parameter p : this.parameters){
            // we need to first check if the parameter has a relationship (base_prefix + p)
            String queryString = base_query_header +
                    "SELECT ?rel ?objs ?funcs ?index ?objval ?funcval \n" +
                    "WHERE {\n" +
                    base_prefix + p.getParameterName() + " concept:parameterRelationship ?rel .\n" +
                    "?rel concept:objects ?objs .\n" +
                    "?rel concept:functions ?funcs .\n" +
                    "?objs ?index ?objval .\n" +
                    "?funcs ?index ?funcval .\n" +
                    "}";
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
                ResultSet results = qexec.execSelect() ;
                if (results.hasNext()){
//                    System.out.println(p + " has relationships.");
                    List<Parameter> objectList = new ArrayList<>();
                    List<String> functionList = new ArrayList<>();
                    for ( ; results.hasNext() ; ) {
                        // each query returns a list of pairs objval and funcval which are the objects and associated function from the subject (effect on Parameter object)
                        QuerySolution soln = results.next();
                        // list indexes are denoted in rdf as rdf:_1 , rdf:_2, so here we are checking if that is the case, we may get other connections that we are note interested in
                        if (soln.get("index").toString().contains(rdf_uri+"_")){
                            // getting the object name
                            String objVal = soln.get("objval").toString();
                            // getting the function
                            String funcVal = soln.get("funcval").toString();
                            objVal = objVal.replace(base_uri, "");
                            // getting the Parameter
                            Parameter o = getParameterByName(objVal);
                            funcVal = funcVal.replace(base_uri, "");
                            objectList.add(o);
                            functionList.add(funcVal);
                        }

                    }
                    // now we need to create the arrays that ParameterRelationship takes
                    // namely the objects (Parameter) and functions (String)
                    Parameter[] objects = objectList.toArray(new Parameter[0]);
                    String[] functions = functionList.toArray(new String[0]);
                    // create the Parameter relationships
                    ParameterRelationship pr = new ParameterRelationship(objects, functions);
                    // and then set it for the Parameter
                    p.setParameterRelationship(pr);
                    System.out.println(p + " has " + objects.length + " relationships");
                }else {
                    System.out.println(p + " has no relationships.");
                }

            }
            System.out.println(p.relationshipsToString());
        }
        System.out.println("----------------");
    }

    private void retrievePerceptions(List<String> classes){
        System.out.println("Retrieving the Perceptions for all of the classes inherited from");
        // input: the model (the rdf), the classes we want to retrieve the parameters from
        // take all the classes in the list of class and retrieve the full list of Parameters
        for (String cl : classes){
            String queryString = base_query_header +
                    "SELECT ?percept\n" +
                    "WHERE {\n" +
                    // where the variables are of the classes' agentPerceptions
                    base_prefix + cl + " " + base_prefix +"agentPerceptions ?perceptlist .\n" +
                    // it is an element of the perception list/bag
                    "?perceptlist ?index ?percept .\n" +
                    // where the variables that are of type concept:Perception
                    "?percept a " + base_prefix + "Perception .\n" +
                    "}";

            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
                ResultSet results = qexec.execSelect() ;
                if (results.hasNext()){
                    for ( ; results.hasNext() ; ) {
                        QuerySolution soln = results.next() ;
                        String percept = soln.get("percept").toString();
                        percept = percept.replace(base_uri, "");

                        System.out.println(" * " + percept);
                        Perception p = new Perception(percept);
                        this.perceptions.add(p);
                    }
                }else {
                    System.out.println("Class "+ cl + " has no Perceptions.");
                }
            }
        }
        System.out.println("----------------");
        this.retrievePerceptionRelationships();
    }

    private void retrievePerceptionRelationships(){
        System.out.println("Retrieving the Perception Relationships for all of the Perceptions");
        for (Perception p : this.perceptions){
            // we need to first check if the parameter has a relationship (base_prefix + p)
            String queryString = base_query_header +
                    "SELECT ?rel ?objs ?funcs ?index ?objval ?funcval \n" +
                    "WHERE {\n" +
                    base_prefix + p.getName() + " concept:perceptionRelationship ?rel .\n" +
                    "?rel concept:objects ?objs .\n" +
                    "?rel concept:functions ?funcs .\n" +
                    "?objs ?index ?objval .\n" +
                    "?funcs ?index ?funcval .\n" +
                    "}";
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
                ResultSet results = qexec.execSelect() ;
                if (results.hasNext()){
//                    System.out.println(p + " has relationships.");
                    List<Parameter> objectList = new ArrayList<>();
                    List<String> functionList = new ArrayList<>();
                    for ( ; results.hasNext() ; ) {
                        // each query returns a list of pairs objval and funcval which are the objects and associated function from the subject (effect on Parameter object)
                        QuerySolution soln = results.next();
                        if (soln.get("index").toString().contains(rdf_uri+"_")){
                            // getting the object name
                            String objVal = soln.get("objval").toString();
                            // getting the function
                            String funcVal = soln.get("funcval").toString();
                            objVal = objVal.replace(base_uri, "");
                            // getting the Parameter
                            Parameter o = getParameterByName(objVal);
                            funcVal = funcVal.replace(base_uri, "");
                            objectList.add(o);
                            functionList.add(funcVal);
                        }

                    }
                    // now we need to create the arrays that ParameterRelationship takes
                    // namely the objects (Parameter) and functions (String)
                    Parameter[] objects = objectList.toArray(new Parameter[0]);
                    String[] functions = functionList.toArray(new String[0]);
                    // create the Parameter relationships
                    PerceptionRelationship pr = new PerceptionRelationship(p, objects, functions);
                    // and then set it for the Parameter
                    this.perceptionRelationships.add(pr);
                    System.out.println(p + " has " + objects.length + " relationships");
                    System.out.println(pr.toString());
                }else {
                    System.out.println(p + " has no relationships.");
                }
            }
        }

        System.out.println("----------------");
    }

    private List<String> getSubclassOfAction(){
        System.out.println("----------------");
        System.out.println("All types of actions in the ontology : ");
        List<String> subclasses = new ArrayList<>();
        String queryString = base_query_header +
                "SELECT ?act\n" +
                "WHERE {\n" +
                // what are all of the subclasses of Action
                "?act " + "rdfs:subClassOf " + base_prefix +"Action .\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String action = soln.get("act").toString();
                    if (!action.equals(base_uri+"Action")){
                        action = action.replace(base_uri, "");
                        System.out.println(" * " + action);
                        subclasses.add(action);
                    }

                }
            }else {
                System.out.println("There are no subclasses of Action in the ontology");
            }
        }
        System.out.println("----------------");
        return subclasses;
    }
    private List<String> retrieveLocations(){
        List<String> locations = new ArrayList<>();
        String queryString = base_query_header +
                "SELECT ?location ?index\n" +
                "WHERE {\n" +
                "concept:locationsList ?index ?location. \n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String location = soln.get("location").toString();
                    if(soln.get("index").toString().contains(rdf_uri+"_")){
                        System.out.println("Detected location "+ location + " in the ontology.");
                        locations.add(location.replace(" ", ""));
                    }

                }
            }
//            else {
//                System.out.println("There are no locations in the ontology");
//            }
        }
        return locations;
    }

    private String getPreconditionLocation(String precon_uri){
        String location = "";
        String queryString = base_query_header +
                "SELECT ?location \n" +
                "WHERE {\n" +
                precon_uri + " concept:location ?location. \n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    location = soln.get("location").toString();
                }
            }
//            else {
//                System.out.println("There is no location for precondition " + precon_uri);
//            }
        }
        return location;
    }

    private TimePeriod getPreconditionTime(String precon_uri){
        String queryString = base_query_header +
                "SELECT ?start ?end \n" +
                "WHERE {\n" +
                precon_uri + " concept:availablePeriod ?period .\n"+
                "?period concept:periodStart ?start .\n" +
                "?period concept:periodEnd ?end .\n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String periodStart = soln.get("start").toString().replace(base_uri, "");
                    String periodEnd = soln.get("end").toString().replace(base_uri, "");
                    TimePeriod tp = new TimePeriod(Integer.parseInt(periodStart),Integer.parseInt(periodEnd));
                    return tp;
                }
            }
//            else {
//                System.out.println("There is no time period for precondition " + precon_uri);
//            }
        }
        return null;
    }

    private String getPreconditionResource(String precon_uri){
        String resource = "";
        String queryString = base_query_header +
                "SELECT ?resource \n" +
                "WHERE {\n" +
                precon_uri + " concept:resource ?resource .\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    resource = soln.get("resource").toString().replace(base_uri, "");
                }
            }
//            else {
//                System.out.println("There is no resource precondition " + precon_uri);
//            }
        }
        return resource;

    }
    private Preconditions getPreconditions(String uri){
        // first we query for the precondition object of this uri
        String queryString = base_query_header +
                "SELECT ?prec \n" +
                "WHERE {\n" +
                uri + " concept:preconditions ?prec. \n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            // If it has a precondition then it means it has at least location, timeperiod, or resource filled
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String precName = soln.get("prec").toString().replace(base_uri, "");
                    // so we can create an instance of Precondition()
                    Preconditions precondition = new Preconditions(precName);
                    String prec_uri = "concept:"+precName;
                    // if there are any preconditions we will query
                    // (individually otherwise it doesnt work for ones that don't have one of the vars) for
                    // * location
                    String location = this.getPreconditionLocation(prec_uri);
                    if(!location.isEmpty()){
                        // System.out.println("Location precondition for " + uri + " " + location);
                        precondition.setLocation(location);
                        // This is a quick fix, TODO: Preconditions need to be revised and a Location class must be created in the ontology such that location is attached to a move to, and preconditions have easy access to what a precondition entails
                        for (MoveTo m : this.movementActions){
                            if (m.equals(location)){
                                precondition.setMoveTo(m);
                                break;
                            }
                        }
                    }
                    // * availablePeriod
                    TimePeriod period = this.getPreconditionTime(prec_uri);
                    if (period != null){
                        // System.out.println("Timeperiod precondition for " + uri + " " + period.toString());
                        precondition.setTime(period);
                    }
                    // * resource
                    String resource = this.getPreconditionResource(prec_uri);
                    if(!resource.isEmpty()){
                        // System.out.println("Resource precondition for " + uri + " " + resource);
                        precondition.setResource(resource);
                        // TODO: Same here as for location, resource needs to be its own class that is tied to its acquisition action
                        for ( Acquire a : this.acquireActions){
                            if (a.isResource(resource)){
                                precondition.setAcquire(a);
                                break;
                            }
                        }
                    }
                    return precondition;
                }
            }
//            else {
//                System.out.println("There are no preconditions for " + uri);
//            }
        }

        return null;
    }

    // retrieve parameterfactors and parameterweights
    private Map<Parameter,Double> getParameterFactors(String uri){
        Map<Parameter,Double> parameterFactors = new HashMap<>();
        String queryString = base_query_header +
                "SELECT ?param ?coeff ?index\n" +
                "WHERE {\n" +
                uri + " concept:parameterFactors ?pf. \n"+
                uri + " concept:parameterWeights ?pw. \n"+
                "?pf ?index ?param. \n"+
                "?pw ?index ?coeff. \n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            // if it has a result that means it contains at least 1 parameter factors and 1 coefficient for that parameter
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    if(soln.get("index").toString().contains(rdf_uri+"_")){
                        // then it is an item of the rdf list construct, we do not want to get any other rdf types
                        // we have to retrieve the parameter that it is equivalent to
                        Parameter parameter = this.getParameterByName(soln.get("param").toString().replace(base_uri, ""));
                        // we have to append that parameter object and the coefficient to the map
                        if (parameter != null){
                            Double coefficient = Double.parseDouble(soln.get("coeff").toString().replace(base_uri, ""));
                            parameterFactors.put(parameter, coefficient);
                        }
                    }
                }
            }
//            else {
//                System.out.println("There are no parameter factors for " + uri);
//            }
        }
        return parameterFactors;
    }
    // retrieve statefactors and stateweights

    // retrieve commitmentfactor
    private Action getActionByName(String name){
        for (MoveTo a : this.movementActions){
            if (a.equals(name)){
                return a;
            }
        }
        for (Acquire a : this.acquireActions){
            if (a.equals(name)){
                return a;
            }
        }
        for (Consume a : this.consumeActions){
            if (a.equals(name)){
                return a;
            }
        }
        return null;
    }
    private Commitment getCommitmentFactor(String uri, Action commitmentAction){
        String queryString = base_query_header +
                "SELECT ?act ?start ?end \n" +
                "WHERE {\n" +
                uri + " concept:commitmentFactor ?commit .\n"+
                "?commit concept:commitmentAction ?act .\n"+
                // we need to check for if its type MoveTo, Acquire, Consume or a general action
                // TODO: this needs to be fixed to retrieve only the type of action (moveto, acquire, consume, general)
                // "?act a ?type .\n"+
                // we need to get the commitment period
                "?commit concept:commitmentPeriod ?cp . \n"+
                "?cp concept:periodStart ?start . \n"+
                "?cp concept:periodEnd ?end . \n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            // if it has a result that means it contains one commitment factor
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    // create a TimePeriod
                    // create the commitment with commitmentAction, TimePeriod
                    int periodStart = Integer.parseInt(soln.get("start").toString().replace(base_uri, ""));
                    int periodEnd = Integer.parseInt(soln.get("end").toString().replace(base_uri, ""));
                    TimePeriod timePeriod = new TimePeriod(periodStart, periodEnd);
                    Commitment commitmentFactor = new Commitment(commitmentAction, timePeriod);
                    return commitmentFactor;
                }
            }
//            else {
//                System.out.println("There is no commitment factor for " + uri);
//            }
        }
        return null;
    }

    // retrieve commitmentproduced

    // retrieve stateproduced

    // get perception by name
    private Perception getPerceptionByName(String perceptName){
        // for each perception in this.perceptions
        // we check if they're the same, currently this is done
        for (Perception p : this.perceptions){
            if (p.equals(perceptName)){
                return p;
            }
        }
        return null;
    }
    // retrieve perceptionproduced
    private Perception getPerceptionProduced(String uri){
        String queryString = base_query_header +
                "SELECT ?percept  \n" +
                "WHERE {\n" +
                uri + " concept:perceptionProduced ?percept .\n"+
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            // if it has a result that means it contains one commitment factor
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String perceptName = soln.get("percept").toString().replace(base_uri, "");
                    Perception percept = this.getPerceptionByName(perceptName);
                    return percept;
                }
            }
//            else {
//                System.out.println("There is no perception produced for " + uri);
//            }
        }

        return null;
    }

    // retrieve actionrelationship
    private ActionRelationship getActionRelationship(String uri, Action act){
        String queryString = base_query_header +
                "SELECT ?rel ?objval ?funcval ?index \n" +
                "WHERE {\n" +
                uri + " concept:actionRelationship ?rel .\n"+
                "?rel concept:objects ?objs .\n"+
                "?rel concept:functions ?funcs .\n"+
                "?objs ?index ?objval .\n"+
                "?funcs ?index ?funcval .\n"+
                "}";
        Query query = QueryFactory.create(queryString);
        List<Parameter> objectList = new ArrayList<>();
        List<String> functionList = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            // if it has a result that means it contains one commitment factor
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    if (soln.get("index").toString().contains(rdf_uri+"_")){
                        // getting the object name
                        String objVal = soln.get("objval").toString().replace(base_uri, "");
                        // getting the function
                        String funcVal = soln.get("funcval").toString().replace(base_uri, "");
                        // getting the Parameter
                        Parameter o = getParameterByName(objVal);

                        if(o != null){
                            objectList.add(o);
                            functionList.add(funcVal);
                        }
                    }
                }
                // now we need to create the arrays that ActionRelationship takes
                // namely the objects (Parameter) and functions (String)
                if(!objectList.isEmpty() && !functionList.isEmpty()){
                    Parameter[] objects = objectList.toArray(new Parameter[0]);
                    String[] functions = functionList.toArray(new String[0]);
                    // create the Action relationship
                    ActionRelationship ar = new ActionRelationship(act, objects, functions);
                    return ar;
                }

            }
        }
        return null;
    }

    private void getActionComponents(Action act){
        // call all the necessary functions here to get all of the components for that action
        BroadcastMediator bc = new BroadcastToFriend();
        String act_uri = "concept:"+act.getName();

        act.setBroadcastMediator(bc);

        Preconditions preconditions = this.getPreconditions(act_uri);
        act.setPreconditions(preconditions);

        Map<Parameter, Double> parameterFactors = this.getParameterFactors(act_uri);
        act.setParameterFactors(parameterFactors);

        Commitment commitmentFactor = this.getCommitmentFactor(act_uri, act);
        act.setCommitmentFactor(commitmentFactor);

        Perception perceptionProduced = this.getPerceptionProduced(act_uri);
        act.setPerceptionProduced(perceptionProduced);

        ActionRelationship actionRelationship = this.getActionRelationship(act_uri, act);
        act.setActionRelationship(actionRelationship);

    }
    private List<MoveTo> getMovementActionsForClass(String specificClass){
        // TODO: add here the commitments, factors, states, preconditions,...
        List<MoveTo> moveToActions = new ArrayList<>();

        String queryString = base_query_header +
                "SELECT ?action ?where\n" +
                "WHERE {\n" +
                // what are all of the subclasses of Action
                base_prefix + specificClass + " concept:agentActions ?classActions .\n" +
                "?classActions ?index ?action .\n"+
                "?action a concept:MoveTo .\n" +
                "?action concept:where ?where .\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String actionName = soln.get("action").toString();
                    String location = soln.get("where").toString();
                    actionName = actionName.replace(base_uri, "");
                    location = location.replace(base_uri, "");
                    MoveTo newMove = new MoveTo(actionName, location);
                    moveToActions.add(newMove);
                    System.out.println("Class " + specificClass + " has action MOVETO : "+ actionName);
                }
            }else {
                System.out.println("There are no Movement Actions for class "+ specificClass);
            }
        }
        return moveToActions;
    }
    private List<MoveTo> retrieveAllMovementActions(List<String> classes){
        List<MoveTo> movementActions = new ArrayList<>();
        for (String cl : classes){
            // we first retrieve the actions in the ontology
            movementActions.addAll(this.getMovementActionsForClass(cl));
        }
        // then we will need to make all of the actions for the existing locations
        // TODO: change location to being its own class instead of a string
        List<String> allLocations = this.retrieveLocations();
        boolean exists = false;
        for (String location : allLocations){
            // if location already in moveActions then we do not make a new one
            for(MoveTo m : movementActions){
                if (m.equals(location)){
                    exists = true;
                    break;
                }
            }
            if (!exists){
                // create the move to location
                // all moveto actions should have a name starting as MOVETO +
                // whatever the location is in uppercase and without spaces
                MoveTo newmoveto = new MoveTo("MOVETO"+location.toUpperCase(),location);
                movementActions.add(newmoveto);
            }
            exists = false;
        }

        return movementActions;
    }

    private List<Acquire> getAcquireActionsForClass(String specificClass) {
        List<Acquire> acquireActions = new ArrayList<>();
        String queryString = base_query_header +
                "SELECT ?action ?raq ?rquan\n" +
                "WHERE {\n" +
                // what are all of the subclasses of Action
                base_prefix + specificClass + " concept:agentActions ?classActions .\n" +
                "?classActions ?index ?action .\n"+
                "?action a concept:Acquire .\n" +
                "?action concept:resourceAcquired ?raq .\n" +
                "?action concept:quantityAcquired ?rquan .\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String actionName = soln.get("action").toString();
                    String resourceAquired = soln.get("raq").toString();
                    String resourceQuantityStr = soln.get("rquan").toString();
                    Double resourceQuantity =Double.parseDouble(resourceQuantityStr);
                    actionName = actionName.replace(base_uri, "");

                    Acquire newaquire = new Acquire(actionName, resourceAquired, resourceQuantity);
                    acquireActions.add(newaquire);
                    System.out.println("Class " + specificClass + " has action ACQUIRE : "+ actionName);
                }
            }else {
                System.out.println("There are no Acquire Actions for class "+ specificClass);
            }
        }
        return acquireActions;

    }
    private List<Acquire> retrieveAllAcquireActions(List<String> classes){
        List<Acquire> acquireActions = new ArrayList<>();
        for (String cl : classes){
            acquireActions.addAll(this.getAcquireActionsForClass(cl));
        }
        // TODO: add more resources to the ontology and make acquires for all of them
        return acquireActions;
    }
    private List<Consume> getConsumeActionsForClass(String specificClass) {
        List<Consume> consumeActions = new ArrayList<>();
        String queryString = base_query_header +
                "SELECT ?action ?rcon ?rquan\n" +
                "WHERE {\n" +
                // what are all of the subclasses of Action
                base_prefix + specificClass + " concept:agentActions ?classActions .\n" +
                "?classActions ?index ?action .\n"+
                "?action a concept:Consume .\n" +
                "?action concept:resourceConsumed ?rcon .\n" +
                "?action concept:quantityConsumed ?rquan .\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect() ;
            if (results.hasNext()){
                for ( ; results.hasNext() ; ) {
                    QuerySolution soln = results.next() ;
                    String actionName = soln.get("action").toString().replace(base_uri, "");;
                    String resourceConsumed = soln.get("rcon").toString();
                    String resourceQuantityStr = soln.get("rquan").toString();
                    Double resourceQuantity =Double.parseDouble(resourceQuantityStr);

                    Consume newconsume = new Consume(actionName, resourceConsumed, resourceQuantity);
                    consumeActions.add(newconsume);
                    System.out.println("Class " + specificClass + " has action CONSUME : "+ actionName);
                }
            }else {
                System.out.println("There are no Consume Actions for class "+ specificClass);
            }
        }
        return consumeActions;

    }

    private List<Consume> retrieveAllConsumeActions(List<String> classes){
        List<Consume> consumeActions = new ArrayList<>();
        for (String cl : classes){
            consumeActions.addAll(this.getConsumeActionsForClass(cl));
        }
        // TODO: add more resources to the ontology and make consumes for all of them
        //  (or the ones that can be consumed)
        return consumeActions;
    }

    private void parse_data(){
        // STEP BY STEP HOW TO GET ALL THE PARAMETERS FOR CHILD
        // STEP 1: we need to retrieve the classes that child inherits from, with an inference model we can retrieve them without having to go up the inheritance tree manually
        List<String> inheritsFrom = this.inferredInheritanceCheck(this.targetClass);
        // STEP 2: we need to get all the parameters that those classes have, and create them, and inside this function we will also retrieve the relationships
        // and set them for each parameter WITH a relationship to other parameters (the parent of)
        this.retrieveParameters(inheritsFrom);
        this.retrievePerceptions(inheritsFrom);

//        for (Parameter p : this.parameters ){
//            Double random_val = Math.round((new Random().nextDouble() * (p.getMax() - p.getMin())) + p.getMin()*100.0)/100.0;
//            System.out.println("Random value for " + p.getParameterName() + " between "+ p.getMin() + " and " + p.getMax() + " : " + random_val);
//            System.out.println(p.relationshipsToString());
//        }
        this.movementActions = this.retrieveAllMovementActions(inheritsFrom);
        this.acquireActions = this.retrieveAllAcquireActions(inheritsFrom);
        this.consumeActions = this.retrieveAllConsumeActions(inheritsFrom);
        for (MoveTo m : this.movementActions){
            this.getActionComponents(m);
            System.out.println(m);
        }
        for (Acquire a : this.acquireActions){
            this.getActionComponents(a);
            System.out.println(a);
        }
        for (Consume c : this.consumeActions){
            this.getActionComponents(c);
            System.out.println(c);
        }
        // TODO: create all other actions that are not movement, or resource management
    }
    public List<Parameter> getParameters(){
        return this.parameters;
    }
    public List<PerceptionRelationship> getPerceptionRelationships(){
        return this.perceptionRelationships;
    }
    public List<MoveTo> getMovementActions(){
        return this.movementActions;
    }
    public List<Acquire> getAcquireActions(){
        return this.acquireActions;
    }
    public List<Consume> getConsumeActions(){
        return this.consumeActions;
    }
}
