package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PremiumFeatureReturnTypeModel: TypeModel = TypeModel(
	  name = "PremiumFeatureReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 978,
    rootId = "A3N5cwAD0g",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"activatedFeature" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
