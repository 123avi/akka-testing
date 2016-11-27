package com.example

import akka.actor.{ActorSystem, Terminated}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.example.PongActor.DivideRequest
import com.example.exceptions.{BarException, FooException}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class PingPongActorSpec extends TestKit(ActorSystem("MySpec")) with TestBase{


  "A Ping actor" must {
    "send back a ping on a pong" in {
      val pingActor = system.actorOf(PingActor.props)
      pingActor ! PongActor.PongMessage("pong")
      expectMsg(PingActor.PingMessage("ping"))
    }

  }

  "A Pong actor" must {
    "send back a pong on a ping" in {
      val pongActor = system.actorOf(PongActor.props)
      pongActor ! PingActor.PingMessage("ping")
      expectMsg(PongActor.PongMessage("pong"))
    }
  }

  "Supervisor strategy" must{

    "The Chuck Noriss test (Chuck Noriss can divide by zero)- " in {

      val supervisor = TestActorRef[PingActor](PingActor.props)
//      val supervisor = system.actorOf(PingActor.props)
      val pong = supervisor.underlyingActor.pongActor
      pong ! DivideRequest(9,3)
      expectMsg(3)
      pong ! DivideRequest(9,0)
      pong ! "get"
      expectMsg(3)

    }

    "restart actor on Foo exception " in {
      val supervisor = TestActorRef[PingActor](PingActor.props)
      val pong = supervisor.underlyingActor.pongActor
      pong ! 10
      pong ! "get"
      expectMsg(10)
      pong ! FooException("Foo")
      pong ! "get"
      expectMsg(0)
    }

    "Stop on Boo exception" in {
      val supervisor = TestActorRef[PingActor](PingActor.props)
      val supervisor2 = system.actorOf(PingActor.props)
      val pong = supervisor.underlyingActor.pongActor
      val tester = TestProbe()
      tester.watch(pong)
      pong ! 6
      pong ! "get"
      expectMsg(6)
      pong ! BarException("Boo")

      tester.expectTerminated(pong)

      //      tester.expectMsgPF() {
      //        case Terminated(p) if p == pong => ()
      //      }
    }
  }
}
