import org.apache.jena.query.*;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

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
    public List<PerceptionRelationship> percRel;
    public String targetClass;
    public OntologyParser(String ontologyFileName){
        this.fileName = ontologyFileName;
        this.model = ModelFactory.createDefaultModel();
        model.read("./substance-use.ttl");
        this.imodel = ModelFactory.createRDFSModel(model);
        this.targetClass = "Child";
        this.parameters = new ArrayList<>();
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
                    "SELECT ?param ?min ?max\n" +
                    "WHERE {\n" +
                    // where the variables are of the domain of the concept:class
                    "?param rdfs:domain " + base_prefix + cl + ".\n" +
                    // where the variables that are of type concept:Parameter
                    "?param a " + base_prefix + "Parameter .\n" +
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
        this.retrieveRelationships();
    }
    private Map<Parameter, String> retrieveRelationshipPairs(String objs, String funcs ) {
        Map<Parameter, String> pairs = new HashMap<Parameter, String>();
        String queryString = base_query_header +
                "SELECT ?objval ?index ?funcval \n" +
                "WHERE {\n" +
                base_prefix + objs + " ?index ?objval .\n" +
                base_prefix + funcs + " ?index ?funcval.\n" +
                "}";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
            ResultSet results = qexec.execSelect();
            if (results.hasNext()) {
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.next();
                    // we may have other results in the query, but what we are interest in is the triplets with rdf:_1, rdf:_2,
                    // and so on, so we check for the if the index contains "rdf:_"
                    if (soln.get("index").toString().contains(rdf_uri + "_")) {
                        Parameter o = getParameterByName(soln.get("objval").toString().replace(base_uri, ""));
                        if (o != null){
                            pairs.put(o, soln.get("funcval").toString());
                        }
                        // System.out.println("This query item contains an object and a function");
                        // System.out.println("Object: " + soln.get("objval").toString() + " Function: " + soln.get("funcval"));
                    }
                }
            }

        }
        return pairs;
    }
    private void retrieveRelationships(){
        // input: the model (the rdf), the classes we want to retrieve the parameters from
        // take all the classes in the list of class and retrieve the full list of Parameters
        for (Parameter p : this.parameters){
            // we need to first check if the parameter has a relationship (base_prefix + p)
            String queryString = base_query_header +
                    "SELECT ?rel ?objs ?funcs \n" +
                    "WHERE {\n" +
                    base_prefix + p.getParameterName() + " concept:ParameterRelationship ?rel .\n" +
                    "?rel concept:objects ?objs .\n" +
                    "?rel concept:functions ?funcs .\n" +
                    "}";
            Query query = QueryFactory.create(queryString);
            try (QueryExecution qexec = QueryExecutionFactory.create(query, this.imodel)) {
                ResultSet results = qexec.execSelect() ;
                if (results.hasNext()){
//                    System.out.println(p + " has relationships.");
                    for ( ; results.hasNext() ; ) {
                        QuerySolution soln = results.next();
                        // we are interested in retrieving the objects and functions (linked by index)
                        String objs = soln.get("objs").toString();
                        String funcs = soln.get("funcs").toString();
                        objs = objs.replace(base_uri, "");
                        funcs = funcs.replace(base_uri, "");
                        // now we query for the items in the objects sequence and functions sequence
                        Map<Parameter,String> pairs = retrieveRelationshipPairs(objs, funcs);
                        // if there are any pairs
                        if (!pairs.isEmpty()){
                            Parameter[] objects = pairs.keySet().toArray(new Parameter[0]);
                            String[] functions = pairs.values().toArray(new String[0]);
                            // create the Parameter relationships
                            ParameterRelationship pr = new ParameterRelationship(objects, functions);
                            // and then set it for the Parameter
                            p.setParameterRelationship(pr);
                            System.out.println(p + " has " + objects.length + " relationships");
                        }

                    }
                }else {
                    System.out.println(p + " has no relationships.");
                }

            }
            System.out.println(p.relationshipsToString());
        }
        System.out.println("----------------");
    }

    private void retrievePerceptions(){


    }
    private void parse_data(){
        // STEP BY STEP HOW TO GET ALL THE PARAMETERS FOR CHILD
        // STEP 1: we need to retrieve the classes that child inherits from, with an inference model we can retrieve them without having to go up the inheritance tree manually
        List<String> inheritsFrom = this.inferredInheritanceCheck(targetClass);
        // STEP 2: we need to get all the parameters that those classes have, and create them, and inside this function we will also retrieve the relationships
        // and set them for each parameter WITH a relationship to other parameters (the parent of)
        this.retrieveParameters(inheritsFrom);
//        for (Parameter p : this.parameters ){
//            Double random_val = Math.round((new Random().nextDouble() * (p.getMax() - p.getMin())) + p.getMin()*100.0)/100.0;
//            System.out.println("Random value for " + p.getParameterName() + " between "+ p.getMin() + " and " + p.getMax() + " : " + random_val);
//            System.out.println(p.relationshipsToString());
//        }
    }
    public List<Parameter> getParameters(){
        return this.parameters;
    }

}
