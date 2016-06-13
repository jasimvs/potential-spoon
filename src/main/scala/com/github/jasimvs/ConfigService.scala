package com.github.jasimvs

import com.typesafe.config.ConfigFactory

/**
 * Created by jsulaiman on 6/13/2016.
 */
object ConfigService {

  val RootConfigString = "hotelsservice.app"
  val DefaultRequestLimitString = "defaultrequestlimit"
  val TimeLimitString = "limittime"
  val RequestLimitsString = "apikeyrequestlimits"
  val SuspensionTimeString = "suspensionTime"

  val conf = ConfigFactory.load.getConfig(RootConfigString)
  val requestLimits = conf.getConfig(RequestLimitsString)
  val timeLimits = conf.getConfig(TimeLimitString)

  val DefaultRequestLimit = {
    if (conf.getIsNull(DefaultRequestLimitString)) 100
    else conf.getInt(DefaultRequestLimitString)
  }

  val DefaultTimeLimit = {
    if (conf.getIsNull(TimeLimitString)) 10
    else conf.getInt(TimeLimitString)
  }

  def getRequestLimit(apiKey: String): Int = {
    if (requestLimits.getIsNull(apiKey)) DefaultRequestLimit
    else requestLimits.getInt(apiKey)
  }

  def getSuspensionTime = {
    if (conf.getIsNull(SuspensionTimeString)) 5
    else requestLimits.getInt(SuspensionTimeString)
  }

}
