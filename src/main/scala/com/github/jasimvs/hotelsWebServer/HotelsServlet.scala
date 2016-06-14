package com.github.jasimvs.hotelsWebServer

import org.scalatra._
import org.slf4j.LoggerFactory

class HotelsServlet extends HotelsServiceStack {

  private val logger =  LoggerFactory.getLogger(getClass)

  get("/hotels") {
    val apiKey = params.get("apiKey")
    val sortBy = params.get("sortBy")
    val city = params.get("city")

    logger.debug(s"Received request $request with apikey $apiKey, city: $city and sortBy: $sortBy")
    if (apiKey.fold(false)(HotelsService.requestApproved(_))) {
      contentType = "text/csv"
      serveHotels(city.fold(HotelsService.getHotels(sortBy))(HotelsService.getHotelsByCity(_, sortBy)))
    } else {
      logger.debug("Rejecting request. Did not get approval from rate limiter.")
      halt(ActionResult(ResponseStatus(429, "Too Many Requests"), tooManyRequestsError , Map()))
    }
  }

  private def serveHotels(hotels: Seq[Hotel]): StringBuilder =
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
