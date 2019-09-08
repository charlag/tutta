package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val OtpChallengeTypeModel: TypeModel = TypeModel(
	  name = "OtpChallenge",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1244,
    rootId = "A3N5cwAE3A",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("secondFactors" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "SecondFactorTypeModel",
	final = false,
	external = false
))
)
