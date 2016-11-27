package com.example

import akka.actor.{Actor, ActorLogging, Props}

class PongActor extends Actor with ActorLogging {
  import PongActor._
  var res = 0

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.warning(s"Restart due to $message reason: $reason")
    res = 0
  }
  override def postRestart(reason: Throwable): Unit = {
    log.warning(s"Restart due to $reason")
    res = 0
  }



  def receive = {
  	case PingActor.PingMessage(text) => 
  	  log.info("In PongActor - received message: {}", text)
  	  sender() ! PongMessage("pong")
    case DivideRequest(n,d) =>
      res =  n/d
      sender() ! res
    case e:Exception =>
      throw e
    case "get" =>
      sender() ! res
    case i:Int =>
      res = i
  }	
}

object PongActor {
  val props = Props[PongActor]
  case class PongMessage(text: String)
  case class DivideRequest(numerator:Int, denominator:Int)
}
