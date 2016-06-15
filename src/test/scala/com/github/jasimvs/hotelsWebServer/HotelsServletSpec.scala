package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
//class HotelsServletSpec extends ScalatraSpec { def is =
//  "GET / on HotelsServlet"                     ^
//    "should return status 200"                  ! root200^
//                                                end
//
//  val conf: Config = ConfigFactory.load.getConfig("hotelsservice.app")
//  val configService: ConfigService = new ConfigService(conf)
//  val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
//  val dataLoader: DataLoader = new CsvDataLoader()
//  implicit val hotelsService: HotelsService = new DefaultHotelsService(configService, rateLimiterService, dataLoader)
//  addServlet(classOf[HotelsServlet], "/*")
//
//  def root200 = get("/") {
//    status must_== 200
//  }
//
//  def hotels200 = get("/hotels") {
//    status must_== 200
//  }
//
//}
