package com.github.jasimvs

import org.scalatra._

class HotelsServlet extends HotelsServiceStack {

  get("/hotels/hotel") {
    val apiKey = params.get("apiKey")
    val sortBy = params.get("sortBy")
    val city = params.get("city")
    val hotels: Seq[Hotel] = HotelsService.getHotelsByCity(params("city"))

    contentType = "text/csv"
    hotels.foldLeft("CITY,HOTELID,ROOM,PRICE\n")((out, hotel) => out + hotel.toString + "\n")
  }

}
