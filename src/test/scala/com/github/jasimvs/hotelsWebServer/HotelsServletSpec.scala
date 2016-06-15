package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class HotelsServletSpec extends ScalatraSpec { def is =
    "GET / on HotelsServlet"                    ^
      "should return status 200"                ! root200 ^
                                                p^
    "GET /hotels on HotelsServlet"              ^
      "should return status 429"                ! hotels ^
                                                p^
    "GET /hotels?apiKey=3e3 on HotelsServlet"   ^
      "should return status 200"                ! hotelsWithApikey ^
                                                p^
    "GET /hotels?apiKey=uuu&city=Amsterdam on HotelsServlet" ^
      "should return status 200"                ! hotelsWithCity ^
                                                p^
    "GET /hotels?apiKey=uuu&city=Amsterdam&sortBy=ASC on HotelsServlet" ^
      "should return status 200"                ! hotelsWithCityAndSortByAsc ^
                                                p^
    "GET /hotels?apiKey=uuu&city=Amsterdam&sortBy=DESC on HotelsServlet" ^
      "should return status 200"                ! hotelsWithCityAndSortByDesc
                                                end

  val conf: Config = ConfigFactory.load.getConfig("hotelsservice.app")
  val configService: ConfigService = new ConfigService(conf)
  val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
  val dataLoader: DataLoader = new CsvDataLoader()
  val hotelsService: HotelsService = new DefaultHotelsService(configService, rateLimiterService, dataLoader)
  val servlet = new HotelsServlet(hotelsService)
  addServlet(servlet, "/*")

  def root200 = get("/") {
    status must_== 200
  }

  def hotels = get("/hotels") {
    status must_== 429
  }

  def hotelsWithApikey = get("/hotels?apiKey=3e3") {
    status must_== 200
    body must_== "CITY,HOTELID,ROOM,PRICE\nBangkok,1,Deluxe,1000\nAmsterdam,2,Superior,2000\nAshburn,3,Sweet Suite,1300\nAmsterdam,4,Deluxe,2200\nAshburn,5,Sweet Suite,1200\nBangkok,6,Superior,2000\nAshburn,7,Deluxe,1600\nBangkok,8,Superior,2400\nAmsterdam,9,Sweet Suite,30000\nAshburn,10,Superior,1100\nBangkok,11,Deluxe,60\nAshburn,12,Deluxe,1800\nAmsterdam,13,Superior,1000\nBangkok,14,Sweet Suite,25000\nBangkok,15,Deluxe,900\nAshburn,16,Superior,800\nAshburn,17,Deluxe,2800\nBangkok,18,Sweet Suite,5300\nAshburn,19,Superior,1000\nAshburn,20,Superior,4444\nAshburn,21,Deluxe,7000\nAshburn,22,Sweet Suite,14000\nAmsterdam,23,Deluxe,5000\nAshburn,24,Superior,1400\nAshburn,25,Deluxe,1900\nAmsterdam,26,Deluxe,2300\n"
  }

  def hotelsWithCity = get("hotels?apiKey=uuu&city=Amsterdam") {
    status must_== 200
    body must_== "CITY,HOTELID,ROOM,PRICE\nAmsterdam,2,Superior,2000\nAmsterdam,4,Deluxe,2200\nAmsterdam,9,Sweet Suite,30000\nAmsterdam,13,Superior,1000\nAmsterdam,23,Deluxe,5000\nAmsterdam,26,Deluxe,2300\n"
  }

  def hotelsWithCityAndSortByAsc = get("hotels?apiKey=uuu&city=Amsterdam&sortBy=ASC") {
    status must_== 200
    body must_== "CITY,HOTELID,ROOM,PRICE\nAmsterdam,13,Superior,1000\nAmsterdam,2,Superior,2000\nAmsterdam,4,Deluxe,2200\nAmsterdam,26,Deluxe,2300\nAmsterdam,23,Deluxe,5000\nAmsterdam,9,Sweet Suite,30000\n"
  }

  def hotelsWithCityAndSortByDesc = get("hotels?apiKey=uuu&city=Amsterdam&sortBy=DESC") {
    status must_== 200
    body must_== "CITY,HOTELID,ROOM,PRICE\nAmsterdam,9,Sweet Suite,30000\nAmsterdam,23,Deluxe,5000\nAmsterdam,26,Deluxe,2300\nAmsterdam,4,Deluxe,2200\nAmsterdam,2,Superior,2000\nAmsterdam,13,Superior,1000\n"
  }
}

