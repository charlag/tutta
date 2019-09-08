package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val BrandingDomainDataTypeModel: TypeModel = TypeModel(
	  name = "BrandingDomainData",
	  encrypted = false,
    type = DATA_TRANSFER_TYPE,
    id = 1149,
    rootId = "A3N5cwAEfQ",
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
),
"sessionEncPemCertificateChain" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"sessionEncPemPrivateKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
"systemAdminPubEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf()
)
