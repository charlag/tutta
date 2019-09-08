package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val EmailSenderListElementTypeModel: TypeModel = TypeModel(
	  name = "EmailSenderListElement",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 949,
    rootId = "A3N5cwADtQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"hashedValue" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf()
)
