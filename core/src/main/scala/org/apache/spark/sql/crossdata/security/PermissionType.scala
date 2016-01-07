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