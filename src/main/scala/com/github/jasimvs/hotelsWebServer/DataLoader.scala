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
  private val logger = LoggerFactory.getLogger(getClass)

  override def loadHotels(fileName: String): Either[Exception, Seq[Hotel]] = {
    var hotels: scala.collection.mutable.ListBuffer[Hotel] = scala.collection.mutable.ListBuffer[Hotel]()
    try {
      val file = this.getClass.getClassLoader.getResource(fileName).getFile
      val reader: CSVReader = new CSVReader(new FileReader(file))
      var nextLine: Array[String] = reader.readNext()
      while (nextLine != null) {
        val hotel = createHotel(nextLine)
        if (hotel.isRight) {
          hotel.right.map(hotels += _)
        }
        nextLine = reader.readNext()
      }
      Right(hotels)
    } catch {
      case ex: IOException => logger.debug(s"Error reading data file $fileName", ex)
        logger.warn(s"Error reading data file $fileName")
        Left(ex)
      case ex: FileNotFoundException => logger.debug(s"File $fileName not found", ex)
        logger.warn(s"File $fileName not found")
        Left(ex)
      case ex: NullPointerException => logger.debug(s"Resource $fileName not found.", ex)
        logger.warn(s"Resource $fileName not found")
        Left(ex)
    }
  }

  private def createHotel(line: Array[String]): Either[Exception, Hotel] = {
    try {
      logger.info(line.fold("")(_ + _))
      Right(Hotel(line(0), line(1).toInt, line(2), line(3).toInt))
    }
    catch {
      case nfe: NumberFormatException => logger.info(s"Skipping line because hotelId or price is not a number.")
        Left(nfe)
      case aiobe: ArrayIndexOutOfBoundsException => logger.info(s"Skipping line because of missing params.")
        Left(aiobe)
    }
  }

}
