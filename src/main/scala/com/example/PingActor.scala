package com.example

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import com.example.exceptions.{BarException, FooException}

class PingActor extends Actor with ActorLogging {
  import PingActor._
  override def supervisorStrategy = OneForOneStrategy() {
    case _: ArithmeticException => Resume
    case _: FooException => Restart
    case _: BarException => Stop
    case _ => Restart
  }
  var counter = 0
  val pongActor = context.actorOf(PongActor.props, "pongActor")

  def receive = {
  	case Initialize => 
	    log.info("In PingActor - starting ping-pong")
  	  pongActor ! PingMessage("ping")	
  	case PongActor.PongMessage(text) =>
  	  log.info("In PingActor - received message: {}", text)
  	  counter += 1
  	  if (counter == 3) context.system.shutdown()
  	  else sender() ! PingMessage("ping")
  }	
}

object PingActor {
  val props = Props[PingActor]
  case object Initialize
  case class PingMessage(text: String)
}