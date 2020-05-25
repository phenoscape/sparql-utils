package org.phenoscape.sparql

import magnolia._
import org.apache.jena.ext.xerces.util.URI
import org.apache.jena.iri.{IRI, IRIFactory}
import org.apache.jena.query.QuerySolution
import org.apache.jena.rdf.model.{Literal, RDFNode, Resource}

import scala.collection.JavaConverters._
import scala.language.experimental.macros
import scala.util.{Failure, Success, Try}

trait FromQuerySolution[T] {

  def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[T]

  private final def checkNull[V](value: V, variablePath: String = ""): Try[V] =
    if (value != null) Success(value)
    else Failure(new IllegalArgumentException(s"No variable binding for '$variablePath''"))

  final def getValue(qs: QuerySolution, variablePath: String = ""): Try[RDFNode] =
    checkNull(qs.get(variablePath), variablePath)

  final def getLiteral(qs: QuerySolution, variablePath: String = ""): Try[Literal] =
    Try(qs.getLiteral(variablePath))
      .recoverWith { case e => Failure(new IllegalArgumentException(s"Variable binding for '$variablePath' is not a literal", e)) }
      .flatMap(v => checkNull(v, variablePath))

  final def getResource(qs: QuerySolution, variablePath: String = ""): Try[Resource] =
    Try(qs.getResource(variablePath))
      .recoverWith { case e => Failure(new IllegalArgumentException(s"Variable binding for '$variablePath' is not a resource", e)) }
      .flatMap(v => checkNull(v, variablePath))

}

object FromQuerySolution {

  type Typeclass[T] = FromQuerySolution[T]

  def combine[T](caseClass: CaseClass[FromQuerySolution, T]): FromQuerySolution[T] = new FromQuerySolution[T] {
    def fromQuerySolution(qs: QuerySolution, variablePath: String): Try[T] = caseClass.constructMonadic { p =>
      val separator = if (variablePath.isEmpty) "" else "_"
      p.typeclass.fromQuerySolution(qs, s"$variablePath$separator${p.label}")
    }
  }

  def dispatch[T](ctx: SealedTrait[FromQuerySolution, T]): FromQuerySolution[T] = ???

  implicit def gen[T]: FromQuerySolution[T] = macro Magnolia.gen[T]

  implicit def optionFromQuerySolution[T: FromQuerySolution]: FromQuerySolution[Option[T]] = new FromQuerySolution[Option[T]] {
    override def fromQuerySolution(qs: QuerySolution, variablePath: String): Try[Option[T]] = {
      val tFQS = implicitly[FromQuerySolution[T]]
      if (qs.varNames.asScala.exists(name => name.startsWith(variablePath) && qs.get(name) != null))
        tFQS.fromQuerySolution(qs, variablePath).map(v => Some(v))
      else Success(None)
    }
  }

  implicit object RDFNodeFromQuerySolution extends FromQuerySolution[RDFNode] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[RDFNode] = getValue(qs, variablePath)

  }

  implicit object URIFromQuerySolution extends FromQuerySolution[URI] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[URI] =
      getResource(qs, variablePath).map(r => new URI(r.getURI))

  }

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[IRI] =
      getResource(qs, variablePath).map(r => IRIFactory.iriImplementation.construct(r.getURI))

  }

  implicit object StringFromQuerySolution extends FromQuerySolution[String] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[String] =
      getLiteral(qs, variablePath).map(_.getLexicalForm)

  }

  implicit object IntFromQuerySolution extends FromQuerySolution[Int] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[Int] = getLiteral(qs, variablePath).map(_.getInt)

  }

  implicit object LongFromQuerySolution extends FromQuerySolution[Long] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[Long] = getLiteral(qs, variablePath).map(_.getLong)

  }

  implicit object FloatFromQuerySolution extends FromQuerySolution[Float] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[Float] = getLiteral(qs, variablePath).map(_.getFloat)

  }

  implicit object DoubleFromQuerySolution extends FromQuerySolution[Double] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Try[Double] = getLiteral(qs, variablePath).map(_.getDouble)

  }

  def mapSolution[T: FromQuerySolution]: QuerySolution => Try[T] = {
    val fqs = implicitly[FromQuerySolution[T]]
    qs => fqs.fromQuerySolution(qs)
  }

}