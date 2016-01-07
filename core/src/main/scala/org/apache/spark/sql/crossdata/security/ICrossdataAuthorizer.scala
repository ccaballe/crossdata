package org.apache.spark.sql.crossdata.security

/**
  * Created by ccaballero on 1/5/16.
  */
trait ICrossdataAuthorizer {

  def login (user: String, password: String) : Boolean

  def checkAcl(datasource: String, catalog: Option[String], table: Option[String], permissionType: PermissionType) : Boolean



}
