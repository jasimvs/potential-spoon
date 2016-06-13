package com.github.jasimvs.hotelsWebServer

import java.io.{FileNotFoundException, IOException, FileReader, File}

import com.opencsv.CSVReader
import org.slf4j.LoggerFactory

/**
 * Created by jsulaiman on 6/13/2016.
 */
trait DataLoader {
  def loadHotels(fileName: String): Either[Exception, Seq[Hotel]]
}

object CsvDatatLoader extends DataLoader {
  private val logger =  LoggerFactory.getLogger(getClass)

  override def loadHotels(fileName: String): Either[Exception, Seq[Hotel]] = {
    var hotels: scala.collection.mutable.ListBuffer[Hotel] = scala.collection.mutable.ListBuffer[Hotel]()
    try {
      val file = this.getClass.getClassLoader.getResource(fileName).getFile
      val reader: CSVReader = new CSVReader(new FileReader(file))
      var nextLine: Array[String] = reader.readNext()
      while ((nextLine = reader.readNext()) != null) {
        createHotel(nextLine).right.map(hotels += _)
      }
      Right(hotels)
    } catch {
      case ex: IOException => logger.error(s"Error reading data file $fileName")
        Left(ex)
      case ex: FileNotFoundException => logger.error(s"File $fileName not found")
        Left(ex)
      case ex: NullPointerException =>  logger.debug(s"Why am I getting null here?")
        Right(hotels)
    }
  }

  private def createHotel(line: Array[String]): Either[Exception, Hotel] = {
    try {
      line.map(logger.info(_))
      Right(Hotel(line(0), line(1).toInt, line(2), line(3).toInt))
    }
    catch {
      case nfe: NumberFormatException => logger.info(s"Skipping line because hotelId or price is not a number: s$line")
        Left(nfe)
      case aiobe: ArrayIndexOutOfBoundsException => logger.info(s"Skipping line because of missing params: s${line.toString}")
        Left(aiobe)
    }
  }

}
