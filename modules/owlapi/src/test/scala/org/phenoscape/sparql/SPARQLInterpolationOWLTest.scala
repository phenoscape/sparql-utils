package org.phenoscape.sparql

import org.phenoscape.sparql.SPARQLInterpolation._
import org.phenoscape.sparql.SPARQLInterpolationOWL._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import utest._

object SPARQLInterpolationOWLTest extends TestSuite {

  val factory = OWLManager.getOWLDataFactory

  val tests = Tests {
    "OWL objects should be formatted correctly in SPARQL" - {
      val class1 = factory.getOWLClass(IRI.create("http://example.org/1"))
      val prop2 = factory.getOWLObjectProperty(IRI.create("http://example.org/2"))
      val class3 = factory.getOWLClass(IRI.create("http://example.org/3"))
      val class4 = factory.getOWLClass(IRI.create("http://example.org/4"))
      val string1 = factory.getOWLLiteral("this is string one")
      val string2 = "this is string 2"
      val int1 = factory.getOWLLiteral(42)
      val int2 = 43
      val queryPart = sparql"$class3 $prop2 $int2 ."
      val iri1 = IRI.create("http://example.org/node/1")
      val langLiteral = factory.getOWLLiteral("à bientôt", "fr")
      val values = List(class1, class3, class4).map(v => sparql"$v ").reduce(_ + _)
      val query =
        sparql"""
SELECT *
WHERE {
  VALUES ?x { $values }
  $class1 $prop2 $string1 .
  $class1 $prop2 $int1 .
  $queryPart
  $iri1 ?p $string2 .
  ?s ?q $langLiteral .
}
"""
      val expected =
        """
SELECT *
WHERE {
  VALUES ?x { <http://example.org/1> <http://example.org/3> <http://example.org/4>  }
  <http://example.org/1> <http://example.org/2> "this is string one" .
  <http://example.org/1> <http://example.org/2> 42 .
  <http://example.org/3> <http://example.org/2> 43 .
  <http://example.org/node/1> ?p "this is string 2" .
  ?s ?q "à bientôt"@fr .
}
"""
      assert(query.text == expected)
      assert(query.toQuery.isSelectType)
    }
  }

}
