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
package org.apache.spark.sql.crossdata.security

/**
  * Created by ccaballero on 1/5/16.
  */
sealed trait PermissionType {
  def name: String
}

case object Read extends PermissionType {
  val name = "Read"
}

case object Write extends PermissionType {
  val name = "Write"
}

case object Create extends PermissionType {
  val name = "Create"
}

case object Delete extends PermissionType {
  val name = "Delete"
}

case object Alter extends PermissionType {
  val name = "Alter"
}

case object Other extends PermissionType {
  val name = "Other"
}

object PermissionType {
  def fromString(action: String): PermissionType = {
    val op = values.find(op => op.name.equalsIgnoreCase(action))
    op.getOrElse(throw new RuntimeException(action + " not a valid operation name. " +
      "The valid names are " + values.mkString(",")))
  }

  def values: Seq[PermissionType] = List(Read, Write, Create, Delete, Alter, Other)
}