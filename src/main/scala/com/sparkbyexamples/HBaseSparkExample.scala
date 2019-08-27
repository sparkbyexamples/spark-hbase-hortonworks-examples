package com.sparkbyexamples

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.datasources.hbase.HBaseTableCatalog

// https://mvnrepository.com/artifact/org.apache.hbase/hbase-spark
object HBaseSparkExample {

  def main(args: Array[String]): Unit = {


    def catalog =
      s"""{
         |"table":{"namespace":"default", "name":"employee"},
         |"rowkey":"key",
         |"columns":{
         |"key":{"cf":"rowkey", "col":"key", "type":"string"},
         |"fName":{"cf":"person", "col":"firstName", "type":"string"},
         |"lName":{"cf":"person", "col":"lastName", "type":"string"},
         |"mName":{"cf":"person", "col":"middleName", "type":"string"},
         |"addressLine":{"cf":"address", "col":"addressLine", "type":"string"},
         |"city":{"cf":"address", "col":"city", "type":"string"},
         |"state":{"cf":"address", "col":"state", "type":"string"},
         |"zipCode":{"cf":"address", "col":"zipCode", "type":"string"}
         |}
         |}""".stripMargin


    val data = Seq(Employee("1", "Abby", "Smith", "K", "3456 main", "Orlando", "FL", "45235"),
      Employee("2", "Amaya", "Williams", "L", "123 Orange", "Newark", "NJ", "27656"),
      Employee("3", "Alchemy", "Davis", "P", "Warners", "Sanjose", "CA", "34789"))

    val spark: SparkSession = SparkSession.builder()
      .master("local[1]")
      .appName("SparkByExamples.com")
      .getOrCreate()

    import spark.implicits._
    val df = spark.sparkContext.parallelize(data).toDF

    df.write.options(
      Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "3"))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()


    // Reading from HBase to DataFrame
    val hbaseDF = spark.read
      .options(Map(HBaseTableCatalog.tableCatalog -> catalog))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .load()

    //Display Schema from DataFrame
    hbaseDF.printSchema()

    //Collect and show Data from DataFrame
    hbaseDF.show(false)

    //Applying Filters
    hbaseDF.filter($"key" === "1" && $"state" === "FL")
      .select("key", "fName", "lName")
      .show()

    //Create Temporary Table on DataFrame
    hbaseDF.createOrReplaceTempView("employeeTable")

    //Run SQL
    spark.sql("select * from employeeTable where fName = 'Amaya' ").show


  }
}