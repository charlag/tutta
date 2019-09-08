package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CustomDomainCheckDataTypeModel: TypeModel = TypeModel(
	  name = "CustomDomainCheckData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1586,
    rootId = "A3N5cwAGMg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"domain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
