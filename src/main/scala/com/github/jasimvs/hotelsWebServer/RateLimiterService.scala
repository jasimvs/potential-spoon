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

  def  getToken(): Boolean = {
    logger.debug(s"get token from $availableTokens")
    locker.synchronized {
      if (availableTokens > 0) {
        availableTokens = availableTokens - 1
        logger.debug(s"remaining: $availableTokens")
        true
      } else if(suspended) {
        false
      } else {
        suspended = true
        suspend
        false
      }
    }
  }

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
