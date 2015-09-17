/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.stratio.crossdata.driver.config

import java.io.File

import com.typesafe.config.{ConfigFactory, Config}
import org.apache.log4j.Logger

object DriverConfig {
  val DRIVER_CONFIG_FILE = "driver-reference.conf"
  val PARENT_CONFIG_NAME = "crossdata-driver"
  val DRIVER_CONFIG_RESOURCE = "external.config.resource"
}

trait DriverConfig {
  lazy val logger: Logger = ???

  val config: Config = {
    val defaultConfig = ConfigFactory.load(DriverConfig.DRIVER_CONFIG_FILE).getConfig(DriverConfig.PARENT_CONFIG_NAME)
    val configResource = defaultConfig.getString(DriverConfig.DRIVER_CONFIG_RESOURCE)

    val mergedConfig = configResource match {
      case x if !x.isEmpty => {
        val resource = DriverConfig.getClass.getClassLoader.getResource(configResource)
        resource match {
          case null => {
            logger.warn("User file '" + configResource + "' haven't been found in the classpath")
            defaultConfig
          }
          case _ => {
            val userConfig = ConfigFactory.parseResources(configResource).getConfig(DriverConfig.PARENT_CONFIG_NAME)
            userConfig.withFallback(defaultConfig)
          }
        }
      }
      case _ => defaultConfig
    }

    ConfigFactory.load(mergedConfig)

  }

}