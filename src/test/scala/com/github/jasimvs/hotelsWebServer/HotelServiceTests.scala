package com.github.jasimvs.hotelsWebServer

import org.scalatest.{Matchers, WordSpec}

/**
 * Created by jsulaiman on 6/14/2016.
 */
class HotelServiceTests extends WordSpec with Matchers {

  "HotelsService" should {
    " return correct header row for csv" in {
      HotelsService.getHeaderRow shouldBe "CITY,HOTELID,ROOM,PRICE\n"
    }
  }

//  "HotelsService" should {
//    " return all 26 hotels on quering for getHotels" in {
//      val hotels = HotelsService.getHotels().right.get
//      hotels.size shouldBe 26
//    }
//  }
//
//  "HotelsService" should {
//    "return hotels for Bangkok" in {
//      val hotels = HotelsService.getHotelsByCity("Bangkok").right.get
//      hotels.size shouldBe 7
//      hotels.filter(_.city == "Bangkok").size shouldBe 7
//    }
//  }
//
//  "HotelsService" should {
//    " return all 26 hotels, sorted by price ascending,on quering for getHotels with sortBy ASC" in {
//      val hotels = HotelsService.getHotels(Some("ASC")).right.get
//      hotels.size shouldBe 26
//      hotels(0).price shouldBe 60
//      hotels(25).price shouldBe 30000
//    }
//  }
//
//  "HotelsService" should {
//    " return all 26 hotels, sorted by price descending,on quering for getHotels with sortBy DESC" in {
//      val hotels = HotelsService.getHotels(Some("DESC")).right.get
//      hotels.size shouldBe 26
//      hotels(25).price shouldBe 60
//      hotels(0).price shouldBe 30000
//    }
//  }
//
//  "HotelsService" should {
//    " return all 26 hotels, sorted by price ascending,on quering for getHotels with sortBy ASC" in {
//      val hotels = HotelsService.getHotelsByCity("Bangkok", Some("ASC")).right.get
//      hotels.size shouldBe 7
//      hotels.filter(_.city == "Bangkok").size shouldBe 7
//      hotels(0).price shouldBe 60
//      hotels(6).price shouldBe 25000
//    }
//  }
//
//  "HotelsService" should {
//    " return all 26 hotels, sorted by price descending,on quering for getHotels with sortBy DESC" in {
//      val hotels = HotelsService.getHotelsByCity("Bangkok", Some("DESC")).right.get
//      hotels.size shouldBe 7
//      hotels.filter(_.city == "Bangkok").size shouldBe 7
//      hotels(6).price shouldBe 60
//      hotels(0).price shouldBe 25000
//    }
//  }
}
