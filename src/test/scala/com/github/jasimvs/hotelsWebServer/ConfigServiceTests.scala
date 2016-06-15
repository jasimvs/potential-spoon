package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatest.{Matchers, WordSpec}

/**
 * Created by jsulaiman on 6/14/2016.
 */
class ConfigServiceTests extends WordSpec with Matchers {

  "ConfigService " should {
    " load values from application.conf" in {
      val conf: Config = ConfigFactory.load.getConfig("hotelsservice.app")
      val configService = new ConfigService(conf)

      configService.getSuspensionTime shouldBe 1
      configService.getTimeLimit shouldBe 11
      configService.getDataLoaderFile shouldBe "hoteldb.csv"
      configService.getRequestLimit("xyz") shouldBe 10
      configService.getRequestLimit("") shouldBe 100
      configService.getRequestLimit(null) shouldBe 100
    }
  }

}
