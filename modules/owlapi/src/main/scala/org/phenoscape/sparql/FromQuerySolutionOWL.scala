package org.phenoscape.sparql

import org.apache.jena.query.QuerySolution
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._

object FromQuerySolutionOWL {

  private val factory = OWLManager.getOWLDataFactory

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): IRI = IRI.create(qs.getResource(prefix).getURI)

  }

  implicit object ClassFromQuerySolution extends FromQuerySolution[OWLClass] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): OWLClass = factory.getOWLClass(
      IRIFromQuerySolution.fromQuerySolution(qs, prefix)
    )

  }

  implicit object ObjectPropertyFromQuerySolution extends FromQuerySolution[OWLObjectProperty] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): OWLObjectProperty = factory.getOWLObjectProperty(
      IRIFromQuerySolution.fromQuerySolution(qs, prefix)
    )

  }

  implicit object AnnotationPropertyFromQuerySolution extends FromQuerySolution[OWLAnnotationProperty] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): OWLAnnotationProperty = factory.getOWLAnnotationProperty(
      IRIFromQuerySolution.fromQuerySolution(qs, prefix)
    )

  }

  implicit object NamedIndividualFromQuerySolution extends FromQuerySolution[OWLNamedIndividual] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): OWLNamedIndividual = factory.getOWLNamedIndividual(
      IRIFromQuerySolution.fromQuerySolution(qs, prefix)
    )

  }

  implicit object LiteralFromQuerySolution extends FromQuerySolution[OWLLiteral] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): OWLLiteral = {
      val literal = qs.getLiteral(prefix)
      if (literal.getLanguage.nonEmpty) factory.getOWLLiteral(literal.getLexicalForm, literal.getLanguage)
      else if (literal.getDatatypeURI != null) factory.getOWLLiteral(literal.getLexicalForm, factory.getOWLDatatype(IRI.create(literal.getDatatypeURI)))
      else factory.getOWLLiteral(literal.getLexicalForm)
    }

  }

}
