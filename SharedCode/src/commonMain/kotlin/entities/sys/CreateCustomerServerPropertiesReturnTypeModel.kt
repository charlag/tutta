package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateCustomerServerPropertiesReturnTypeModel: TypeModel = TypeModel(
	  name = "CreateCustomerServerPropertiesReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 964,
    rootId = "A3N5cwADxA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("id" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerServerPropertiesTypeModel",
	final = false,
	external = false
))
)
