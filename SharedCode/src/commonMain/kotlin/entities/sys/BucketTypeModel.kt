package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BucketTypeModel: TypeModel = TypeModel(
	  name = "Bucket",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 129,
    rootId = "A3N5cwAAgQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("bucketPermissions" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "BucketPermissionTypeModel",
	final = true,
	external = false
))
)
