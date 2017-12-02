package cse512

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
{
  // Load the original data from a data source
  var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter",";").option("header","false").load(pointPath);
  pickupInfo.createOrReplaceTempView("nyctaxitrips")
  pickupInfo.show()

  // Assign cell coordinates based on pickup points
  spark.udf.register("CalculateX",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 0)
    )))
  spark.udf.register("CalculateY",(pickupPoint: String)=>((
    HotcellUtils.CalculateCoordinate(pickupPoint, 1)
    )))
  spark.udf.register("CalculateZ",(pickupTime: String)=>((
    HotcellUtils.CalculateCoordinate(pickupTime, 2)
    )))
  pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
  var newCoordinateName = Seq("x", "y", "z")
  pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
  pickupInfo.show()

  // Define the min and max of x, y, z
  val minX = -74.50/HotcellUtils.coordinateStep
  val maxX = -73.70/HotcellUtils.coordinateStep
  val minY = 40.50/HotcellUtils.coordinateStep
  val maxY = 40.90/HotcellUtils.coordinateStep
  val minZ = 1
  val maxZ = 31
  val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)

  pickupInfo.createOrReplaceTempView("pickupInfo")

  val numX = Math.floor(maxX - minX + 1).toInt
  val numY = Math.floor(maxY - minY + 1).toInt
  val numZ = maxZ - minZ + 1
  HotcellUtils.initializeCube(numX, numY, numZ)

  var temp = spark.sql("select * from pickupInfo where " +
    "x >= " + minX.toString + " and x <= " + maxX.toString +
    " and y >= " + minY.toString + " and y <= " + maxY.toString +
    " and z >= " + minZ.toString + " and z <= " + maxZ.toString).persist()
  temp.show()
  temp.createOrReplaceTempView("pickupInfo")

  temp.foreachPartition(part => part.foreach(row =>
    HotcellUtils.updateCube(
      row.getInt(0) - Math.floor(minX).toInt,
      row.getInt(1) - Math.floor(minY).toInt,
      row.getInt(2) - minZ
    )
  ))

  val mean = HotcellUtils.getMean(numX, numY, numZ, numCells)
  val variance = HotcellUtils.getVariance(numX, numY, numZ, numCells, mean)

  spark.udf.register("GetGVal",(x: Int, y: Int, z: Int)=>
    HotcellUtils.getGVal(
      x - Math.floor(minX).toInt,
      y - Math.floor(minY).toInt,
      z - minZ,
      numX,
      numY,
      numZ,
      numCells,
      mean,
      variance
    ))
  val gVals = spark.sql("select x, y, z, GetGVal(x, y, z) as gVal from (select distinct * from pickupInfo) order by gVal desc").persist()
  gVals.show()
  gVals.createOrReplaceTempView("gValues")

  val result = spark.sql("select x, y, z from gValues limit 50").persist()
  result.show()

  return result
}
}

