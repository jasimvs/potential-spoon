package com.github.jasimvs.hotelsWebServer

import org.scalatest.{Matchers, WordSpec}

/**
 * Created by jsulaiman on 6/14/2016.
 */
class ConfigServiceTests extends WordSpec with Matchers {

  "ConfigService " should {
    " load values from application.conf" in {
      val configService = ConfigService()
      configService.DefaultRequestLimit shouldBe 100
      configService.getSuspensionTime shouldBe 1
      configService.getTimeLimit shouldBe 11
      configService.getCsvDataLoaderFile shouldBe "/hoteldb.csv"
      configService.getRequestLimit("xyz") shouldBe 10
    }
  }

}
