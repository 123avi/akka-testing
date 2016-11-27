package com.example

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class EchoActorSpec  extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {
  import com.example.EchoActor._

  import concurrent.duration._

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An Echo actor" must {
    val echoActor = TestActorRef[EchoActor](EchoActor.props)
    "send back a message" in {

//      echoActor.tell(Message("Hello"),testActor) //without implicit
      echoActor  ! Message("Hello")
      expectMsg("Hello")
    }

    "deliver message to pipe " ignore {
      val test = TestProbe()
      echoActor ! PipeMessage("hello",test.ref)
      test.expectMsg("hello")
    }

    "filter ints " in {
      val list = (1 to 10).toList
      val f:Int => Boolean =  i => i % 2 == 0
      echoActor ! Filter(list, f )
      val ints = receiveWhile(){ //blocking
        case Filtered(i) if f(i) => i

      }
      ints.toList shouldEqual list.filter(f)
    }

    "respond to list with separate values " in {
      val list = List(1,2,"a","b",3,"c")
      echoActor ! list
      var countInts = 0
      var countStrings = 0
      def condition = (countInts+countStrings) >= list.size
      fishForMessage(5 seconds, "get int messages"){
        case _:Int =>
          countInts += 1
          condition
        case _:String =>
          countStrings += 1
          condition
        case _ => false

      }
    }

    "return the same number of messages when getting some list " in{
      val list = List(1,2,"a","b",3,"c")
      echoActor ! list
      receiveN(list.size)
      }

    }






}
