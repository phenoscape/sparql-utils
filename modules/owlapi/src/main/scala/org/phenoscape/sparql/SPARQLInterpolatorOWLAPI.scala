package org.phenoscape.sparql

import contextual._
import org.apache.jena.query.ParameterizedSparqlString
import org.phenoscape.sparql.SPARQLInterpolation.SPARQLInterpolator.SPARQLContext
import org.phenoscape.sparql.SPARQLInterpolation._
import org.semanticweb.owlapi.model.{IRI, OWLAnnotationProperty, OWLClass, OWLObjectProperty}

object SPARQLInterpolatorOWLAPI {

  implicit val embedIRIInSPARQL = SPARQLInterpolator.embed[IRI](Case(SPARQLContext, SPARQLContext) { iri =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(iri.toString)
    pss.toString
  })

  implicit val embedOWLClassInSPARQL = SPARQLInterpolator.embed[OWLClass](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLObjectPropertyInSPARQL = SPARQLInterpolator.embed[OWLObjectProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLAnnotationPropertyInSPARQL = SPARQLInterpolator.embed[OWLAnnotationProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

}
