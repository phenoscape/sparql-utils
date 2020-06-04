[![Build Status](https://travis-ci.com/phenoscape/sparql-utils.svg?branch=master)](https://travis-ci.com/phenoscape/sparql-utils)

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

The `sparql""` interpolator produces a `QueryText` object. These can be concatenated using `+`. 
A `QueryText` can be embedded using the interpolator; in this case no escaping happens, allowing queries to 
be built from fragments.

```scala
scala> val people = List(ResourceFactory.createResource("http://example.org/Homer"), ResourceFactory.createResource("http://example.org/Marge"), ResourceFactory.createResource("http://example.org/Bart"))
people: List[org.apache.jena.rdf.model.Resource] = List(http://example.org/Homer, http://example.org/Marge, http://example.org/Bart)

scala> val values = people.map(p => sparql"$p ").reduce(_ + _)
values: org.phenoscape.sparql.SPARQLInterpolation.QueryText = QueryText(<http://example.org/Homer> <http://example.org/Marge> <http://example.org/Bart> )

scala> sparql"""
     | SELECT ?name
     | WHERE {
     |   VALUES ?person { $values }
     |   ?person $foafName ?name .
     | }
     | """
res0: org.phenoscape.sparql.SPARQLInterpolation.QueryText =
QueryText(
SELECT ?name
WHERE {
  VALUES ?person { <http://example.org/Homer> <http://example.org/Marge> <http://example.org/Bart>  }
  ?person <http://xmlns.com/foaf/0.1/name> ?name .
}
)
```

You can convert the `QueryText` to a Jena `Query` (may throw a `QueryParseException`):

```scala
scala> res0.toQuery
res1: org.apache.jena.query.Query =
SELECT  ?name
WHERE
  { VALUES ?person { <http://example.org/Homer> <http://example.org/Marge> <http://example.org/Bart> }
    ?person  <http://xmlns.com/foaf/0.1/name>  ?name
  }
```

### SPARQL query result decoding
