package com.github.jasimvs.hotelsWebServer

import org.scalatra._
import org.slf4j.LoggerFactory

class HotelsServlet(hotelsService: HotelsService) extends HotelsServiceStack {

  private val logger = LoggerFactory.getLogger(getClass)

  get("/hotels") {
    val apiKey = params.get("apiKey")
    val sortBy = params.get("sortBy")
    val city = params.get("city")

    logger.debug(s"Received request $request with apikey $apiKey, city: $city and sortBy: $sortBy")
    apiKey match {
      case Some(key1) =>
        if (hotelsService.requestApproved(key1)) {
          val exceptionOrHotels = city.fold(hotelsService.getHotels(sortBy))(hotelsService.getHotelsByCity(_, sortBy))
          if (exceptionOrHotels.isRight) {
            contentType = "text/csv"
            serveHotels(exceptionOrHotels.right.get)
          } else {
            logger.debug("Cannot serve request as unable to load hotels.")
            halt(ActionResult(ResponseStatus(500, "Internal Server Error"), internalServerError, Map()))
          }
        } else {
          logger.debug("Rejecting request. Did not get approval from rate limiter.")
          halt(ActionResult(ResponseStatus(429, "Too Many Requests"), tooManyRequestsError, Map()))
        }
      case None =>
        logger.debug("Unauthorized request.")
        halt(ActionResult(ResponseStatus(403, "Forbidden"), forbiddenRequestError, Map()))
    }
  }

  private def serveHotels(hotels: Seq[Hotel]): StringBuilder =
    hotels.foldLeft(new StringBuilder(hotelsService.getHeaderRow))((out, hotel) => out.append(hotel.toString))
}
