/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.driver.querybuilder

/**
 * Where clause for Select statements.
 */
class Where (parent : Select) extends SelectQuery(parent) {

  val clauses : StringBuilder = new StringBuilder

  def this(clause : String, parent : Select){
    this(parent)
    clauses.append(clause)
  }

  def and(clause : String) : Where = {
    clauses.append(" AND ").append(clause)
    this
  }

  def asString() : String = {
    clauses.toString
  }

}
