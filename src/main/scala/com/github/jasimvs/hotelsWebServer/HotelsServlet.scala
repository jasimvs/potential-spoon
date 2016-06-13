package com.github.jasimvs.hotelsWebServer

import org.scalatra._
import org.slf4j.LoggerFactory

class HotelsServlet extends HotelsServiceStack {

  private val logger =  LoggerFactory.getLogger(getClass)

  get("/hotels") {
    val apiKey = params.get("apiKey")
    logger.debug(s"Received request $request with apikey $apiKey")
    if (apiKey.fold(false)(HotelsService.requestApproved(_))) {
      val sortBy = params.get("sortBy")
      val city = params.get("city")
      logger.debug(s"Query params city: $city and sortBy: $sortBy")

      val hotels: Seq[Hotel] = city.fold(HotelsService.getHotels())(HotelsService.getHotelsByCity(_))
      val sortedHotels = HotelsService.sortHotelsByPrice(hotels, sortBy)
      contentType = "text/csv"

      serveHotels(sortedHotels)
    } else {
      logger.debug("Rejecting request. Did not get approval from rate limiter.")
      halt(ActionResult(ResponseStatus(429, "Too Many Requests"), tooManyRequestsError , Map()))
    }
  }

  private def serveHotels(hotels: Seq[Hotel]) =
    hotels.foldLeft(new StringBuilder(HotelsService.getHeaderRow))((out, hotel) => out.append(hotel.toString))

  private val tooManyRequestsError = <html>
    <head>
      <title>Too Many Requests</title>
    </head>
    <body>
      <h1>Too Many Requests</h1>
      <p>Please wait for a while before sending any more requests.</p>
    </body>
  </html>

}
