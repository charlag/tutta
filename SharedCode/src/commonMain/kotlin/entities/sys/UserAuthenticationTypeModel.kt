package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val UserAuthenticationTypeModel: TypeModel = TypeModel(
	  name = "UserAuthentication",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1206,
    rootId = "A3N5cwAEtg",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("recoverCode" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "RecoverCodeTypeModel",
	final = false,
	external = false
),
"secondFactors" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactorTypeModel",
	final = true,
	external = false
),
"sessions" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SessionTypeModel",
	final = true,
	external = false
))
)
