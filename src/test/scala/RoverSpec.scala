import akka.actor.ActorSystem
import roverMarsTrip._


import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class RoverSpec extends TestKit(ActorSystem("MarsRoverControllerTest"))
  with WordSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll() {
    system.shutdown()
  }
  "Rover" should {
    val roverTest:TestActorRef[Rover] = TestActorRef(Rover.props(Position(0,0)))

    "have initial position 0,0" in {
        roverTest.underlyingActor.position must equal(Position(0,0))
    }
    "have initial direction North" in {
      roverTest.underlyingActor.facingDirection must equal(Directions.NORTH)
    }
    "have initial position 1,0 and direction North after forward" in {
      roverTest ! Forward
      roverTest.underlyingActor.position must equal(Position(0,1))
      roverTest.underlyingActor.facingDirection must equal(Directions.NORTH)
    }
    "have initial position 0,0 and direction South  after backward" in {
      roverTest ! Backward
      roverTest.underlyingActor.position must equal(Position(0,0))
      roverTest.underlyingActor.facingDirection must equal(Directions.SOUTH)
    }
    "have initial position -1,0 and direction West  after rigth command" in {
      roverTest ! Right
      roverTest.underlyingActor.position must equal(Position(-1,0))
      roverTest.underlyingActor.facingDirection must equal(Directions.WEST)
    }
    "have initial position 0,0 and direction   after backward command" in {
      roverTest ! Backward
      roverTest.underlyingActor.position must equal(Position(0,0))
      roverTest.underlyingActor.facingDirection must equal(Directions.EAST)
    }
  }
  }
