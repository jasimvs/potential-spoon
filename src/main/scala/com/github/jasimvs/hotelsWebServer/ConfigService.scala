package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */
class ConfigService(conf: Config, requestLimits: Config) {

  private val DefaultRequestLimitString = "defaultrequestlimit"
  private val TimeLimitString = "limittime"
  private val SuspensionTimeString = "suspensiontime"

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
    else conf.getInt(SuspensionTimeString)
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
  private val RootConfigString = "hotelsservice.app"
  private val RequestLimitsString = "apikeyrequestlimits"

  def apply() = {
    val conf: Config = ConfigFactory.load.getConfig(RootConfigString)
    val requestLimits = conf.getConfig(RequestLimitsString)
    new ConfigService(conf, requestLimits)

  }
}