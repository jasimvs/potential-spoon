package com.github.jasimvs

import org.apache.commons.lang3.mutable.Mutable
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

/**
 * Created by jsulaiman on 6/13/2016.
 */
object RateLimiterService {

  val logger =  LoggerFactory.getLogger(getClass)

  private val rateLimiters: scala.collection.mutable.Map[String, RateLimiter] = scala.collection.mutable.Map[String, RateLimiter]()

  private lazy val configService = ConfigService()

  def requestApproved(apikey: String) = {
    logger.info("requestApproved")
    val rateLimiter: RateLimiter = rateLimiters.get(apikey)
      .fold(createNewRateLimiter(apikey))(rateLimiter => rateLimiter)
    rateLimiter.getToken()
  }

  def createNewRateLimiter(apikey: String): RateLimiter = {
    val reqLimit = configService.getRequestLimit(apikey)
    val timeLimit = configService.getTimeLimit
    val suspensionTime = configService.getSuspensionTime
    val rl = RateLimiter(apikey, reqLimit, timeLimit, suspensionTime)
    rateLimiters.put(apikey, rl)
    rl
  }

  //ConfigService.getRequestLimit()


}
