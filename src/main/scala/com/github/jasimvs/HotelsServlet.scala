package com.github.jasimvs

import org.scalatra._

class HotelsServlet extends HotelsServiceStack {

  val error = <html>
    <head>
      <title>Too Many Requests</title>
    </head>
    <body>
      <h1>Too Many Requests</h1>
      <p>Please wait for a while before sending any more requests.</p>
    </body>
  </html>

  get("/hotels/hotel") {
    val apiKey = params.get("apiKey")
    if (apiKey.fold(false)(RateLimiterService.requestApproved(_))) {
      val sortBy = params.get("sortBy")
      val city = params.get("city")
      val hotels: Seq[Hotel] = city.fold(HotelsService.getHotels())(HotelsService.getHotelsByCity(_))
      val sortedHotels = HotelsService.sortHotelsByPrice(hotels, sortBy)
      contentType = "text/csv"
      sortedHotels.foldLeft("CITY,HOTELID,ROOM,PRICE\n")((out, hotel) => out + hotel.toString + "\n")
    } else {
      halt(ActionResult(ResponseStatus(429, "Too Many Requests"), error , Map()))
    }
  }

}
