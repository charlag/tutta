package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PaymentDataServicePutReturnTypeModel: TypeModel = TypeModel(
	  name = "PaymentDataServicePutReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 805,
    rootId = "A3N5cwADJQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"result" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
