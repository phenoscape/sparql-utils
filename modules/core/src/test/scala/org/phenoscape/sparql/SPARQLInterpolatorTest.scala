package org.phenoscape.sparql

import org.apache.jena.graph.NodeFactory
import org.apache.jena.rdf.model.ResourceFactory
import org.phenoscape.sparql.SPARQLInterpolation._
import utest._

object SPARQLInterpolatorTest extends TestSuite {

  val tests: Tests = Tests {
    test("Jena objects should be formatted correctly in SPARQL") {
      val uri1 = ResourceFactory.createResource("http://example.org/1")
      val prop2 = ResourceFactory.createProperty("http://example.org/2")
      val uri3 = ResourceFactory.createResource("http://example.org/3")
      val uri4 = ResourceFactory.createResource("http://example.org/4")
      val string1 = ResourceFactory.createStringLiteral("this is string one")
      val string2 = "this is string 2"
      val int1 = ResourceFactory.createTypedLiteral(42)
      val int2 = 43
      val queryPart = sparql"$uri3 $prop2 $int2 ."
      val node1 = NodeFactory.createURI("http://example.org/node/1")
      val node2 = NodeFactory.createLiteral("à bientôt", "fr")
      val values = List(uri1, uri3, uri4).map(v => sparql"$v ").reduce(_ + _)
      val query =
        sparql"""
SELECT *
WHERE {
  VALUES ?x { $values }
  $uri1 $prop2 $string1 .
  $uri1 $prop2 $int1 .
  $queryPart
  $node1 ?p $string2 .
  ?s ?q $node2 .
}
"""
      val expected =
        """
SELECT *
WHERE {
  VALUES ?x { <http://example.org/1> <http://example.org/3> <http://example.org/4>  }
  <http://example.org/1> <http://example.org/2> "this is string one" .
  <http://example.org/1> <http://example.org/2> "42"^^<http://www.w3.org/2001/XMLSchema#int> .
  <http://example.org/3> <http://example.org/2> 43 .
  <http://example.org/node/1> ?p "this is string 2" .
  ?s ?q "à bientôt"@fr .
}
"""
      assert(query.text == expected)
      assert(query.toQuery.isSelectType)

      case class Person(name: String)
      val person = Person("Incitatus")
      // No typeclass instance for Person
      compileError("""sparql"$person"""")
    }
  }

}
