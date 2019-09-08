package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PremiumFeatureDataTypeModel: TypeModel = TypeModel(
	  name = "PremiumFeatureData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 651,
    rootId = "A3N5cwACiw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"activationCode" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"featureName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
