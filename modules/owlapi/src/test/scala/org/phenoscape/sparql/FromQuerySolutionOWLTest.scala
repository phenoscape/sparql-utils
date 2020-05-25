package org.phenoscape.sparql

import org.apache.jena.query.QuerySolutionMap
import org.apache.jena.rdf.model.ResourceFactory
import org.phenoscape.sparql.FromQuerySolution.mapSolution
import org.phenoscape.sparql.FromQuerySolutionOWL._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLClass, OWLLiteral}
import org.semanticweb.owlapi.vocab.OWL2Datatype
import utest._

import scala.language.higherKinds
import scala.util.Success

object FromQuerySolutionOWLTest extends TestSuite {

  private val factory = OWLManager.getOWLDataFactory

  val tests: Tests = Tests {
    test("FromQuerySolution OWL typeclasses should be derivable") {
      val qs1 = new QuerySolutionMap()
      qs1.add("id", ResourceFactory.createResource("http://example.org/marcus_aurelius"))
      qs1.add("name", ResourceFactory.createPlainLiteral("Marcus"))
      assert(mapSolution[Person].apply(qs1) ==
        Success(Person(IRI.create("http://example.org/marcus_aurelius"), factory.getOWLLiteral("Marcus")))
      )

      val qs2 = new QuerySolutionMap()
      qs2.add("cls", ResourceFactory.createResource("http://example.org/marcus_aurelius"))
      qs2.add("age", ResourceFactory.createTypedLiteral(42))
      assert(mapSolution[PersonWithAge].apply(qs2) ==
        Success(PersonWithAge(factory.getOWLClass(IRI.create("http://example.org/marcus_aurelius")), factory.getOWLLiteral("42", OWL2Datatype.XSD_INT.getDatatype(factory))))
      )
    }
  }

  case class Person(id: IRI, name: OWLLiteral)

  case class PersonWithAge(cls: OWLClass, age: OWLLiteral)

}
