package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val CreateCustomerServerPropertiesDataTypeModel: TypeModel = TypeModel(
	  name = "CreateCustomerServerPropertiesData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 961,
    rootId = "A3N5cwADwQ",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"adminGroupEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
