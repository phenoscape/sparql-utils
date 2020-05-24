package org.phenoscape.sparql

import org.apache.jena.query.QuerySolution
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

object FromQuerySolutionOWL {

  private val factory = OWLManager.getOWLDataFactory

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): IRI = IRI.create(qs.getResource(variablePath).getURI)

  }

  implicit object ClassFromQuerySolution extends FromQuerySolution[OWLClass] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): OWLClass = factory.getOWLClass(
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath)
    )

  }

  implicit object ObjectPropertyFromQuerySolution extends FromQuerySolution[OWLObjectProperty] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): OWLObjectProperty = factory.getOWLObjectProperty(
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath)
    )

  }

  implicit object AnnotationPropertyFromQuerySolution extends FromQuerySolution[OWLAnnotationProperty] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): OWLAnnotationProperty = factory.getOWLAnnotationProperty(
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath)
    )

  }

  implicit object NamedIndividualFromQuerySolution extends FromQuerySolution[OWLNamedIndividual] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): OWLNamedIndividual = factory.getOWLNamedIndividual(
      IRIFromQuerySolution.fromQuerySolution(qs, variablePath)
    )

  }

  implicit object LiteralFromQuerySolution extends FromQuerySolution[OWLLiteral] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): OWLLiteral = {
      val literal = qs.getLiteral(variablePath)
      if (literal.getLanguage.nonEmpty) factory.getOWLLiteral(literal.getLexicalForm, literal.getLanguage)
      else if (literal.getDatatypeURI != null) factory.getOWLLiteral(literal.getLexicalForm, factory.getOWLDatatype(IRI.create(literal.getDatatypeURI)))
      else factory.getOWLLiteral(literal.getLexicalForm)
    }

  }

}
