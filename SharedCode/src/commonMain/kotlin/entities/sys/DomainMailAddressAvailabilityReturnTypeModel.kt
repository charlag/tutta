package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val DomainMailAddressAvailabilityReturnTypeModel: TypeModel = TypeModel(
	  name = "DomainMailAddressAvailabilityReturn",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 602,
    rootId = "A3N5cwACWg",
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
