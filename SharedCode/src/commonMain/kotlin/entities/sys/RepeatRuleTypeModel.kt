package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val RepeatRuleTypeModel: TypeModel = TypeModel(
	  name = "RepeatRule",
	  encrypted = false,
    type = AGGREGATED_TYPE,
    id = 1557,
    rootId = "A3N5cwAGFQ",
    values = mapOf("_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
"endType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"endValue" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
"frequency" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"interval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
"timeZone" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf()
)
