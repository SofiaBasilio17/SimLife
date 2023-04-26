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

todo: organize these
```
SELECT ?percept
WHERE {
concept:Human concept:agentPerceptions ?perceptlist .
?perceptlist ?index ?percept .
?percept a concept:Perception.
}

SELECT ?prop ?index
WHERE {
concept:locationsList ?index ?prop .
}

SELECT ?act ?value
WHERE {
?act a concept:Action .
?props rdfs:domain concept:MoveTo .
?act ?props ?value .
}

SELECT ?paramlist ?param ?min ?max
WHERE {
concept:Child concept:agentParameters ?paramlist .
?paramlist ?index ?param .
?param a concept:Parameter.
?param concept:min ?min .
?param concept:max ?max .
}
```