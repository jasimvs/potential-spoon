package com.github.jasimvs.hotelsWebServer

/**
 * Created by jsulaiman on 6/13/2016.
 */

case class Hotel(city: String, hotelId: Int, room: String, price: Int) {
  override def toString = s"$city,$hotelId,$room,$price\n"
}

case class Domain(hotels: Either[Exception, Seq[Hotel]]) {

  val CsvHeaderRow = "CITY,HOTELID,ROOM,PRICE\n"
}
