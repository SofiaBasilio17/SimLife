# Query examples in SPARQL
The first three lines when writing a query are always the prefix header, these include the links to rdf, rdfs, and concept:
```
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX concept: <https://www.dictionary.com/browse/> 
```

Example 1 : Retrieve all the classes that Agent is a subClassOf.
```
SELECT ?class
WHERE {
    concept:Agent rdfs:subClassOf ?class
}
```
Example 2 : Retrieve all the parameters that are of the domain of child, and their min and max values.
```
SELECT ?paramlist ?index ?param ?min ?max
WHERE {
    concept:Child concept:agentParameters ?paramlist .
    ?param a concept:Parameter. 
    ?paramlist ?index ?param .
    ?param concept:min ?min .
    ?param concept:max ?max .
}
```