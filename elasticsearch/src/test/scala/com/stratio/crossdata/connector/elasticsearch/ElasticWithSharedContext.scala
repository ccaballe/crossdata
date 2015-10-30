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
package com.stratio.crossdata.connector.elasticsearch



import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType._
import org.apache.spark.Logging
import org.apache.spark.sql.crossdata.test.SharedXDContextTest
import org.elasticsearch.common.joda.time.DateTime
import org.elasticsearch.common.settings.ImmutableSettings
import org.scalatest.Suite


trait ElasticWithSharedContext extends SharedXDContextTest with ElasticSearchDefaultConstants with Logging {
  this: Suite =>

  var elasticClient: Option[ElasticClient] = None
  var isEnvironmentReady = false

  override protected def beforeAll() = {
    super.beforeAll()

    try {
      elasticClient = Some(prepareEnvironment())

      xdContext.sql(
        s"""|CREATE TEMPORARY TABLE $Type
            |(id INT, age INT, description STRING, enrolled BOOLEAN, name STRING, optionalField BOOLEAN, birthday DATE)
            |USING $SourceProvider
            |OPTIONS (
            |resource '$Index/$Type',
            |es.node '$ElasticHost',
            |es.port '$ElasticRestPort',
            |es.nativePort '$ElasticNativePort',
            |es.cluster '$ElasticClusterName'
            |)
         """.stripMargin.replaceAll("\n", " "))

    } catch {
      case e: Throwable => logError(e.getMessage)
    }

    isEnvironmentReady = elasticClient.isDefined
  }

  override protected def afterAll() = {
    super.afterAll()
    elasticClient.foreach(cleanEnvironment)
  }


  def prepareEnvironment(): ElasticClient = {
    val settings = ImmutableSettings.settingsBuilder().put("cluster.name", ElasticClusterName).build()
    val elasticClient = ElasticClient.remote(settings, ElasticHost, ElasticNativePort)

    val command = create index Index mappings (
      Type as(
        "id" typed IntegerType,
        "age" typed IntegerType,
        "description" typed StringType,
        "enrolled" typed BooleanType,
        "name" typed StringType index NotAnalyzed,
        "birthday" typed DateType
        ))

    elasticClient.execute {command}.await

    saveTestData(elasticClient)
    elasticClient
  }

  def cleanEnvironment(elasticClient: ElasticClient) = {
    cleanTestData(elasticClient)
    elasticClient.close()
  }

  private def saveTestData(elasticClient: ElasticClient): Unit = {

    for (a <- 1 to 10) {

      elasticClient.execute {
        index into Index / Type fields(
          "id" -> a,
          "age" -> (10 + a),
          "description" -> s"A ${a}description about the Name$a",
          "enrolled" -> (if (a % 2 == 0) true else null),
          "name" -> s"Name $a",
          "birthday" -> DateTime.parse((1980+a)+"-01-01T10:00:00-00:00").toDate)
      }.await

    }
  }

  private def cleanTestData(elasticClient: ElasticClient): Unit = {
    elasticClient.execute {
      deleteIndex(Index)
    }
  }

  lazy val assumeEnvironmentIsUpAndRunning = {
    assume(isEnvironmentReady, "ElasticSearch and Spark must be up and running")
  }
}


sealed trait ElasticSearchDefaultConstants {
  val Index = "highschool"
  val Type = "students"
  val ElasticHost = "127.0.0.1"
  val ElasticRestPort = 9200
  val ElasticNativePort = 9300
  val SourceProvider = "com.stratio.crossdata.connector.elasticsearch"
  val ElasticClusterName = "esCluster"
}