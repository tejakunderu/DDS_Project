package cse512

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean = {
    val rectArr = queryRectangle.split(",").map(cord => cord.toDouble)
    val pointArr = pointString.split(",").map(cord => cord.toDouble)
    val minRectX = math.min(rectArr(0), rectArr(2))
    val minRectY = math.min(rectArr(1), rectArr(3))
    val maxRectX = math.max(rectArr(0), rectArr(2))
    val maxRectY = math.max(rectArr(1), rectArr(3))
    if (minRectX <= pointArr(0) && pointArr(0) <= maxRectX && minRectY <= pointArr(1) && pointArr(1) <= maxRectY) true else false // YOU NEED TO CHANGE THIS PART
  }

}
