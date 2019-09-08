package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PaymentDataServiceGetReturnTypeModel: TypeModel = TypeModel(
	  name = "PaymentDataServiceGetReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 790,
    rootId = "A3N5cwADFg",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"loginUrl" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
