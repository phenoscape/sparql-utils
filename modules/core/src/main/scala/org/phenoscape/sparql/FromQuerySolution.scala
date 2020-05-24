package org.phenoscape.sparql

import magnolia._
import org.apache.jena.iri.{IRI, IRIFactory}
import org.apache.jena.query.QuerySolution

import scala.collection.JavaConverters._
import scala.language.experimental.macros

trait FromQuerySolution[T] {

  def fromQuerySolution(qs: QuerySolution, prefix: String = ""): T

}

object FromQuerySolution {

  type Typeclass[T] = FromQuerySolution[T]

  def combine[T](caseClass: CaseClass[FromQuerySolution, T]): FromQuerySolution[T] = new FromQuerySolution[T] {
    def fromQuerySolution(qs: QuerySolution, prefix: String): T = caseClass.construct { p =>
      val separator = if (prefix.isEmpty) "" else "_"
      p.typeclass.fromQuerySolution(qs, s"$prefix$separator${p.label}")
    }
  }

  def dispatch[T](ctx: SealedTrait[FromQuerySolution, T]): FromQuerySolution[T] = ???

  implicit def gen[T]: FromQuerySolution[T] = macro Magnolia.gen[T]

  implicit def optionFromQuerySolution[T: FromQuerySolution]: FromQuerySolution[Option[T]] = new FromQuerySolution[Option[T]] {
    override def fromQuerySolution(qs: QuerySolution, prefix: String): Option[T] = {
      val tFQS = implicitly[FromQuerySolution[T]]
      if (qs.varNames.asScala.exists(name => name.startsWith(prefix) && qs.get(name) != null))
        Some(tFQS.fromQuerySolution(qs, prefix))
      else None
    }
  }

  implicit object IRIFromQuerySolution extends FromQuerySolution[IRI] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): IRI = IRIFactory.iriImplementation.construct(qs.getResource(prefix).getURI)

  }

  implicit object StringFromQuerySolution extends FromQuerySolution[String] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): String = qs.getLiteral(prefix).getLexicalForm

  }

  implicit object IntFromQuerySolution extends FromQuerySolution[Int] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): Int = qs.getLiteral(prefix).getInt

  }

  implicit object LongFromQuerySolution extends FromQuerySolution[Long] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): Long = qs.getLiteral(prefix).getLong

  }

  implicit object FloatFromQuerySolution extends FromQuerySolution[Float] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): Float = qs.getLiteral(prefix).getFloat

  }

  implicit object DoubleFromQuerySolution extends FromQuerySolution[Double] {

    def fromQuerySolution(qs: QuerySolution, prefix: String = ""): Double = qs.getLiteral(prefix).getDouble

  }

  def mapSolution[T: FromQuerySolution]: QuerySolution => T = {
    val fqs = implicitly[FromQuerySolution[T]]
    qs => fqs.fromQuerySolution(qs)
  }

}