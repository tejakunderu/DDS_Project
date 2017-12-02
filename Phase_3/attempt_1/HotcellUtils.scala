package cse512

import java.lang.Exception
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

object HotcellUtils {
  val coordinateStep = 0.01
  var hotcellCube = Array.ofDim[Int](0, 0, 0)

  def CalculateCoordinate(inputString: String, coordinateOffset: Int): Int =
  {
    // Configuration variable:
    // Coordinate step is the size of each cell on x and y
    var result = 0
    coordinateOffset match
    {
      case 0 => result = Math.floor((inputString.split(",")(0).replace("(","").toDouble/coordinateStep)).toInt
      case 1 => result = Math.floor(inputString.split(",")(1).replace(")","").toDouble/coordinateStep).toInt
      // We only consider the data from 2009 to 2012 inclusively, 4 years in total. Week 0 Day 0 is 2009-01-01
      case 2 => {
        val timestamp = HotcellUtils.timestampParser(inputString)
        result = HotcellUtils.dayOfMonth(timestamp) // Assume every month has 31 days
      }
    }
    return result
  }

  def timestampParser (timestampString: String): Timestamp =
  {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
    val parsedDate = dateFormat.parse(timestampString)
    val timeStamp = new Timestamp(parsedDate.getTime)
    return timeStamp
  }

  def dayOfYear (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_YEAR)
  }

  def dayOfMonth (timestamp: Timestamp): Int =
  {
    val calendar = Calendar.getInstance
    calendar.setTimeInMillis(timestamp.getTime)
    return calendar.get(Calendar.DAY_OF_MONTH)
  }

  def initializeCube (x: Int, y: Int, z: Int): Int =
  {
    hotcellCube = Array.fill[Int](x, y, z)(0)
    return 0
  }

  def updateCube (x: Int, y: Int, z: Int): Int =
  {
    try {
      hotcellCube(x)(y)(z) = hotcellCube(x)(y)(z) + 1
    } catch {
      case e: Exception => return 0
    }
    return 0
  }

  def getMean (numX: Int, numY: Int, numZ: Int, numCells: Double): Double =
  {
    var sum = 0
    for (x <- 0 until numX) {
      for (y <- 0 until numY) {
        for (z <- 0 until numZ) {
          sum += hotcellCube(x)(y)(z)
        }
      }
    }

    return sum / numCells
  }

  def getVariance (numX: Int, numY: Int, numZ: Int, numCells: Double, mean: Double): Double =
  {
    var sum = 0.0
    for (x <- 0 until numX) {
      for (y <- 0 until numY) {
        for (z <- 0 until numZ) {
          sum += Math.pow(hotcellCube(x)(y)(z), 2)
        }
      }
    }

    return Math.sqrt((sum / numCells) - Math.pow(mean, 2))
  }

  def getGVal (x: Int, y: Int, z: Int, numX: Int, numY: Int, numZ: Int, numCells: Double, mean: Double, variance: Double): Double =
  {
    var tempX, tempY, tempZ = 0
    var sum = 0.0
    var count = 0

    for (dx <- -1 until 2) {
      for (dy <- -1 until 2) {
        for (dz <- -1 until 2) {
          tempX = x + dx
          tempY = y + dy
          tempZ = z + dz

          if (!(tempX < 0 || tempX >= numX || tempY < 0 || tempY >= numY || tempZ < 0 || tempZ >= numZ)) {
            sum += hotcellCube(tempX)(tempY)(tempZ)
            count += 1
          }
        }
      }
    }

    val numerator = sum - (mean * count)
    val denominator = variance * Math.sqrt((numCells * count - Math.pow(count, 2)) / (numCells - 1))

    return numerator / denominator
  }

}
