package com.sparkbyexamples


case class HBaseRecord(
                        key1: String,
                        name1: String,
                        state1: String)

//object HBaseRecord {
//  def apply(a: Int, b: String, c: String): HBaseRecord = {
//    HBaseRecord(a, b + "_" + a, c + "_" + a)
//  }
//}