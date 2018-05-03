package com.pako.routee

import akka.actor.{ActorSystem, Props}
import com.pako.routee.Message.Work

object Main extends App {

  val system = ActorSystem.create("pako")

  val master = system.actorOf( Props[Master] )

  master ! Work()

  Thread.sleep(1000000)

}
