package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PdfInvoiceServiceReturnTypeModel: TypeModel = TypeModel(
	  name = "PdfInvoiceServiceReturn",
	  encrypted = true,
    type = DATA_TRANSFER_TYPE,
    id = 780,
    rootId = "A3N5cwADDA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"data" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf()
)
