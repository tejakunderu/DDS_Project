package cse512

import org.apache.spark.sql.SparkSession

object SpatialQuery extends App{
  def runRangeQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",(queryRectangle:String, pointString:String)=>{
      val rectArr = queryRectangle.split(",").map(cord => cord.toDouble)
      val pointArr = pointString.split(",").map(cord => cord.toDouble)
      val minRectX = math.min(rectArr(0), rectArr(2))
      val minRectY = math.min(rectArr(1), rectArr(3))
      val maxRectX = math.max(rectArr(0), rectArr(2))
      val maxRectY = math.max(rectArr(1), rectArr(3))
      if (minRectX <= pointArr(0) && pointArr(0) <= maxRectX && minRectY <= pointArr(1) && pointArr(1) <= maxRectY) true else false
    })

    val resultDf = spark.sql("select * from point where ST_Contains('"+arg2+"',point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runRangeJoinQuery(spark: SparkSession, arg1: String, arg2: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    val rectangleDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    rectangleDf.createOrReplaceTempView("rectangle")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Contains",(queryRectangle:String, pointString:String)=>{
      val rectArr = queryRectangle.split(",").map(cord => cord.toDouble)
      val pointArr = pointString.split(",").map(cord => cord.toDouble)
      val minRectX = math.min(rectArr(0), rectArr(2))
      val minRectY = math.min(rectArr(1), rectArr(3))
      val maxRectX = math.max(rectArr(0), rectArr(2))
      val maxRectY = math.max(rectArr(1), rectArr(3))
      if (minRectX <= pointArr(0) && pointArr(0) <= maxRectX && minRectY <= pointArr(1) && pointArr(1) <= maxRectY) true else false
    })

    val resultDf = spark.sql("select * from rectangle,point where ST_Contains(rectangle._c0,point._c0)")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=>{
      val pointArr1 = pointString1.split(",").map(cord => cord.toDouble)
      val pointArr2 = pointString2.split(",").map(cord => cord.toDouble)
      val actualDistance = math.sqrt(math.pow(pointArr1(0) - pointArr2(0), 2) + math.pow(pointArr1(1) - pointArr2(1), 2))
      if (actualDistance <= distance) true else false
    })

    val resultDf = spark.sql("select * from point where ST_Within(point._c0,'"+arg2+"',"+arg3+")")
    resultDf.show()

    return resultDf.count()
  }

  def runDistanceJoinQuery(spark: SparkSession, arg1: String, arg2: String, arg3: String): Long = {

    val pointDf = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg1);
    pointDf.createOrReplaceTempView("point1")

    val pointDf2 = spark.read.format("com.databricks.spark.csv").option("delimiter","\t").option("header","false").load(arg2);
    pointDf2.createOrReplaceTempView("point2")

    // YOU NEED TO FILL IN THIS USER DEFINED FUNCTION
    spark.udf.register("ST_Within",(pointString1:String, pointString2:String, distance:Double)=>{
      val pointArr1 = pointString1.split(",").map(cord => cord.toDouble)
      val pointArr2 = pointString2.split(",").map(cord => cord.toDouble)
      val actualDistance = math.sqrt(math.pow(pointArr1(0) - pointArr2(0), 2) + math.pow(pointArr1(1) - pointArr2(1), 2))
      if (actualDistance <= distance) true else false
    })
    val resultDf = spark.sql("select * from point1 p1, point2 p2 where ST_Within(p1._c0, p2._c0, "+arg3+")")
    resultDf.show()

    return resultDf.count()
  }
}
