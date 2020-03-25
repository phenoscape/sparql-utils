package org.phenoscape.sparql

import contextual._
import org.apache.jena.graph.Node
import org.apache.jena.query.{ParameterizedSparqlString, Query, QueryFactory}
import org.apache.jena.rdf.model.{Property, Resource, Literal => JenaLiteral}

object SPARQLInterpolation {

  final case class QueryText(text: String) {

    def toQuery: Query = QueryFactory.create(text)

  }

  object SPARQLInterpolator extends Interpolator {

    override type Input = String

    override type Output = QueryText

    sealed trait SPARQLContextType extends Context

    object SPARQLContext extends SPARQLContextType

    override type ContextType = SPARQLContextType

    def contextualize(interpolation: StaticInterpolation): Seq[ContextType] =
      interpolation.holes.map(_ => SPARQLContext)

    def evaluate(interpolation: RuntimeInterpolation): QueryText = {
      val buf = new StringBuffer()
      interpolation.parts.foreach {
        case Literal(_, value)      => buf.append(value)
        case Substitution(_, value) => buf.append(value)
      }
      QueryText(buf.toString)
    }

  }

  import SPARQLInterpolator.SPARQLContext

  implicit val embedQueryTextInSPARQL = SPARQLInterpolator.embed[QueryText](
    Case(SPARQLContext, SPARQLContext)(_.text))

  implicit val embedStringInSPARQL = SPARQLInterpolator.embed[String](
    Case(SPARQLContext, SPARQLContext)(str => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(str)
      pss.toString
    }))

  implicit val embedIntInSPARQL = SPARQLInterpolator.embed[Int](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedLongInSPARQL = SPARQLInterpolator.embed[Long](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedFloatInSPARQL = SPARQLInterpolator.embed[Float](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedDoubleInSPARQL = SPARQLInterpolator.embed[Double](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedBooleanInSPARQL = SPARQLInterpolator.embed[Boolean](
    Case(SPARQLContext, SPARQLContext)(bool => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(bool)
      pss.toString
    }))

  implicit val embedJenaNodeInSPARQL = SPARQLInterpolator.embed[Node](
    Case(SPARQLContext, SPARQLContext)(node => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(node)
      pss.toString
    }))

  implicit val embedJenaResourceInSPARQL = SPARQLInterpolator.embed[Resource](
    Case(SPARQLContext, SPARQLContext)(resource => {
      if (resource.isAnon) throw new IllegalArgumentException("Blank nodes are not supported in SPARQL interpolations.")
      val pss = new ParameterizedSparqlString()
      pss.appendNode(resource.asNode)
      pss.toString
    }))

  implicit val embedJenaPropertyInSPARQL = SPARQLInterpolator.embed[Property](
    Case(SPARQLContext, SPARQLContext)(property => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(property.asNode)
      pss.toString
    }))

  implicit val embedJenaLiteralInSPARQL = SPARQLInterpolator.embed[JenaLiteral](
    Case(SPARQLContext, SPARQLContext)(literal => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(literal.asNode)
      pss.toString
    }))

  implicit class SPARQLStringContext(val sc: StringContext) {

    val sparql = Prefix(SPARQLInterpolator, sc)

  }

}