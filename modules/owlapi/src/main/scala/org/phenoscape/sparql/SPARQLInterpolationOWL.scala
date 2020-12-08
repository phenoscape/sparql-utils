package org.phenoscape.sparql

import contextual._
import org.apache.jena.datatypes.TypeMapper
import org.apache.jena.query.ParameterizedSparqlString
import org.phenoscape.sparql.SPARQLInterpolation.SPARQLInterpolator.SPARQLContext
import org.phenoscape.sparql.SPARQLInterpolation._
import org.semanticweb.owlapi.model._

object SPARQLInterpolationOWL {

  implicit val embedIRIInSPARQL: SPARQLEmbedder[IRI] = SPARQLInterpolator.embed[IRI](Case(SPARQLContext, SPARQLContext) { iri =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(iri.toString)
    pss.toString
  })

  implicit val embedOWLClassInSPARQL: SPARQLEmbedder[OWLClass] = SPARQLInterpolator.embed[OWLClass](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLObjectPropertyInSPARQL: SPARQLEmbedder[OWLObjectProperty] = SPARQLInterpolator.embed[OWLObjectProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLAnnotationPropertyInSPARQL: SPARQLEmbedder[OWLAnnotationProperty] = SPARQLInterpolator.embed[OWLAnnotationProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLDataPropertyInSPARQL: SPARQLEmbedder[OWLDataProperty] = SPARQLInterpolator.embed[OWLDataProperty](Case(SPARQLContext, SPARQLContext) { obj =>
    val pss = new ParameterizedSparqlString()
    pss.appendIri(obj.getIRI.toString)
    pss.toString
  })

  implicit val embedOWLLiteralInSPARQL: SPARQLEmbedder[OWLLiteral] = SPARQLInterpolator.embed[OWLLiteral](Case(SPARQLContext, SPARQLContext) { literal =>
    val pss = new ParameterizedSparqlString()
    if (literal.hasLang) pss.appendLiteral(literal.getLiteral, literal.getLang)
    else if (literal.isRDFPlainLiteral) pss.appendLiteral(literal.getLiteral)
    else pss.appendLiteral(literal.getLiteral, TypeMapper.getInstance.getSafeTypeByName(literal.getDatatype.getIRI.toString))
    pss.toString
  })

}
