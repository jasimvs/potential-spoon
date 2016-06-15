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

  def isRequestApproved(apikey: String): Boolean = {
    val rateLimiter: RateLimiter = apikey.intern.synchronized(rateLimiters.get(apikey)
      .fold(createNewRateLimiter(apikey))(rateLimiter => rateLimiter))
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

case class RateLimiter(apiKey: String, maxRequests: Int, perTimeInSecs: Int, suspensionTimeInMins: Int) {
  private val logger = LoggerFactory.getLogger(getClass)
  private val locker = this

  private var availableTokens = maxRequests
  private var lastRequestServedTime = System.currentTimeMillis()
  private var suspended = false

  require(maxRequests > 0 && perTimeInSecs > 0 && suspensionTimeInMins >= 0)

  def getToken(): Boolean = {
    logger.debug(s"get token from $availableTokens")
    if (suspended) {
      logger.debug(s"$apiKey suspended status: $suspended")
      false
    } else {
      val currentTime = System.currentTimeMillis()
      val timeSinceLastRequest = ((currentTime - lastRequestServedTime) / 1000).toInt
      val tokensAvailableSinceLastRequest = timeSinceLastRequest * maxRequests / perTimeInSecs
      locker.synchronized {
        availableTokens = availableTokens + tokensAvailableSinceLastRequest
        if (availableTokens < 1) {
          logger.debug(s"Not enough tokens for $apiKey.")
          suspended = true
          suspend(Duration(suspensionTimeInMins, TimeUnit.MINUTES))
          false
        } else {
          if (availableTokens > maxRequests) {
            availableTokens = maxRequests
          }
          availableTokens = availableTokens - 1
          lastRequestServedTime = currentTime
          logger.debug(s"remaining tokens for $apiKey: $availableTokens")
          true
        }
      }
    }
  }

  def suspend(duration: Duration)(implicit ec: ExecutionContext = ExecutionContext.global) =
    Future(Await.ready(Promise().future, duration))
      .onComplete(u => {
        logger.debug(s"Ending suspension for $apiKey")
        Try(locker.synchronized {
          suspended = false
        })
      })
}
