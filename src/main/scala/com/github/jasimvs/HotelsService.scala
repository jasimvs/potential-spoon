package com.github.jasimvs

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Created by jsulaiman on 6/13/2016.
 */
object HotelsService {

  def getHotelById(hotelId: Int): Option[Hotel] = {
    Domain.hotels.find(_.hotelId == hotelId)
  }

  def getHotelsByCity(city: String): Seq[Hotel] = {
    Domain.hotels.filter(_.city == city)
  }

  def getHotelsByRoom(room: String): Seq[Hotel] = {
    Domain.hotels.filter(_.room == room)
  }

  def getHotelsByPrice(min: Int, max: Int): Seq[Hotel] = {
    Domain.hotels.filter(hotel => hotel.price >= min && hotel.price <= max)
  }

  def sortHotelsByPrice(hotels: Seq[Hotel], sortOrder: Option[String]): Seq[Hotel] = {
    sortOrder match {
      case Some("ASC") => hotels.sortWith(sortByPriceAsc)
      case Some("DESC") => hotels.sortWith(sortByPriceAsc)
      case _ => hotels
    }
  }

  def sortByPriceAsc = (h1: Hotel, h2: Hotel) => h1.price < h2.price

  def sortByPriceDesc = (h1: Hotel, h2: Hotel) => h1.price > h2.price

}

