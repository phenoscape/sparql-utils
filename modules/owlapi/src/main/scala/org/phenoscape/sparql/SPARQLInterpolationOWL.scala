package org.phenoscape.sparql

import contextual._
import org.apache.jena.datatypes.{RDFDatatype, TypeMapper}
import org.apache.jena.graph.NodeFactory
import org.apache.jena.query.ParameterizedSparqlString
import org.apache.jena.rdf.model.ResourceFactory
import org.phenoscape.sparql.SPARQLInterpolation.SPARQLInterpolator.SPARQLContext
import org.phenoscape.sparql.SPARQLInterpolation._
import org.semanticweb.owlapi.model.{IRI, OWLAnnotationProperty, OWLClass, OWLDataProperty, OWLLiteral, OWLObjectProperty}

object SPARQLInterpolationOWL {

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

  implicit val embedOWLDataPropertyInSPARQL = SPARQLInterpolator.embed[OWLDataProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLLiteralInSPARQL = SPARQLInterpolator.embed[OWLLiteral](Case(SPARQLContext, SPARQLContext) { literal =>
    val pss = new ParameterizedSparqlString()
    if (literal.hasLang) pss.appendLiteral(literal.getLiteral, literal.getLang)
    else if (literal.isRDFPlainLiteral) pss.appendLiteral(literal.getLiteral)
    else pss.appendLiteral(literal.getLiteral, TypeMapper.getInstance.getSafeTypeByName(literal.getDatatype.getIRI.toString))
    pss.toString
  })

}
