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

  private val logger =  LoggerFactory.getLogger(getClass)

  private val rateLimiters: scala.collection.mutable.Map[String, RateLimiter] = scala.collection.mutable.Map[String, RateLimiter]()

//  private lazy val configService = ConfigService()

  def requestApproved(apikey: String) = {
    logger.info("requestApproved")
    val rateLimiter: RateLimiter = rateLimiters.get(apikey)
      .fold(createNewRateLimiter(apikey))(rateLimiter => rateLimiter)
    rateLimiter.getToken()
  }

  private def createNewRateLimiter(apikey: String): RateLimiter = {
    val reqLimit = configService.getRequestLimit(apikey)
    val timeLimit = configService.getTimeLimit
    val suspensionTime = configService.getSuspensionTime
    val rl = RateLimiter(apikey, reqLimit, timeLimit, suspensionTime)
    rateLimiters.put(apikey, rl)
    rl
  }
}

case class RateLimiter(apiKey: String, maxRequests: Int, perTime: Int, suspensionTime: Int) {
  private val logger = LoggerFactory.getLogger(getClass)

  private val locker = this
  private var availableTokens = maxRequests
  private var suspended = false
  private var lastRequestServedTime = System.currentTimeMillis()


  def  getToken(): Boolean = {
    logger.debug(s"get token from $availableTokens")
    val currentTime = System.currentTimeMillis()
    val timeSinceLastRequest = ((currentTime - lastRequestServedTime) / 1000).toInt

    val maxRequestRate = maxRequests / perTime
    val currentRequestRate = timeSinceLastRequest * maxRequestRate
    locker.synchronized {
      availableTokens = availableTokens + currentRequestRate
      if (availableTokens > maxRequests) {
        availableTokens = maxRequests - 1
        lastRequestServedTime = currentTime
        logger.debug(s"remaining: $availableTokens")
        true
      } else if(availableTokens < 1) {
        logger.debug("Not enough tokens.")
        false
      } else {
        availableTokens = availableTokens - 1
        logger.debug(s"remaining: $availableTokens")
        lastRequestServedTime = currentTime
        true
      }
    }
  }

  def returnToken()(implicit ec: ExecutionContext = ExecutionContext.global) =
    Future(Await.ready(Promise().future, Duration.apply(maxRequests / perTime, TimeUnit.SECONDS)))
      .onComplete(u => {
        logger.debug("Returning 1 token")
        Try(locker.synchronized {
          availableTokens = availableTokens + 1
        })
      })

  def suspend(implicit ec: ExecutionContext = ExecutionContext.global) =
    Future(Await.ready(Promise().future, Duration.apply(suspensionTime, TimeUnit.MINUTES)))
      .onComplete(u => {
        logger.debug("Returning tokens after suspension")
        Try(locker.synchronized {
          suspended = false
          availableTokens = maxRequests
        })
      })
}
