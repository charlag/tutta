package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val AuthenticationTypeModel: TypeModel = TypeModel(
	  name = "Authentication",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 453,
    rootId = "A3N5cwABxQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"accessToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"authVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
"externalAuthToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "UserTypeModel",
	final = false,
	external = false
))
)
