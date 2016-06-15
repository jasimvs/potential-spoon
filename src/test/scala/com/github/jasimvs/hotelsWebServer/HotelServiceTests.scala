package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatest.{Matchers, WordSpec}

/**
 * Created by jsulaiman on 6/14/2016.
 */
class HotelServiceTests extends WordSpec with Matchers {

  val conf: Config = ConfigFactory.load.getConfig("hotelsservice.app")
  val configService: ConfigService = new ConfigService(conf)
  val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
  val dataLoader: DataLoader = new CsvDataLoader()

  val hotelsService: HotelsService = new DefaultHotelsService(configService, rateLimiterService, dataLoader)
  
  "HotelsService" should {
    " return correct header row for csv" in {
      hotelsService.getHeaderRow shouldBe "CITY,HOTELID,ROOM,PRICE\n"
    }
  }

  "HotelsService" should {
    " return all 26 hotels on quering for getHotels" in {
      val hotels = hotelsService.getHotels().right.get
      hotels.size shouldBe 26
    }
  }

  "HotelsService" should {
    "return 7 hotels for Bangkok" in {
      val hotels = hotelsService.getHotelsByCity("Bangkok").right.get
      hotels.size shouldBe 7
      hotels.filter(_.city == "Bangkok").size shouldBe 7
    }
  }

  "HotelsService" should {
    " return all 26 hotels, sorted by price ascending,on quering for getHotels with sortBy ASC" in {
      val hotels = hotelsService.getHotels(Some("ASC")).right.get
      hotels.size shouldBe 26
      hotels(0).price shouldBe 60
      hotels(25).price shouldBe 30000
    }
  }

  "HotelsService" should {
    " return all 26 hotels, sorted by price descending,on quering for getHotels with sortBy DESC" in {
      val hotels = hotelsService.getHotels(Some("DESC")).right.get
      hotels.size shouldBe 26
      hotels(25).price shouldBe 60
      hotels(0).price shouldBe 30000
    }
  }

  "HotelsService" should {
    " return 7 hotels, sorted by price ascending,on quering for getHotels with sortBy ASC" in {
      val hotels = hotelsService.getHotelsByCity("Bangkok", Some("ASC")).right.get
      hotels.size shouldBe 7
      hotels.filter(_.city == "Bangkok").size shouldBe 7
      hotels(0).price shouldBe 60
      hotels(6).price shouldBe 25000
    }
  }

  "HotelsService" should {
    " return 7 hotels, sorted by price descending,on quering for getHotels with sortBy DESC" in {
      val hotels = hotelsService.getHotelsByCity("Bangkok", Some("DESC")).right.get
      hotels.size shouldBe 7
      hotels.filter(_.city == "Bangkok").size shouldBe 7
      hotels(6).price shouldBe 60
      hotels(0).price shouldBe 25000
    }
  }
}
