package roverMarsTrip

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import akka.actor.{Actor, ActorLogging, ActorRef, Kill, Props}
import akka.event.LoggingReceive
import akka.routing._
import Rover._
import utils.{Config, Logger}

object Expedition {

  case object Abort

  case object Start

  case object RandomMove

  case object  Status

  case class  Message(text: String)

  def props(nRovers: Int): Props = Props(new Expedition(nRovers))
}

class Expedition(
  nRovers: Int) extends Actor
  with ActorLogging
  with Config
  with Logger{

  import ExecutionContext.Implicits.global
  import Expedition._
  val router: ActorRef =
    context.actorOf(RoundRobinPool(nRovers).props(Rover.props(Position(0,0))), "router1")
  var moves = List(Rover.listOfMovement)
  val moveFrequency = config.getInt(Config.moveFrequency)
  val statusFrequency = config.getInt(Config.statusFrequency)
  val duration = config.getInt(Config.duration)
  context.system.scheduler.schedule(moveFrequency second, moveFrequency second, self, RandomMove)
  context.system.scheduler.schedule(0 second, duration second, self, Status)
  context.system.scheduler.schedule(0 second, duration second, self, Status)



  override def receive = LoggingReceive {
    case Message(text) =>
      logger.info(s"[EXPEDITION-ACTOR] Received message [$text] " +
        s"from Rover ${sender.path.name}")
    case Abort =>
      router ! Broadcast(Kill)

    case Status =>
      router ! Broadcast(Log)
    case RandomMove =>
        val r = scala.util.Random
        val randomNumber = r.nextInt(3)
        router ! listOfMovement(randomNumber)

    case Start =>
  }
}

