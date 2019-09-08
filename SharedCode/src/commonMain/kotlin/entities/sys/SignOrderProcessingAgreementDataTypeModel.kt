package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val SignOrderProcessingAgreementDataTypeModel: TypeModel = TypeModel(
	  name = "SignOrderProcessingAgreementData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1342,
    rootId = "A3N5cwAFPg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"customerAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
"version" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
