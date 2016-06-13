package com.github.jasimvs.hotelsWebServer

/**
 * Created by jsulaiman on 6/13/2016.
 */

case class Hotel(city: String, hotelId: Int, room: String, price: Int) {
  override def toString = s"$city,$hotelId,$room,$price\n"
}

object Domain {

  val hotels: Seq[Hotel] = loadHotels

  val CsvHeaderRow = "CITY,HOTELID,ROOM,PRICE\n"

  def loadHotels: Seq[Hotel] = {
    //TODO load CSV
    Seq[Hotel](Hotel("Bangkok",1,"Deluxe",1000),
      Hotel("Amsterdam",2,"Superior",2000),
      Hotel("Ashburn",3,"Sweet Suite",1300),
      Hotel("Amsterdam",4,"Deluxe",2200),
      Hotel("Ashburn",5,"Sweet Suite",1200))
  }


}
