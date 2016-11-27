package com.example

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import com.example.CalcActor._
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.util.Random

class CalcActorSpec  extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with Eventually with  BeforeAndAfterAll {
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(100, Millis))

  "CalcActor actor" must {
    val a = 6
    val b = 3
    val calcActor = TestActorRef[CalcActor](CalcActor.props)

    "should add two numbers" in {
//      fail("Not implemented")
      calcActor ! Divide(a,b)
      expectMsg(a/b)
    }

    "must substract " in {
      calcActor ! Subtract(a,b)
      expectMsg(a - b)
    }

    "must multiply two numbers " in {
      calcActor ! Multi(a, b)
      expectMsg(a * b)
    }

    "Clear message should set the last result to zero " in {
      calcActor.underlyingActor.state = a
      calcActor ! Clear
      calcActor.underlyingActor.state shouldEqual 0
      expectNoMsg()
    }

    "eventually should create file  " in {
      val magigNumber = Random.nextInt()
      calcActor ! LongUpdateState(magigNumber)

      eventually{
        calcActor.underlyingActor.state shouldEqual magigNumber
      }
    }

    "intercept arithmetic exception" in {
      intercept[ArithmeticException]{calcActor.receive(5/0)}
//      intercept[NullPointerException]{calcActor.receive(5/0)}
    }

  }

}
