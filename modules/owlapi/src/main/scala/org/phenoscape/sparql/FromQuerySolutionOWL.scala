package org.phenoscape.sparql

import org.apache.jena.query.QuerySolution
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

import scala.util.Try

object FromQuerySolutionOWL {

  private val factory = OWLManager.getOWLDataFactory

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[IRI] =
      getResource(qs, variablePath).map(r => IRI.create(r.getURI))

  }

  implicit object ClassFromQuerySolution extends FromQuerySolution[OWLClass] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[OWLClass] =
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath).map(factory.getOWLClass)

  }

  implicit object ObjectPropertyFromQuerySolution extends FromQuerySolution[OWLObjectProperty] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[OWLObjectProperty] =
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath).map(factory.getOWLObjectProperty)

  }

  implicit object AnnotationPropertyFromQuerySolution extends FromQuerySolution[OWLAnnotationProperty] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[OWLAnnotationProperty] =
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath).map(factory.getOWLAnnotationProperty)

  }

  implicit object NamedIndividualFromQuerySolution extends FromQuerySolution[OWLNamedIndividual] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[OWLNamedIndividual] =
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath).map(factory.getOWLNamedIndividual)

  }

  implicit object LiteralFromQuerySolution extends FromQuerySolution[OWLLiteral] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[OWLLiteral] = {
      getLiteral(qs, variablePath).map { literal =>
        if (literal.getLanguage.nonEmpty) factory.getOWLLiteral(literal.getLexicalForm, literal.getLanguage)
        else if (literal.getDatatypeURI != null) factory.getOWLLiteral(literal.getLexicalForm, factory.getOWLDatatype(IRI.create(literal.getDatatypeURI)))
        else factory.getOWLLiteral(literal.getLexicalForm)
      }

    }

  }

}
