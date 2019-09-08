package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UpdatePermissionKeyDataTypeModel: TypeModel = TypeModel(
	  name = "UpdatePermissionKeyData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 445,
    rootId = "A3N5cwABvQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"symEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bucketPermission" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "BucketPermissionTypeModel",
	final = false,
	external = false
),
"permission" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "PermissionTypeModel",
	final = false,
	external = false
))
)
