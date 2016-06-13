package com.github.jasimvs.hotelsWebServer

import org.slf4j.LoggerFactory


/**
 * Created by jsulaiman on 6/13/2016.
 */
object HotelsService {

  private val logger =  LoggerFactory.getLogger(getClass)

  private lazy val configService = ConfigService()
  private val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
  private val hotelsDomain: Domain =  Domain(CsvDatatLoader.loadHotels(configService.getCsvDataLoaderFile).right.getOrElse(Seq[Hotel]()))

  def requestApproved(apikey: String) = rateLimiterService.requestApproved(apikey)

  def getHotels() = {
    logger.debug("Get all hotels")
    hotelsDomain.hotels
  }

  def getHotelsByCity(city: String): Seq[Hotel] = {
    logger.debug(s"Get hotels by city $city")
    hotelsDomain.hotels.filter(_.city == city)
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
    logger.debug(s"Sorting by $sortOrder")
    sortOrder match {
      case Some("ASC") => hotels.sortWith(sortByPriceAsc)
      case Some("DESC") => hotels.sortWith(sortByPriceAsc)
      case _ => hotels
    }
  }

  def getHeaderRow = hotelsDomain.CsvHeaderRow

  private def sortByPriceAsc = (h1: Hotel, h2: Hotel) => h1.price < h2.price

  private def sortByPriceDesc = (h1: Hotel, h2: Hotel) => h1.price > h2.price

}

