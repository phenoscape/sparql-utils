# sparql-utils

`sparql-utils` provides some convenient utilities for working with SPARQL queries in Scala. Current functionality includes:
- A string interpolator for embedding and properly formatting/escaping Scala objects within SPARQL.
- Decoding of SPARQL SELECT results to case class instances via automatic typeclass derivation.

SPARQL handling in `sparql-utils` is based on [Apache Jena](https://jena.apache.org). A separate module provides additional support for [OWL API](https://owlcs.github.io/owlapi/) types.

## Dependency configuration

```scala
libraryDependencies += "org.phenoscape" %% "sparql-utils" % "1.2"
```

For OWL API support:

```scala
libraryDependencies += "org.phenoscape" %% "sparql-utils-owlapi" % "1.2"
```

## Usage

### SPARQL string interpolator

```scala
scala> import org.apache.jena.rdf.model.ResourceFactory
import org.apache.jena.rdf.model.ResourceFactory

scala> import org.phenoscape.sparql.SPARQLInterpolation._
import org.phenoscape.sparql.SPARQLInterpolation._

scala> val foafName = ResourceFactory.createResource("http://xmlns.com/foaf/0.1/name")
foafName: org.apache.jena.rdf.model.Resource = http://xmlns.com/foaf/0.1/name

scala> val personName = "Ignatius J. Reilly"
personName: String = Ignatius J. Reilly

scala> val query =
     |   sparql"""
     | SELECT DISTINCT ?person
     | WHERE {
     |   ?person $foafName $personName .
     | }
     |   """
query: org.phenoscape.sparql.SPARQLInterpolation.QueryText =
QueryText(
SELECT DISTINCT ?person
WHERE {
  ?person <http://xmlns.com/foaf/0.1/name> "Ignatius J. Reilly" .
}
  )
```
Various built-in and Jena types are automatically supported for embedding, including 
`Resource`, `Property`, `Node`, `Literal`, `String`, `Boolean`, and numeric types. 
With the `owlapi` module you can also embed the various OWL entities (e.g. `OWLClass`) as URIs, 
as well as `OWLLiteral` instances. 

### SPARQL query result decoding
