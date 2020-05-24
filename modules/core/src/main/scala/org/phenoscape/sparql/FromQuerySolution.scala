package org.phenoscape.sparql

import magnolia._
import org.apache.jena.iri.{IRI, IRIFactory}
import org.apache.jena.query.QuerySolution

import scala.collection.JavaConverters._
import scala.language.experimental.macros

trait FromQuerySolution[T] {

  def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): T

}

object FromQuerySolution {

  type Typeclass[T] = FromQuerySolution[T]

  def combine[T](caseClass: CaseClass[FromQuerySolution, T]): FromQuerySolution[T] = new FromQuerySolution[T] {
    def fromQuerySolution(qs: QuerySolution, variablePath: String): T = caseClass.construct { p =>
      val separator = if (variablePath.isEmpty) "" else "_"
      p.typeclass.fromQuerySolution(qs, s"$variablePath$separator${p.label}")
    }
  }

  def dispatch[T](ctx: SealedTrait[FromQuerySolution, T]): FromQuerySolution[T] = ???

  implicit def gen[T]: FromQuerySolution[T] = macro Magnolia.gen[T]

  implicit def optionFromQuerySolution[T: FromQuerySolution]: FromQuerySolution[Option[T]] = new FromQuerySolution[Option[T]] {
    override def fromQuerySolution(qs: QuerySolution, variablePath: String): Option[T] = {
      val tFQS = implicitly[FromQuerySolution[T]]
      if (qs.varNames.asScala.exists(name => name.startsWith(variablePath) && qs.get(name) != null))
        Some(tFQS.fromQuerySolution(qs, variablePath))
      else None
    }
  }

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): IRI = IRIFactory.iriImplementation.construct(qs.getResource(variablePath).getURI)

  }

  implicit object StringFromQuerySolution extends FromQuerySolution[String] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): String = qs.getLiteral(variablePath).getLexicalForm

  }

  implicit object IntFromQuerySolution extends FromQuerySolution[Int] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Int = qs.getLiteral(variablePath).getInt

  }

  implicit object LongFromQuerySolution extends FromQuerySolution[Long] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Long = qs.getLiteral(variablePath).getLong

  }

  implicit object FloatFromQuerySolution extends FromQuerySolution[Float] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Float = qs.getLiteral(variablePath).getFloat

  }

  implicit object DoubleFromQuerySolution extends FromQuerySolution[Double] {

    def fromQuerySolution(qs: QuerySolution, variablePath: String = ""): Double = qs.getLiteral(variablePath).getDouble

  }

  def mapSolution[T: FromQuerySolution]: QuerySolution => T = {
    val fqs = implicitly[FromQuerySolution[T]]
    qs => fqs.fromQuerySolution(qs)
  }

}