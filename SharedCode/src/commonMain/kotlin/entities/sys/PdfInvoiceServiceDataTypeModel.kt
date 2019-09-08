package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val PdfInvoiceServiceDataTypeModel: TypeModel = TypeModel(
	  name = "PdfInvoiceServiceData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 777,
    rootId = "A3N5cwADCQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invoice" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "InvoiceTypeModel",
	final = false,
	external = false
))
)
