package com.pako.routee

import akka.actor.{Actor, AllDeadLetters, PoisonPill, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import com.pako.routee.Message.Work

object Message {
  case class Work()
}

class Worker extends Actor {

  println("Worker created!!!")

  self ! PoisonPill

  override def receive: Receive = {
    case w : Work => println("Doing some inmportant work!!!!")
    case _ => println("Unknow message")
  }
}

class WorkNotDeliveredActor extends Actor {
  override def receive: Receive = {
    case w: Work => println("Message not delivered!!!!!!!!!!")
    case a => println("Message not delivered!!!!!!!!!!" + a)
  }
}

class Master extends Actor {

  context.system.eventStream.subscribe(context.actorOf(Props[WorkNotDeliveredActor]), classOf[AllDeadLetters])

  var router = {
    val routees = Vector.fill(0) {
      val r = context.actorOf(Props[Worker])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }


  def receive = {
    case w: Work ⇒
      router.route(w, sender())
    case Terminated(a) ⇒
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
    case _ => println("Unknow message")
  }
}

