# Query examples in SPARQL
Example 1 : Retrieve all the classes that Agent is a subClassOf.
```
PREFIX rdfs: <https://www.w3.org/TR/rdf-schema/#>
PREFIX concept: <https://www.dictionary.com/browse/>

SELECT ?class
WHERE {
    concept:Agent rdfs:subClassOf ?class
}
```
Example 2 : Retrieve all the parameters that are of the domain of child, and their min and max values.
```
PREFIX rdfs: <https://www.w3.org/TR/rdf-schema/#>
PREFIX concept: <https://www.dictionary.com/browse/>

SELECT ?param ?min ?max
WHERE {
    ?param rdfs:domain concept:Child .
    ?param a concept:Parameter .
    ?param concept:min ?min .
    ?param concept:max ?max .
}
```