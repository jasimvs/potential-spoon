package com.github.jasimvs

import java.util.concurrent.{TimeUnit, Semaphore}

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Await, Promise, Future}
import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */

case class Hotel(city: String, hotelId: Int, room: String, price: Int) {
  override def toString = s"$city,$hotelId,$room,$price"
}

case class RateLimiter(apiKey: String, maxRequests: Int, perTime: Int, suspensionTime: Int) {
  private val locker = this

  var availableTokens = maxRequests
  var suspended = false

  def  getToken(): Boolean = {
    locker.synchronized {
      if (availableTokens > 0) {
        availableTokens = availableTokens - 1
        true
      } else if(suspended) {
        false
      } else {
        suspended = true
        suspend
        false
      }
    }
  }

  def suspend()(implicit ec: ExecutionContext = ExecutionContext.global) =
    Future(Await.ready(Promise().future, Duration.apply(suspensionTime, TimeUnit.MINUTES)))
    .onComplete (u =>
      Try(locker.synchronized {
        suspended = false
      }))
  availableTokens = availableTokens + 1
}


object Domain {

  val hotels: Seq[Hotel] = loadHotels

  val CsvFirstRow = "CITY,HOTELID,ROOM,PRICE"

  def loadHotels: Seq[Hotel] = {
    //TODO load CSV
    Seq[Hotel](Hotel("Bangkok",1,"Deluxe",1000),
      Hotel("Amsterdam",2,"Superior",2000),
      Hotel("Ashburn",3,"Sweet Suite",1300),
      Hotel("Amsterdam",4,"Deluxe",2200),
      Hotel("Ashburn",5,"Sweet Suite",1200))
  }


}
