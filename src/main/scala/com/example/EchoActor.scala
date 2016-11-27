package com.example

import akka.actor.{Actor, ActorRef, Props}

object EchoActor{
  def props = Props(new EchoActor)

  case class PipeMessage(text: String, pipeTo: ActorRef)
  case class Message(text: String)
  case class Filter(list:List[Int], f:Int => Boolean)
  case class Filtered(i:Int)

}

class EchoActor extends Actor {
  import EchoActor._

  override def receive: Receive = {
    case PipeMessage(msg,pipe) => pipe ! msg
    case Message(msg) => sender() ! msg
    case Filter(l,f) => l.filter(f).foreach{ i => sender ! Filtered(i)}
    case l:List[_] => ???

  }

}
