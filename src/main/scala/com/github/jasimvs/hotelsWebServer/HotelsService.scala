package com.github.jasimvs.hotelsWebServer

import org.slf4j.LoggerFactory


/**
 * Created by jsulaiman on 6/13/2016.
 */
object HotelsService {

  private val logger = LoggerFactory.getLogger(getClass)

  private lazy val configService = ConfigService()
  private lazy val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
  private lazy val hotelsDomain: Domain = Domain(CsvDatatLoader.loadHotels(configService.getCsvDataLoaderFile))

  def requestApproved(apikey: String): Boolean = rateLimiterService.requestApproved(apikey)

  def getHotels(sortBy: Option[String] = None): Either[Exception, Seq[Hotel]] = {
    logger.debug("Get all hotels")
    if (hotelsDomain.hotels.isRight)
      Right(sortHotelsByPrice(hotelsDomain.hotels.right.get, sortBy))
    else
      hotelsDomain.hotels
  }

  def getHotelsByCity(city: String, sortBy: Option[String] = None): Either[Exception, Seq[Hotel]] = {
    logger.debug(s"Get hotels by city $city")
    if (hotelsDomain.hotels.isRight)
      Right(sortHotelsByPrice(hotelsDomain.hotels.right.get.filter(_.city == city), sortBy))
    else
      hotelsDomain.hotels
  }

  //  def getHotelById(hotelId: Int): Option[Hotel] = {
  //    Domain.hotels.find(_.hotelId == hotelId)
  //  }
  //
  //  def getHotelsByRoom(room: String): Seq[Hotel] = {
  //    Domain.hotels.filter(_.room == room)
  //  }
  //
  //  def getHotelsByPrice(min: Int, max: Int): Seq[Hotel] = {
  //    Domain.hotels.filter(hotel => hotel.price >= min && hotel.price <= max)
  //  }

  def sortHotelsByPrice(hotels: Seq[Hotel], sortOrder: Option[String]): Seq[Hotel] = {
    logger.info(s"Sorting by $sortOrder")
    sortOrder match {
      case Some("ASC") => hotels.sortWith(sortByPriceAsc)
      case Some("DESC") => hotels.sortWith(sortByPriceDesc)
      case _ => hotels
    }
  }

  def getHeaderRow: String = hotelsDomain.CsvHeaderRow

  private def sortByPriceAsc: (Hotel, Hotel) => Boolean = (h1: Hotel, h2: Hotel) => h1.price < h2.price

  private def sortByPriceDesc: (Hotel, Hotel) => Boolean = (h1: Hotel, h2: Hotel) => h1.price > h2.price

}

