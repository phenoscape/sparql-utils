package org.phenoscape.sparql

import org.apache.jena.query.QuerySolutionMap
import org.apache.jena.rdf.model.ResourceFactory
import org.phenoscape.sparql.FromQuerySolution.mapSolution
import utest._

import scala.util.Try

object FromQuerySolutionTest extends TestSuite {

  val tests = Tests {
    "FromQuerySolution typeclasses should be derivable" - {
      val qs = new QuerySolutionMap()
      qs.add("name", ResourceFactory.createPlainLiteral("Aurelius"))
      qs.add("friend_name", ResourceFactory.createPlainLiteral("Tiberius"))
      val fromQS = implicitly[FromQuerySolution[Person]]
      assert(fromQS.fromQuerySolution(qs) == Person("Aurelius"))
      val func = mapSolution[PersonWithFriend]
      assert(func(qs) == PersonWithFriend("Aurelius", Person("Tiberius")))

      val fromQSOpt = implicitly[FromQuerySolution[PersonWithOptionalLastName]]
      val qs2 = new QuerySolutionMap()
      qs2.add("name", ResourceFactory.createPlainLiteral("Marcus"))
      qs2.add("lastName", null)
      assert(fromQSOpt.fromQuerySolution(qs2) == PersonWithOptionalLastName("Marcus", None))

      val qs3 = new QuerySolutionMap()
      qs3.add("name", ResourceFactory.createPlainLiteral("Marcus"))
      qs3.add("lastName", ResourceFactory.createPlainLiteral("Aurelius"))
      assert(mapSolution[PersonWithOptionalLastName].apply(qs3) == PersonWithOptionalLastName("Marcus", Some("Aurelius")))

      val qs4 = new QuerySolutionMap()
      qs4.add("name", ResourceFactory.createPlainLiteral("Marcus"))
      qs4.add("friend_person_name", ResourceFactory.createPlainLiteral("Tiberius"))
      qs4.add("friend_age", ResourceFactory.createTypedLiteral(42))
      assert(mapSolution[PersonWithOptionalFriend].apply(qs4) == PersonWithOptionalFriend("Marcus", Some(AgedPerson(Person("Tiberius"), 42))))

      val qs5 = new QuerySolutionMap()
      qs5.add("person_name", ResourceFactory.createPlainLiteral("Marcus"))
      qs5.add("age", ResourceFactory.createTypedLiteral(true))
      assert(Try(mapSolution[AgedPerson].apply(qs5)).isFailure)
    }
  }

  case class Person(name: String)

  case class PersonWithFriend(name: String, friend: Person)

  case class PersonWithOptionalLastName(name: String, lastName: Option[String])

  case class PersonWithOptionalFriend(name: String, friend: Option[AgedPerson])

  case class AgedPerson(person: Person, age: Int)

}
