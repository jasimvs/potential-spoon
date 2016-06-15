package com.github.jasimvs.hotelsWebServer

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

/**
 * Created by jsulaiman on 6/13/2016.
 */
class ConfigService(rootConf: Config) {

  private val RequestLimitsString = "apikeyrequestlimits"
  private val DefaultRequestLimitString = "defaultrequestlimit"
  private val TimeLimitString = "limittime"
  private val SuspensionTimeString = "suspensiontime"
  private val CsvDataFileString = "csvdatafile"
  private val requestLimits = rootConf.getConfig(RequestLimitsString)

  private val DefaultRequestLimit: Int = {
    val value = if (isConfigMissing(rootConf, DefaultRequestLimitString)) 100
    else rootConf.getInt(DefaultRequestLimitString)
    if (value > 0) value
    else 100
  }

  val getTimeLimit: Int = {
    val value = if (isConfigMissing(rootConf, TimeLimitString)) 10
    else rootConf.getInt(TimeLimitString)
    if (value > 0) value
    else 10
  }

  val getSuspensionTime: Int = {
    val value = if (isConfigMissing(rootConf, SuspensionTimeString)) 5
    else rootConf.getInt(SuspensionTimeString)
    if (value >= 0) value
    else 5
  }

  val getDataLoaderFile: String = {
    if (isConfigMissing(rootConf, CsvDataFileString)) "hoteldb.csv"
    else rootConf.getString(CsvDataFileString)
  }

  def getRequestLimit(apiKey: String): Int = {
    if (apiKey == null || apiKey.length == 0 || isConfigMissing(requestLimits, apiKey)) DefaultRequestLimit
    else requestLimits.getInt(apiKey)
  }

  private def isConfigMissing(conf: Config, configName: String) = {
    Try(conf.getIsNull(configName)).isFailure
  }
}
