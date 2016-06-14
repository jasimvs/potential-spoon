package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */
class ConfigService(rootConf: Config, requestLimits: Config) {

  private val DefaultRequestLimitString = "defaultrequestlimit"
  private val TimeLimitString = "limittime"
  private val SuspensionTimeString = "suspensiontime"
  private val CsvDataFileString = "csvdatafile"

  val DefaultRequestLimit: Int = {
    if (isConfigMissing(rootConf, DefaultRequestLimitString))
      100
    else
      rootConf.getInt(DefaultRequestLimitString)
  }

  val getTimeLimit: Int = {
    if (isConfigMissing(rootConf, TimeLimitString)) 10
    else rootConf.getInt(TimeLimitString)
  }

  val getSuspensionTime: Int = {
    if (isConfigMissing(rootConf, SuspensionTimeString)) 5
    else rootConf.getInt(SuspensionTimeString)
  }

  val getCsvDataLoaderFile: String = {
    if (isConfigMissing(rootConf, CsvDataFileString)) "/hoteldb.csv"
    else rootConf.getString(CsvDataFileString)
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