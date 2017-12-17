# CSE512-Project-Phase2

* Range query: Use ST_Contains. Given a query rectangle R and a set of points P, find all the points within R.
* Range join query: Use ST_Contains. Given a set of Rectangles R and a set of Points S, find all (Point, Rectangle) pairs such that the point is within the rectangle.
* Distance query: Use ST_Within. Given a point location P and distance D in km, find all points that lie within a distance D from P
* Distance join query: Use ST_Within. Given a set of Points S1 and a set of Points S2 and a distance D in km, find all (s1, s2) pairs such that s1 is within a distance D from s2 (i.e., s1 belongs to S1 and s2 belongs to S2).

### The main function is in "SparkSQLExample.scala".

### The User Defined Functions are in "SpatialQuery.scala".

### Run your code on Apache Spark using "spark-submit"

1. The main function in this template takes **dynamic length of parameters** as follows:
	* Output file path (**Mandatory**): ```/Users/ubuntu/Downloads/output```
	* Range query data file path, query window: ```rangequery /Users/ubuntu/Downloads/arealm.csv -155.940114,19.081331,-155.618917,19.5307```
	* Range join query data file path, range join query window data file path: ```rangejoinquery /Users/ubuntu/Downloads/arealm.csv /Users/ubuntu/Downloads/zcta510.csv```
	* Distance query data file path, query point, distance: ```distancequery /Users/ubuntu/Downloads/arealm.csv -88.331492,32.324142 10```
	* Distance join query data A file path, distance join query data B file path, distance: ```distancejoinquery /Users/ubuntu/Downloads/arealm.csv /Users/ubuntu/Downloads/arealm.csv 10```
2. Two example datasets are put in "src/resources" folder. arealm10000 is a point dataset and zcta10000 is a rectangle dataset. You can can use them to test your code but eventually you must run your code on NYC taxi trip dataset.
3. Here is an example that tells you how to submit your jar using "spark-submit"
```
./bin/spark-submit ~/GitHub/CSE512-Project-Phase2-Template/target/scala-2.11/CSE512-Project-Phase2-Template-assembly-0.1.0.jar result/output rangequery src/resources/arealm10000.csv -93.63173,33.0183,-93.359203,33.219456 rangejoinquery src/resources/arealm10000.csv src/resources/zcta10000.csv distancequery src/resources/arealm10000.csv -88.331492,32.324142 1 distancejoinquery src/resources/arealm10000.csv src/resources/arealm10000.csv 0.1
```

## How to submit your code to Spark
If you are using the Scala template

1. Go to project root folder
2. Run ```sbt clean assembly```. You may need to install sbt in order to run this command.
3. Find the packaged jar in "./target/scala-2.11/CSE512-Project-Phase2-Template-assembly-0.1.0.jar"
4. Submit the jar to Spark using Spark command "./bin/spark-submit"
