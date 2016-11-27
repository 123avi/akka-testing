package com.example

import java.nio.file.{Files, Paths}

import akka.actor.{Actor, Props}
import com.example.CalcActor.Multi

import scala.concurrent.{Future, blocking, future}

object CalcActor{
  def props = Props[CalcActor]

  case class Divide(numerator:Int, denominator:Int)
  case class Multi(num:Int, multplier:Int)
  case class Add(first:Int, second:Int)
  case class Subtract(first:Int, second:Int)
  case class LongUpdateState(n:Int)
  case object Clear
}

class CalcActor extends Actor {
  import CalcActor._
  implicit val ec = context.system.dispatcher
  var state = 0

  override def receive: Receive = {
    case Divide(a, b) =>
      state = a/b
      sender ! state
    case Multi(a, b) =>
      state = a*b
      sender ! state
    case Add(a, b) => state = a+b
      sender ! state
    case Subtract(a, b) => state = a-b
      sender ! state
    case Clear =>
      state = 0
    case LongUpdateState(n) =>
      Future {
        blocking {
          Thread.sleep(1000)
          state = n
        }
      }
  }

}
