package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val DomainMailAddressAvailabilityDataTypeModel: TypeModel = TypeModel(
	  name = "DomainMailAddressAvailabilityData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 599,
    rootId = "A3N5cwACVw",
    values = mapOf("_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
"mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf()
)
