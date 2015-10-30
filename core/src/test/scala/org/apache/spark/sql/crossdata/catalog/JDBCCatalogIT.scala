/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.sql.crossdata.catalog

import org.apache.spark.sql.crossdata._
import org.apache.spark.sql.crossdata.test.SharedXDContextTest
import org.apache.spark.sql.execution.datasources.LogicalRelation
import org.apache.spark.sql.types._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class JDBCCatalogIT extends SharedXDContextTest with JDBCCatalogConstants {

  "JdbcCatalogSpec" must "return a dataframe from a persist table without catalog using json datasource" in {
    val fields = Seq[StructField](Field1, Field2)
    val columns = StructType(fields)
    val opts = Map("path" -> "/fake_path")
    val tableIdentifier = Seq(TableName)
    val crossdataTable = CrossdataTable(TableName, None, Option(columns), SourceDatasource, Array[String](), opts)
    xdContext.catalog.persistTable(crossdataTable)
    val dataframe = xdContext.sql(s"SELECT * FROM $TableName")

    dataframe shouldBe a[XDDataFrame]
  }

  it should "persist a table with catalog and partitionColumns in Jdbc" in {

    val tableIdentifier = Seq(Database, TableName)
    val crossdataTable = CrossdataTable(TableName, Option(Database), Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)
    xdContext.catalog.persistTable(crossdataTable)
    xdContext.catalog.tableExists(tableIdentifier) shouldBe true

    val df = xdContext.sql(s"SELECT * FROM $Database.$TableName")
    df shouldBe a[XDDataFrame]

  }

  it should "persist a table with catalog and partitionColumns with multiple subdocuments as schema in JDBC" in {
    xdContext.catalog.dropAllTables()
    val tableIdentifier = Seq(Database, TableName)
    val crossdataTable = CrossdataTable(TableName, Option(Database), Option(ColumnsWithSubColumns), SourceDatasource, Array.empty[String], OptsJSON)
    xdContext.catalog.persistTable(crossdataTable)

    xdContext.catalog.unregisterTable(tableIdentifier)
    xdContext.catalog.tableExists(tableIdentifier) shouldBe true
    val df = xdContext.sql(s"SELECT $FieldWithSubcolumnsName FROM $Database.$TableName")

    df shouldBe a[XDDataFrame]
    df.schema.apply(0).dataType.asInstanceOf[StructType].size shouldBe (2)
  }


  it should "returns list of tables" in {
    xdContext.catalog.dropAllTables()
    val crossdataTable1 = CrossdataTable(TableName, Option(Database), Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)
    val crossdataTable2 = CrossdataTable(TableName, None, Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)

    xdContext.catalog.persistTable(crossdataTable1)
    xdContext.catalog.persistTable(crossdataTable2)

    val tables = xdContext.catalog.getTables(Option(Database))
    tables.size shouldBe (1)
    val tables2 = xdContext.catalog.getTables(None)
    tables2.size shouldBe (2)
  }



  it should "drop tables" in {
    xdContext.catalog.dropAllTables()

    val crossdataTable1 = CrossdataTable(TableName, Option(Database), Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)
    val crossdataTable2 = CrossdataTable(TableName, None, Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)
    val tableIdentifier1 = Seq(Database, TableName)
    val tableIdentifier2 = Seq(TableName)
    xdContext.catalog.persistTable(crossdataTable1)
    xdContext.catalog.persistTable(crossdataTable2)

    val tables = xdContext.catalog.getTables(None)
    tables.size shouldBe 2
    xdContext.catalog.dropTable(tableIdentifier2)
    val tables2 = xdContext.catalog.getTables(None)
    tables2.size shouldBe 1
    xdContext.catalog.dropTable(tableIdentifier1)
    val tables3 = xdContext.catalog.getTables(None)
    tables3.size shouldBe 0
  }

  it should "check if tables map is correct" in {
    xdContext.catalog.dropAllTables()
    val crossdataTable1 = CrossdataTable(TableName, Option(Database), Option(Columns), SourceDatasource, Array[String](Field1Name), OptsJSON)
    val tableIdentifier2 = Seq(TableName)

    xdContext.catalog.persistTable(crossdataTable1)

    xdContext.catalog.registerTable(tableIdentifier2, LogicalRelation(new MockBaseRelation))
    xdContext.catalog.unregisterAllTables()
    xdContext.catalog.tables.size shouldBe 0
    val tables = xdContext.catalog.getTables(None)
    tables.size shouldBe 1

  }
  override protected def afterAll() {
    xdContext.catalog.dropAllTables()
    super.afterAll()
  }

}

sealed trait JDBCCatalogConstants {
  val Database = "database"
  val TableName = "tableName"
  val AnotherTable = "anotherTable"
  val Field1Name = "column1"
  val Field2Name = "column2"
  val SubField1Name = "subcolumn1"
  val SubField2Name = "subcolumn2"
  val FieldWithSubcolumnsName = "columnWithSubcolumns"
  val Field1 = StructField(Field1Name, StringType, nullable = true)
  val Field2 = StructField(Field2Name, StringType, nullable = true)
  val SubField = StructField(SubField1Name, StringType, nullable = true)
  val SubField2 = StructField(SubField2Name, StringType, nullable = true)
  val SourceDatasource = "org.apache.spark.sql.json"
  val Fields = Seq[StructField](Field1, Field2)
  val SubFields = Seq(SubField, SubField2)
  val Columns = StructType(Fields)
  val ColumnsWithSubColumns = StructType(Seq(StructField(Field1Name, StringType, nullable = true), StructField(FieldWithSubcolumnsName, StructType(SubFields), nullable = true)) )
  val OptsJSON = Map("path" -> "/fake_path")
}