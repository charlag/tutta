package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val LocationServiceGetReturnTypeModel: TypeModel = TypeModel(
	  name = "LocationServiceGetReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1321,
    rootId = "A3N5cwAFKQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"country" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
