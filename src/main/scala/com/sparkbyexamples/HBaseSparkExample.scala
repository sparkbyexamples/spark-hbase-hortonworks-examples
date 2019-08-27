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
         |"key1":{"cf":"rowkey", "col":"key", "type":"string"},
         |"name1":{"cf":"person", "col":"name", "type":"string"},
         |"state1":{"cf":"address", "col":"state", "type":"string"}
         |}
         |}""".stripMargin

    println(catalog)

    val data = (0 to 10).map { i => HBaseRecord(i+"", i+"Naveen", i+"Santa Ana") }

      val spark: SparkSession = SparkSession.builder()
        .master("local[1]")
        .appName("SparkByExamples.com")
        .getOrCreate()

      import spark.implicits._
      val df = spark.sparkContext.parallelize(data).toDF

        df.write.options(
          Map(HBaseTableCatalog.tableCatalog -> catalog, HBaseTableCatalog.newTable -> "5"))
       // .format("org.apache.hadoop.hbase.spark")
          .format("org.apache.spark.sql.execution.datasources.hbase") // https://github.com/hortonworks-spark/shc
        .save()
  }
}