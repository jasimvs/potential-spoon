package com.github.jasimvs.hotelsWebServer

import java.util.concurrent.TimeUnit

import org.slf4j.LoggerFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Promise, Await, Future, ExecutionContext}
import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */
class RateLimiterService(configService: ConfigService) {

  private val logger = LoggerFactory.getLogger(getClass)

  private val rateLimiters: scala.collection.mutable.Map[String, RateLimiter] = scala.collection.mutable.Map[String, RateLimiter]()

  def requestApproved(apikey: String): Boolean = {
    val rateLimiter: RateLimiter = rateLimiters.get(apikey)
      .fold(createNewRateLimiter(apikey))(rateLimiter => rateLimiter)
    rateLimiter.getToken()
  }

  private def createNewRateLimiter(apikey: String): RateLimiter = {
    val reqLimit = configService.getRequestLimit(apikey)
    val timeLimit = configService.getTimeLimit
    val suspensionTime = configService.getSuspensionTime
    val rateLimiter = RateLimiter(apikey, reqLimit, timeLimit, suspensionTime)
    rateLimiters.put(apikey, rateLimiter)
    rateLimiter
  }
}

case class RateLimiter(apiKey: String, maxRequests: Int, perTime: Int, suspensionTime: Int) {
  private val logger = LoggerFactory.getLogger(getClass)

  private val locker = this
  private var availableTokens = maxRequests
  private var lastRequestServedTime = System.currentTimeMillis()


  def getToken(): Boolean = {
    logger.debug(s"get token from $availableTokens")
    val currentTime = System.currentTimeMillis()
    val timeSinceLastRequest = ((currentTime - lastRequestServedTime) / 1000).toInt

    val maxRequestRate = maxRequests / perTime
    val tokensAvailableSinceLastRequest = timeSinceLastRequest * maxRequestRate
    locker.synchronized {
      availableTokens = availableTokens + tokensAvailableSinceLastRequest
      if (availableTokens < 1) {
        logger.debug("Not enough tokens.")
        false
      } else {
        if (availableTokens > maxRequests) {
          availableTokens = maxRequests
        }
        availableTokens = availableTokens - 1
        lastRequestServedTime = currentTime
        logger.debug(s"remaining: $availableTokens")
        true
      }
    }
  }
}
