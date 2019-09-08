package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BrandingDomainDeleteDataTypeModel: TypeModel = TypeModel(
	  name = "BrandingDomainDeleteData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1155,
    rootId = "A3N5cwAEgw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"domain" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
