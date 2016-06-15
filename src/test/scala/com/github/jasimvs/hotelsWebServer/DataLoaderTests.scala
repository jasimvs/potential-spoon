package com.github.jasimvs.hotelsWebServer

import org.scalatest.{Matchers, WordSpec}

/**
 * Created by jsulaiman on 6/14/2016.
 */
class DataLoaderTests extends WordSpec with Matchers {

  val dataLoder: DataLoader = new CsvDataLoader()

  "DataLoader "  should {
    " be able to load a good csv file" in {
      val exceptionOrHotels = dataLoder.loadHotels("hoteldb.csv")
      exceptionOrHotels.isRight shouldBe true
      val hotels: Seq[Hotel] = exceptionOrHotels.right.get
      hotels.size shouldBe 26
    }
  }

  "DataLoader "  should {
    " be able to load a good csv file even if it has incorrect rows" in {
      val exceptionOrHotels = dataLoder.loadHotels("hoteldb-incorrect.csv")
      exceptionOrHotels.isRight shouldBe true
      val hotels: Seq[Hotel] = exceptionOrHotels.right.get
      hotels.size shouldBe 1
    }
  }

  "DataLoader "  should {
    " return exception (left in Either) for file that does not exist" in {
      val exceptionOrHotels = dataLoder.loadHotels("nohoteldb.csv")
      exceptionOrHotels.isLeft shouldBe true
      val exception = exceptionOrHotels.left.get
      exception.isInstanceOf[NullPointerException] shouldBe true
    }
  }

  "DataLoader "  should {
    " return exception (left in Either) if null is passed instead of filename" in {
      val exceptionOrHotels = dataLoder.loadHotels(null)
      exceptionOrHotels.isLeft shouldBe true
      val exception = exceptionOrHotels.left.get
      exception.isInstanceOf[NullPointerException] shouldBe true
    }
  }

}
