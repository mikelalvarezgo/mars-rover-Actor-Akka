package utils

import com.typesafe.config.ConfigFactory


trait Config {

  lazy val config = ConfigFactory.load("application.conf")

}

object Config {
  val MarsSize = "field.size"
  val nRover = "akka.actor.nRovers"
  val moveFrequency = "execution.frequency.random"
  val statusFrequency = "execution.frequency.status"
  val duration = "execution.duration"

}

