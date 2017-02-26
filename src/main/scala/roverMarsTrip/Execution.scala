package roverMarsTrip
import akka.actor._
import akka.actor.{Actor, Props, Terminated}
import akka.event.LoggingReceive
import utils.{Config, Logger}


object Execution extends App
with Config
with Logger{
  logger.info("[EXECUTION]  Starting execution ...")
  val system = ActorSystem("example")
  val nRover = config.getInt(Config.nRover)
  logger.info(s"[EXECUTION] Expedition  $nRover Rovers is going to start!")

  system.actorOf(Expedition.props(nRover), "expedition-actor")
  }
