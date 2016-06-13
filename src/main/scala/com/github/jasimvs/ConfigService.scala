package com.github.jasimvs

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */
class ConfigService(conf: Config, requestLimits: Config) {

  val RootConfigString = "hotelsservice.app"
  val DefaultRequestLimitString = "defaultrequestlimit"
  val TimeLimitString = "limittime"
  val RequestLimitsString = "apikeyrequestlimits"
  val SuspensionTimeString = "suspensiontime"


  val DefaultRequestLimit = {
    if (isConfigMissing(conf, DefaultRequestLimitString)) 100
    else conf.getInt(DefaultRequestLimitString)
  }

  val getTimeLimit = {
    if (isConfigMissing(conf, TimeLimitString)) 10
    else conf.getInt(TimeLimitString)
  }

  val getSuspensionTime: Int = {
    if (isConfigMissing(conf, SuspensionTimeString)) 5
    else requestLimits.getInt(SuspensionTimeString)
  }

  def getRequestLimit(apiKey: String): Int = {
    if (isConfigMissing(requestLimits, apiKey)) DefaultRequestLimit
    else requestLimits.getInt(apiKey)
  }

  private def isConfigMissing(conf: Config, configName: String) = {
    Try(conf.getIsNull(configName)).isFailure
  }

}

object ConfigService {
  val RootConfigString = "hotelsservice.app"
  val DefaultRequestLimitString = "defaultrequestlimit"
  val TimeLimitString = "limittime"
  val RequestLimitsString = "apikeyrequestlimits"
  val SuspensionTimeString = "suspensionTime"


  def apply() = {
    val conf: Config = ConfigFactory.load.getConfig(RootConfigString)
    val requestLimits = conf.getConfig(RequestLimitsString)
    new ConfigService(conf, requestLimits)

  }
}