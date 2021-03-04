package org.phenoscape.sparql

import contextual._
import org.apache.jena.graph.Node
import org.apache.jena.query.{ParameterizedSparqlString, Query, QueryFactory}
import org.apache.jena.rdf.model.{Property, Resource, Literal => JenaLiteral}
import org.apache.jena.sparql.path.Path

import language.experimental.macros

object SPARQLInterpolation {

  type SPARQLEmbedder[T] = Embedder[(SPARQLInterpolator.SPARQLContext.type, SPARQLInterpolator.SPARQLContext.type), T, String, SPARQLInterpolation.SPARQLInterpolator.type]

  final case class QueryText(text: String) {

    def +(that: QueryText): QueryText = QueryText(this.text + that.text)

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

  implicit val embedQueryTextInSPARQL: SPARQLEmbedder[QueryText] = SPARQLInterpolator.embed[QueryText](
    Case(SPARQLContext, SPARQLContext)(_.text))

  implicit val embedStringInSPARQL: SPARQLEmbedder[String] = SPARQLInterpolator.embed[String](
    Case(SPARQLContext, SPARQLContext)(str => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(str)
      pss.toString
    }))

  implicit val embedIntInSPARQL: SPARQLEmbedder[Int] = SPARQLInterpolator.embed[Int](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedLongInSPARQL: SPARQLEmbedder[Long] = SPARQLInterpolator.embed[Long](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedFloatInSPARQL: SPARQLEmbedder[Float] = SPARQLInterpolator.embed[Float](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedDoubleInSPARQL: SPARQLEmbedder[Double] = SPARQLInterpolator.embed[Double](
    Case(SPARQLContext, SPARQLContext)(num => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(num)
      pss.toString
    }))

  implicit val embedBooleanInSPARQL: SPARQLEmbedder[Boolean] = SPARQLInterpolator.embed[Boolean](
    Case(SPARQLContext, SPARQLContext)(bool => {
      val pss = new ParameterizedSparqlString()
      pss.appendLiteral(bool)
      pss.toString
    }))

  implicit val embedJenaNodeInSPARQL: SPARQLEmbedder[Node] = SPARQLInterpolator.embed[Node](
    Case(SPARQLContext, SPARQLContext)(node => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(node)
      pss.toString
    }))

  implicit val embedJenaPropertyPathInSPARQL: SPARQLEmbedder[Path] = SPARQLInterpolator.embed[Path](
    Case(SPARQLContext, SPARQLContext)(path => {
      val pss = new ParameterizedSparqlString()
      pss.append(path)
      pss.toString
    }))

  implicit val embedJenaResourceInSPARQL: SPARQLEmbedder[Resource] = SPARQLInterpolator.embed[Resource](
    Case(SPARQLContext, SPARQLContext)(resource => {
      if (resource.isAnon) throw new IllegalArgumentException("Blank nodes are not supported in SPARQL interpolations.")
      val pss = new ParameterizedSparqlString()
      pss.appendNode(resource.asNode)
      pss.toString
    }))

  implicit val embedJenaPropertyInSPARQL: SPARQLEmbedder[Property] = SPARQLInterpolator.embed[Property](
    Case(SPARQLContext, SPARQLContext)(property => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(property.asNode)
      pss.toString
    }))

  implicit val embedJenaLiteralInSPARQL: SPARQLEmbedder[JenaLiteral] = SPARQLInterpolator.embed[JenaLiteral](
    Case(SPARQLContext, SPARQLContext)(literal => {
      val pss = new ParameterizedSparqlString()
      pss.appendNode(literal.asNode)
      pss.toString
    }))

  implicit class SPARQLStringContext(val sc: StringContext) {

    val sparql: Prefix[String, QueryText, SPARQLInterpolator.SPARQLContextType, SPARQLInterpolation.SPARQLInterpolator.type] = Prefix(SPARQLInterpolator, sc)

  }

}
