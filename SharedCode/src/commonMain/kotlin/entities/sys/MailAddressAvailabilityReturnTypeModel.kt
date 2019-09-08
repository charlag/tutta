package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val MailAddressAvailabilityReturnTypeModel: TypeModel = TypeModel(
	  name = "MailAddressAvailabilityReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 312,
    rootId = "A3N5cwABOA",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"available" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
