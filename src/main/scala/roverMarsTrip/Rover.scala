package roverMarsTrip

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import roverMarsTrip.Expedition.Message
import utils.{Config, Logger}




object Directions {
  type Direction = Int

  val NORTH = 0
  val EAST = 1
  val SOUTH = 2
  val WEST = 3

  def turnLeft(facingDirection:Direction, nTurns: Int): Direction =
    Math.abs(facingDirection - nTurns) % 4

  def turnRigth(facingDirection:Direction, nTurns: Int): Direction =
    Math.abs(facingDirection + nTurns) % 4
}
case class Position(x: Int, y: Int) {
  import Directions._

  override def toString: String = s"X: $x, Y: $y"
  def move(vel: Int,facingDirection: Direction): Position =
    facingDirection match  {
      case NORTH => Position(x,y+vel)
      case EAST => Position(x+vel,y)
      case SOUTH => Position(x,y-vel)
      case WEST =>  Position(x-vel,y)
    }
}
case object Forward
case object Backward
case object Left
case object Right
case object Subscribe
case object StopTrip
case object Log
object Rover{
  val listOfMovement = List(Forward,Backward,Left,Right)

  def props(position: Position): Props = Props(new Rover(position))
}

class Rover(initialPosition: Position) extends Actor
  with ActorLogging
  with Logger
  with Config{
  var position = initialPosition
  var facingDirection = Directions.NORTH
  var subscribers = List.empty[ActorRef]
  var expeditionController = sender()
  val sizeMars = config.getInt(Config.MarsSize)
  private def publishPosition = s"[MARS-ROVER] Position ${position.toString} and " +
    s" Direction $facingDirection for rover"

  private def isValidPosition(position: Position): Boolean = {
    !(Math.abs(position.y) > sizeMars/2 || Math.abs(position.x) > sizeMars/2)
  }

  def receive = {
    case Log =>
      logger.info(s"Sending [${publishPosition}] message to Expedition actor ...")
      sender ! Message(publishPosition)
    case Subscribe =>
      subscribers = sender :: subscribers
    case Forward =>
      val newPosition = position.move(1,facingDirection)
      if (!isValidPosition(newPosition))
        logger.warn(s"[MARS-ROVER] Position not valid ${position.toString}" +
          s"after forward command , cancelling movement ....")
      else{
        logger.info(s"[MARS-ROVER] Rover moved from ${position.toString}" +
          s" to ${newPosition.toString} after Forward command")
        position = newPosition
      }
    case Backward =>
      facingDirection = Directions.turnLeft(facingDirection,2)
      val newPosition = position.move(1,facingDirection)
      if (!isValidPosition(newPosition))
        logger.warn(s"[MARS-ROVER] Position not valid ${position.toString}" +
          s"after Backward command , cancelling movement ....")
      else {
        logger.info(s"[MARS-ROVER] Rover moved from ${position.toString}" +
          s" to ${newPosition.toString} after Backward command")
        position = newPosition
      }
    case Left =>
      facingDirection = Directions.turnLeft(facingDirection,1)
      val newPosition = position.move(1,facingDirection)
      if (!isValidPosition(newPosition))
        logger.warn(s"[MARS-ROVER] Position not valid ${position.toString}" +
          s"after Left command , cancelling movement ....")
      else {
        logger.info(s"[MARS-ROVER] Rover moved from ${position.toString}" +
          s" to ${newPosition.toString} after Left command")
        position = newPosition
      }
    case Right =>
      facingDirection = Directions.turnRigth(facingDirection,1)
      val newPosition = position.move(1,facingDirection)
      if (!isValidPosition(newPosition))
        logger.warn(s"[MARS-ROVER] Position not valid ${position.toString}" +
          s"after Rigth command , cancelling movement ....")
      else{
        logger.info(s"[MARS-ROVER] Rover moved from ${position.toString}" +
          s" to ${newPosition.toString} after Right command")
        position = newPosition
      }
    case StopTrip =>
      self ! PoisonPill
  }
}


