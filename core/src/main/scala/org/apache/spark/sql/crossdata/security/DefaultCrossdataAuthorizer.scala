package org.apache.spark.sql.crossdata.security

/**
  * Created by ccaballero on 1/5/16.
  */
class DefaultCrossdataAuthorizer extends ICrossdataAuthorizer{

  override def login(user: String, password: String): Boolean = true

  override def checkAcl(datasource: String, catalog: Option[String], table: Option[String], permissionType: PermissionType): Boolean = true
}
