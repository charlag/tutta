
package com.charlag.tuta.entities.tutanota

import com.charlag.tuta.entities.tutanota.File as TutanotaFile
import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.entities.accounting.*
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val typeInfos = listOf(
	
  TypeInfo(
      AccountingInfo::class,
      "sys",
      TypeModel(
	  name = "AccountingInfo",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 143,
      rootId = "A3N5cwAAjw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_modified" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "invoiceAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "invoiceCountry" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "invoiceName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "invoiceVatIdNo" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "lastInvoiceNbrOfSentSms" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "lastInvoiceTimestamp" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "paymentAccountIdentifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "paymentMethod" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "paymentMethodInfo" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "paymentProviderCustomerId" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "paypalBillingAgreement" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "secondCountryInfo" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("invoiceInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "InvoiceInfo",
	final = true,
	external = false
)),
	  version = 61
),
      AccountingInfo.serializer()
  ),
	
  TypeInfo(
      AdministratedGroup::class,
      "sys",
      TypeModel(
	  name = "AdministratedGroup",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 1294,
      rootId = "A3N5cwAFDg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "groupType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = false,
	external = false
),
"localAdminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      AdministratedGroup.serializer()
  ),
	
  TypeInfo(
      AdministratedGroupsRef::class,
      "sys",
      TypeModel(
	  name = "AdministratedGroupsRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1303,
      rootId = "A3N5cwAFFw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "AdministratedGroup",
	final = true,
	external = false
)),
	  version = 61
),
      AdministratedGroupsRef.serializer()
  ),
	
  TypeInfo(
      AlarmInfo::class,
      "sys",
      TypeModel(
	  name = "AlarmInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1536,
      rootId = "A3N5cwAGAA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "alarmIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "trigger" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("calendarRef" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CalendarEventRef",
	final = false,
	external = false
)),
	  version = 61
),
      AlarmInfo.serializer()
  ),
	
  TypeInfo(
      AlarmNotification::class,
      "sys",
      TypeModel(
	  name = "AlarmNotification",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1564,
      rootId = "A3N5cwAGHA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "eventEnd" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "eventStart" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "operation" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "summary" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("alarmInfo" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "AlarmInfo",
	final = true,
	external = false
),
"notificationSessionKeys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationSessionKey",
	final = true,
	external = false
),
"repeatRule" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "RepeatRule",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = true,
	external = false
)),
	  version = 61
),
      AlarmNotification.serializer()
  ),
	
  TypeInfo(
      AlarmServicePost::class,
      "sys",
      TypeModel(
	  name = "AlarmServicePost",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 1576,
      rootId = "A3N5cwAGKA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("alarmNotifications" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AlarmNotification",
	final = false,
	external = false
)),
	  version = 61
),
      AlarmServicePost.serializer()
  ),
	
  TypeInfo(
      AuditLogEntry::class,
      "sys",
      TypeModel(
	  name = "AuditLogEntry",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1101,
      rootId = "A3N5cwAETQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "action" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "actorIpAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "actorMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "date" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "modifiedEntity" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupInfo",
	final = true,
	external = false
),
"modifiedGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupInfo",
	final = true,
	external = false
)),
	  version = 61
),
      AuditLogEntry.serializer()
  ),
	
  TypeInfo(
      AuditLogRef::class,
      "sys",
      TypeModel(
	  name = "AuditLogRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1114,
      rootId = "A3N5cwAEWg",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "AuditLogEntry",
	final = true,
	external = false
)),
	  version = 61
),
      AuditLogRef.serializer()
  ),
	
  TypeInfo(
      AuthenticatedDevice::class,
      "sys",
      TypeModel(
	  name = "AuthenticatedDevice",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 43,
      rootId = "A3N5cwAr",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "authType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "deviceKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      AuthenticatedDevice.serializer()
  ),
	
  TypeInfo(
      Authentication::class,
      "sys",
      TypeModel(
	  name = "Authentication",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 453,
      rootId = "A3N5cwABxQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "accessToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "authVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "externalAuthToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      Authentication.serializer()
  ),
	
  TypeInfo(
      AutoLoginDataDelete::class,
      "sys",
      TypeModel(
	  name = "AutoLoginDataDelete",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 435,
      rootId = "A3N5cwABsw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      AutoLoginDataDelete.serializer()
  ),
	
  TypeInfo(
      AutoLoginDataGet::class,
      "sys",
      TypeModel(
	  name = "AutoLoginDataGet",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 431,
      rootId = "A3N5cwABrw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      AutoLoginDataGet.serializer()
  ),
	
  TypeInfo(
      AutoLoginDataReturn::class,
      "sys",
      TypeModel(
	  name = "AutoLoginDataReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 438,
      rootId = "A3N5cwABtg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "deviceKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      AutoLoginDataReturn.serializer()
  ),
	
  TypeInfo(
      AutoLoginPostReturn::class,
      "sys",
      TypeModel(
	  name = "AutoLoginPostReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 441,
      rootId = "A3N5cwABuQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "deviceToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      AutoLoginPostReturn.serializer()
  ),
	
  TypeInfo(
      Booking::class,
      "sys",
      TypeModel(
	  name = "Booking",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 709,
      rootId = "A3N5cwACxQ",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "createDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "endDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "paymentMonths" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "BookingItem",
	final = false,
	external = false
)),
	  version = 61
),
      Booking.serializer()
  ),
	
  TypeInfo(
      BookingItem::class,
      "sys",
      TypeModel(
	  name = "BookingItem",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 700,
      rootId = "A3N5cwACvA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "currentCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "currentInvoicedCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "maxCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "price" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "priceType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "totalInvoicedCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      BookingItem.serializer()
  ),
	
  TypeInfo(
      BookingServiceData::class,
      "sys",
      TypeModel(
	  name = "BookingServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1061,
      rootId = "A3N5cwAEJQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "amount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      BookingServiceData.serializer()
  ),
	
  TypeInfo(
      BookingsRef::class,
      "sys",
      TypeModel(
	  name = "BookingsRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 722,
      rootId = "A3N5cwAC0g",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Booking",
	final = true,
	external = false
)),
	  version = 61
),
      BookingsRef.serializer()
  ),
	
  TypeInfo(
      BootstrapFeature::class,
      "sys",
      TypeModel(
	  name = "BootstrapFeature",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1249,
      rootId = "A3N5cwAE4Q",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "feature" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      BootstrapFeature.serializer()
  ),
	
  TypeInfo(
      BrandingDomainData::class,
      "sys",
      TypeModel(
	  name = "BrandingDomainData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1149,
      rootId = "A3N5cwAEfQ",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      BrandingDomainData.serializer()
  ),
	
  TypeInfo(
      BrandingDomainDeleteData::class,
      "sys",
      TypeModel(
	  name = "BrandingDomainDeleteData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1155,
      rootId = "A3N5cwAEgw",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      BrandingDomainDeleteData.serializer()
  ),
	
  TypeInfo(
      BrandingDomainGetReturn::class,
      "sys",
      TypeModel(
	  name = "BrandingDomainGetReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1723,
      rootId = "A3N5cwAGuw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("certificateInfo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CertificateInfo",
	final = false,
	external = false
)),
	  version = 61
),
      BrandingDomainGetReturn.serializer()
  ),
	
  TypeInfo(
      Bucket::class,
      "sys",
      TypeModel(
	  name = "Bucket",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 129,
      rootId = "A3N5cwAAgQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("bucketPermissions" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "BucketPermission",
	final = true,
	external = false
)),
	  version = 61
),
      Bucket.serializer()
  ),
	
  TypeInfo(
      BucketPermission::class,
      "sys",
      TypeModel(
	  name = "BucketPermission",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 118,
      rootId = "A3N5cwB2",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "ownerEncBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "pubEncBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "pubKeyVersion" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "symEncBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      BucketPermission.serializer()
  ),
	
  TypeInfo(
      CalendarEventRef::class,
      "sys",
      TypeModel(
	  name = "CalendarEventRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1532,
      rootId = "A3N5cwAF_A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "elementId" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "listId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      CalendarEventRef.serializer()
  ),
	
  TypeInfo(
      CertificateInfo::class,
      "sys",
      TypeModel(
	  name = "CertificateInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1500,
      rootId = "A3N5cwAF3A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "expiryDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "state" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("certificate" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "SslCertificate",
	final = true,
	external = false
)),
	  version = 61
),
      CertificateInfo.serializer()
  ),
	
  TypeInfo(
      Challenge::class,
      "sys",
      TypeModel(
	  name = "Challenge",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1187,
      rootId = "A3N5cwAEow",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("otp" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "OtpChallenge",
	final = true,
	external = false
),
"u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fChallenge",
	final = true,
	external = false
)),
	  version = 61
),
      Challenge.serializer()
  ),
	
  TypeInfo(
      ChangePasswordData::class,
      "sys",
      TypeModel(
	  name = "ChangePasswordData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 534,
      rootId = "A3N5cwACFg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "code" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "oldVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "pwEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recoverCodeVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      ChangePasswordData.serializer()
  ),
	
  TypeInfo(
      Chat::class,
      "sys",
      TypeModel(
	  name = "Chat",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 457,
      rootId = "A3N5cwAByQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "recipient" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sender" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "text" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      Chat.serializer()
  ),
	
  TypeInfo(
      CloseSessionServicePost::class,
      "sys",
      TypeModel(
	  name = "CloseSessionServicePost",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1595,
      rootId = "A3N5cwAGOw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accessToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("sessionId" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Session",
	final = false,
	external = false
)),
	  version = 61
),
      CloseSessionServicePost.serializer()
  ),
	
  TypeInfo(
      CreateCustomerServerPropertiesData::class,
      "sys",
      TypeModel(
	  name = "CreateCustomerServerPropertiesData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 961,
      rootId = "A3N5cwADwQ",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      CreateCustomerServerPropertiesData.serializer()
  ),
	
  TypeInfo(
      CreateCustomerServerPropertiesReturn::class,
      "sys",
      TypeModel(
	  name = "CreateCustomerServerPropertiesReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 964,
      rootId = "A3N5cwADxA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("id" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerServerProperties",
	final = false,
	external = false
)),
	  version = 61
),
      CreateCustomerServerPropertiesReturn.serializer()
  ),
	
  TypeInfo(
      CreateGroupData::class,
      "sys",
      TypeModel(
	  name = "CreateGroupData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 356,
      rootId = "A3N5cwABZA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "adminEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerEncUserGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "listEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "symEncPrivKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      CreateGroupData.serializer()
  ),
	
  TypeInfo(
      CreateGroupListData::class,
      "sys",
      TypeModel(
	  name = "CreateGroupListData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 365,
      rootId = "A3N5cwABbQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "adminEncGroupInfoListKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerEncGroupInfoListKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("createGroupData" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreateGroupData",
	final = false,
	external = false
)),
	  version = 61
),
      CreateGroupListData.serializer()
  ),
	
  TypeInfo(
      CreateSessionData::class,
      "sys",
      TypeModel(
	  name = "CreateSessionData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1211,
      rootId = "A3N5cwAEuw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accessKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "authVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "clientIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "recoverCodeVerifier" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "User",
	final = true,
	external = false
)),
	  version = 61
),
      CreateSessionData.serializer()
  ),
	
  TypeInfo(
      CreateSessionReturn::class,
      "sys",
      TypeModel(
	  name = "CreateSessionReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1219,
      rootId = "A3N5cwAEww",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accessToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("challenges" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "Challenge",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = true,
	external = false
)),
	  version = 61
),
      CreateSessionReturn.serializer()
  ),
	
  TypeInfo(
      CreditCard::class,
      "sys",
      TypeModel(
	  name = "CreditCard",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1313,
      rootId = "A3N5cwAFIQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "cardHolderName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "cvv" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "expirationMonth" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "expirationYear" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "number" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 61
),
      CreditCard.serializer()
  ),
	
  TypeInfo(
      CustomDomainCheckData::class,
      "sys",
      TypeModel(
	  name = "CustomDomainCheckData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1586,
      rootId = "A3N5cwAGMg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "domain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      CustomDomainCheckData.serializer()
  ),
	
  TypeInfo(
      CustomDomainCheckReturn::class,
      "sys",
      TypeModel(
	  name = "CustomDomainCheckReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1589,
      rootId = "A3N5cwAGNQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "checkResult" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invalidRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DnsRecord",
	final = false,
	external = false
),
"missingRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DnsRecord",
	final = false,
	external = false
)),
	  version = 61
),
      CustomDomainCheckReturn.serializer()
  ),
	
  TypeInfo(
      CustomDomainData::class,
      "sys",
      TypeModel(
	  name = "CustomDomainData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 735,
      rootId = "A3N5cwAC3w",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "domain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("catchAllMailGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      CustomDomainData.serializer()
  ),
	
  TypeInfo(
      CustomDomainReturn::class,
      "sys",
      TypeModel(
	  name = "CustomDomainReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 731,
      rootId = "A3N5cwAC2w",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "validationResult" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invalidDnsRecords" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "StringWrapper",
	final = true,
	external = false
)),
	  version = 61
),
      CustomDomainReturn.serializer()
  ),
	
  TypeInfo(
      Customer::class,
      "sys",
      TypeModel(
	  name = "Customer",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 31,
      rootId = "A3N5cwAf",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "approvalStatus" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "businessUse" to Value(
  type = BooleanType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "canceledPremiumAccount" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "orderProcessingAgreementNeeded" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("auditLog" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "AuditLogRef",
	final = true,
	external = false
),
"contactFormUserAreaGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroups",
	final = true,
	external = false
),
"contactFormUserGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroups",
	final = true,
	external = false
),
"customizations" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "Feature",
	final = false,
	external = false
),
"rejectedSenders" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "RejectedSendersRef",
	final = true,
	external = false
),
"userAreaGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroups",
	final = true,
	external = false
),
"whitelabelChildren" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelChildrenRef",
	final = true,
	external = false
),
"whitelabelParent" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelParent",
	final = true,
	external = false
),
"adminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"adminGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"customerGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"customerGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"customerInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CustomerInfo",
	final = true,
	external = false
),
"orderProcessingAgreement" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "OrderProcessingAgreement",
	final = true,
	external = false
),
"properties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerProperties",
	final = true,
	external = false
),
"serverProperties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "CustomerServerProperties",
	final = true,
	external = false
),
"teamGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"userGroups" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
)),
	  version = 61
),
      Customer.serializer()
  ),
	
  TypeInfo(
      CustomerData::class,
      "sys",
      TypeModel(
	  name = "CustomerData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 374,
      rootId = "A3N5cwABdg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accountingInfoBucketEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "adminEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "company" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "domain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "symEncAccountGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "systemCustomerPubEncAccountingInfoBucketKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "systemCustomerPubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListData",
	final = false,
	external = false
),
"customerGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListData",
	final = false,
	external = false
),
"teamGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListData",
	final = false,
	external = false
),
"userGroupList" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateGroupListData",
	final = false,
	external = false
)),
	  version = 61
),
      CustomerData.serializer()
  ),
	
  TypeInfo(
      CustomerInfo::class,
      "sys",
      TypeModel(
	  name = "CustomerInfo",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 148,
      rootId = "A3N5cwAAlA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "activationTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "company" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "creationTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "deletionReason" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "deletionTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "domain" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "erased" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "includedEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "includedStorageCapacity" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "promotionEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "promotionStorageCapacity" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "registrationMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "source" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "testEndTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "usedSharedEmailAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("bookings" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "BookingsRef",
	final = true,
	external = false
),
"domainInfos" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DomainInfo",
	final = true,
	external = false
),
"accountingInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "AccountingInfo",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = true,
	external = false
),
"takeoverCustomer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Customer",
	final = false,
	external = false
)),
	  version = 61
),
      CustomerInfo.serializer()
  ),
	
  TypeInfo(
      CustomerInfoReturn::class,
      "sys",
      TypeModel(
	  name = "CustomerInfoReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 550,
      rootId = "A3N5cwACJg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sendMailDisabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      CustomerInfoReturn.serializer()
  ),
	
  TypeInfo(
      CustomerProperties::class,
      "sys",
      TypeModel(
	  name = "CustomerProperties",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 656,
      rootId = "A3N5cwACkA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "externalUserWelcomeMessage" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "lastUpgradeReminder" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bigLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = false,
	external = false
),
"notificationMailTemplates" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationMailTemplate",
	final = false,
	external = false
),
"smallLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = false,
	external = false
)),
	  version = 61
),
      CustomerProperties.serializer()
  ),
	
  TypeInfo(
      CustomerReturn::class,
      "sys",
      TypeModel(
	  name = "CustomerReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 370,
      rootId = "A3N5cwABcg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminUser" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
),
"adminUserGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      CustomerReturn.serializer()
  ),
	
  TypeInfo(
      CustomerServerProperties::class,
      "sys",
      TypeModel(
	  name = "CustomerServerProperties",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 954,
      rootId = "A3N5cwADug",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "requirePasswordUpdateAfterReset" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "saveEncryptedIpAddressInSession" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "whitelabelCode" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("emailSenderList" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EmailSenderListElement",
	final = false,
	external = false
),
"whitelabelRegistrationDomains" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "StringWrapper",
	final = false,
	external = false
),
"whitelistedDomains" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "DomainsRef",
	final = true,
	external = false
)),
	  version = 61
),
      CustomerServerProperties.serializer()
  ),
	
  TypeInfo(
      DebitServicePutData::class,
      "sys",
      TypeModel(
	  name = "DebitServicePutData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1041,
      rootId = "A3N5cwAEEQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invoice" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "LegacyInvoice",
	final = false,
	external = false
)),
	  version = 61
),
      DebitServicePutData.serializer()
  ),
	
  TypeInfo(
      DeleteCustomerData::class,
      "sys",
      TypeModel(
	  name = "DeleteCustomerData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 641,
      rootId = "A3N5cwACgQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "reason" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "takeoverMailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "undelete" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = false,
	external = false
)),
	  version = 61
),
      DeleteCustomerData.serializer()
  ),
	
  TypeInfo(
      DnsRecord::class,
      "sys",
      TypeModel(
	  name = "DnsRecord",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1581,
      rootId = "A3N5cwAGLQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "subdomain" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      DnsRecord.serializer()
  ),
	
  TypeInfo(
      DomainInfo::class,
      "sys",
      TypeModel(
	  name = "DomainInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 696,
      rootId = "A3N5cwACuA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "domain" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "validatedMxRecord" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("catchAllMailGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = true,
	external = false
),
"whitelabelConfig" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "WhitelabelConfig",
	final = true,
	external = false
)),
	  version = 61
),
      DomainInfo.serializer()
  ),
	
  TypeInfo(
      DomainMailAddressAvailabilityData::class,
      "sys",
      TypeModel(
	  name = "DomainMailAddressAvailabilityData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 599,
      rootId = "A3N5cwACVw",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      DomainMailAddressAvailabilityData.serializer()
  ),
	
  TypeInfo(
      DomainMailAddressAvailabilityReturn::class,
      "sys",
      TypeModel(
	  name = "DomainMailAddressAvailabilityReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 602,
      rootId = "A3N5cwACWg",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      DomainMailAddressAvailabilityReturn.serializer()
  ),
	
  TypeInfo(
      DomainsRef::class,
      "sys",
      TypeModel(
	  name = "DomainsRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1096,
      rootId = "A3N5cwAESA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Domain",
	final = true,
	external = false
)),
	  version = 61
),
      DomainsRef.serializer()
  ),
	
  TypeInfo(
      EmailSenderListElement::class,
      "sys",
      TypeModel(
	  name = "EmailSenderListElement",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 949,
      rootId = "A3N5cwADtQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "field" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "hashedValue" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 61
),
      EmailSenderListElement.serializer()
  ),
	
  TypeInfo(
      EntityEventBatch::class,
      "sys",
      TypeModel(
	  name = "EntityEventBatch",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 1079,
      rootId = "A3N5cwAENw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("events" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EntityUpdate",
	final = true,
	external = false
)),
	  version = 61
),
      EntityEventBatch.serializer()
  ),
	
  TypeInfo(
      EntityUpdate::class,
      "sys",
      TypeModel(
	  name = "EntityUpdate",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 462,
      rootId = "A3N5cwABzg",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "application" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "instanceId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "instanceListId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "operation" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      EntityUpdate.serializer()
  ),
	
  TypeInfo(
      Exception::class,
      "sys",
      TypeModel(
	  name = "Exception",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 468,
      rootId = "A3N5cwAB1A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "msg" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      Exception.serializer()
  ),
	
  TypeInfo(
      ExternalPropertiesReturn::class,
      "sys",
      TypeModel(
	  name = "ExternalPropertiesReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 663,
      rootId = "A3N5cwAClw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "message" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bigLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = false,
	external = false
),
"smallLogo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = false,
	external = false
)),
	  version = 61
),
      ExternalPropertiesReturn.serializer()
  ),
	
  TypeInfo(
      ExternalUserReference::class,
      "sys",
      TypeModel(
	  name = "ExternalUserReference",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 103,
      rootId = "A3N5cwBn",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = true,
	external = false
),
"userGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
)),
	  version = 61
),
      ExternalUserReference.serializer()
  ),
	
  TypeInfo(
      Feature::class,
      "sys",
      TypeModel(
	  name = "Feature",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1253,
      rootId = "A3N5cwAE5Q",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "feature" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      Feature.serializer()
  ),
	
  TypeInfo(
      File::class,
      "sys",
      TypeModel(
	  name = "File",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 917,
      rootId = "A3N5cwADlQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "data" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mimeType" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      File.serializer()
  ),
	
  TypeInfo(
      GeneratedIdWrapper::class,
      "sys",
      TypeModel(
	  name = "GeneratedIdWrapper",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1349,
      rootId = "A3N5cwAFRQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "value" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      GeneratedIdWrapper.serializer()
  ),
	
  TypeInfo(
      Group::class,
      "sys",
      TypeModel(
	  name = "Group",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 5,
      rootId = "A3N5cwAF",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "adminGroupEncGKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "external" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("administratedGroups" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "AdministratedGroupsRef",
	final = true,
	external = false
),
"keys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "KeyPair",
	final = true,
	external = false
),
"admin" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Customer",
	final = true,
	external = false
),
"groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"invitations" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SentGroupInvitation",
	final = true,
	external = false
),
"members" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupMember",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "User",
	final = true,
	external = false
)),
	  version = 61
),
      Group.serializer()
  ),
	
  TypeInfo(
      GroupInfo::class,
      "sys",
      TypeModel(
	  name = "GroupInfo",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 14,
      rootId = "A3N5cwAO",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_listEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "created" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "deleted" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "groupType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("mailAddressAliases" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddressAlias",
	final = true,
	external = false
),
"group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"localAdmin" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = true,
	external = false
)),
	  version = 61
),
      GroupInfo.serializer()
  ),
	
  TypeInfo(
      GroupMember::class,
      "sys",
      TypeModel(
	  name = "GroupMember",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 216,
      rootId = "A3N5cwAA2A",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "capability" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = true,
	external = false
),
"userGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
)),
	  version = 61
),
      GroupMember.serializer()
  ),
	
  TypeInfo(
      GroupMembership::class,
      "sys",
      TypeModel(
	  name = "GroupMembership",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 25,
      rootId = "A3N5cwAZ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "admin" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "capability" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "groupType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"groupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"groupMember" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupMember",
	final = true,
	external = false
)),
	  version = 61
),
      GroupMembership.serializer()
  ),
	
  TypeInfo(
      GroupRoot::class,
      "sys",
      TypeModel(
	  name = "GroupRoot",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 110,
      rootId = "A3N5cwBu",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("externalUserAreaGroupInfos" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAreaGroups",
	final = true,
	external = false
),
"externalGroupInfos" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
),
"externalUserReferences" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "ExternalUserReference",
	final = true,
	external = false
)),
	  version = 61
),
      GroupRoot.serializer()
  ),
	
  TypeInfo(
      Invoice::class,
      "sys",
      TypeModel(
	  name = "Invoice",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 1650,
      rootId = "A3N5cwAGcg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "address" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "adminUser" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "business" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "country" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "date" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "grandTotal" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "paymentMethod" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "reason" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "subTotal" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "vat" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "vatIdNumber" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "vatRate" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("items" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InvoiceItem",
	final = true,
	external = false
),
"bookings" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "Booking",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = true,
	external = false
)),
	  version = 61
),
      Invoice.serializer()
  ),
	
  TypeInfo(
      InvoiceInfo::class,
      "sys",
      TypeModel(
	  name = "InvoiceInfo",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 752,
      rootId = "A3N5cwAC8A",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "extendedPeriodOfPaymentDays" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "persistentPaymentPeriodExtension" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "publishInvoices" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "reminderState" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "specialPriceBrandingPerUser" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "specialPriceContactFormSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "specialPriceSharedGroupSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "specialPriceSharingPerUser" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "specialPriceUserSingle" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "specialPriceUserTotal" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("paymentErrorInfo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PaymentErrorInfo",
	final = true,
	external = false
),
"invoices" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "LegacyInvoice",
	final = true,
	external = false
)),
	  version = 61
),
      InvoiceInfo.serializer()
  ),
	
  TypeInfo(
      InvoiceItem::class,
      "sys",
      TypeModel(
	  name = "InvoiceItem",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1641,
      rootId = "A3N5cwAGaQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "amount" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "endDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "singlePrice" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "singleType" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "startDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "totalPrice" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 61
),
      InvoiceItem.serializer()
  ),
	
  TypeInfo(
      KeyPair::class,
      "sys",
      TypeModel(
	  name = "KeyPair",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 0,
      rootId = "A3N5cwAA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "symEncPrivKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "version" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      KeyPair.serializer()
  ),
	
  TypeInfo(
      LocationServiceGetReturn::class,
      "sys",
      TypeModel(
	  name = "LocationServiceGetReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1321,
      rootId = "A3N5cwAFKQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "country" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      LocationServiceGetReturn.serializer()
  ),
	
  TypeInfo(
      Login::class,
      "sys",
      TypeModel(
	  name = "Login",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 48,
      rootId = "A3N5cwAw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "time" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      Login.serializer()
  ),
	
  TypeInfo(
      MailAddressAlias::class,
      "sys",
      TypeModel(
	  name = "MailAddressAlias",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 684,
      rootId = "A3N5cwACrA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      MailAddressAlias.serializer()
  ),
	
  TypeInfo(
      MailAddressAliasServiceData::class,
      "sys",
      TypeModel(
	  name = "MailAddressAliasServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 688,
      rootId = "A3N5cwACsA",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      MailAddressAliasServiceData.serializer()
  ),
	
  TypeInfo(
      MailAddressAliasServiceDataDelete::class,
      "sys",
      TypeModel(
	  name = "MailAddressAliasServiceDataDelete",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 785,
      rootId = "A3N5cwADEQ",
      values = mapOf(                "_format" to Value(
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
),
                "restore" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      MailAddressAliasServiceDataDelete.serializer()
  ),
	
  TypeInfo(
      MailAddressAliasServiceReturn::class,
      "sys",
      TypeModel(
	  name = "MailAddressAliasServiceReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 692,
      rootId = "A3N5cwACtA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "enabledAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "nbrOfFreeAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "totalAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "usedAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      MailAddressAliasServiceReturn.serializer()
  ),
	
  TypeInfo(
      MailAddressAvailabilityData::class,
      "sys",
      TypeModel(
	  name = "MailAddressAvailabilityData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 309,
      rootId = "A3N5cwABNQ",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      MailAddressAvailabilityData.serializer()
  ),
	
  TypeInfo(
      MailAddressAvailabilityReturn::class,
      "sys",
      TypeModel(
	  name = "MailAddressAvailabilityReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 312,
      rootId = "A3N5cwABOA",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      MailAddressAvailabilityReturn.serializer()
  ),
	
  TypeInfo(
      MailAddressToGroup::class,
      "sys",
      TypeModel(
	  name = "MailAddressToGroup",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 204,
      rootId = "A3N5cwAAzA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("internalGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      MailAddressToGroup.serializer()
  ),
	
  TypeInfo(
      MembershipAddData::class,
      "sys",
      TypeModel(
	  name = "MembershipAddData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 505,
      rootId = "A3N5cwAB-Q",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "symEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      MembershipAddData.serializer()
  ),
	
  TypeInfo(
      MembershipRemoveData::class,
      "sys",
      TypeModel(
	  name = "MembershipRemoveData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 867,
      rootId = "A3N5cwADYw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      MembershipRemoveData.serializer()
  ),
	
  TypeInfo(
      MissedNotification::class,
      "sys",
      TypeModel(
	  name = "MissedNotification",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 1693,
      rootId = "A3N5cwAGnQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "changeTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "confirmationId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "lastProcessedNotificationId" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarmNotifications" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AlarmNotification",
	final = false,
	external = false
),
"notificationInfos" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationInfo",
	final = false,
	external = false
)),
	  version = 61
),
      MissedNotification.serializer()
  ),
	
  TypeInfo(
      NotificationInfo::class,
      "sys",
      TypeModel(
	  name = "NotificationInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1364,
      rootId = "A3N5cwAFVA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "counter" to Value(
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
),
                "userId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      NotificationInfo.serializer()
  ),
	
  TypeInfo(
      NotificationMailTemplate::class,
      "sys",
      TypeModel(
	  name = "NotificationMailTemplate",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1517,
      rootId = "A3N5cwAF7Q",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "body" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "subject" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      NotificationMailTemplate.serializer()
  ),
	
  TypeInfo(
      NotificationSessionKey::class,
      "sys",
      TypeModel(
	  name = "NotificationSessionKey",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1553,
      rootId = "A3N5cwAGEQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "pushIdentifierSessionEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("pushIdentifier" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "PushIdentifier",
	final = false,
	external = false
)),
	  version = 61
),
      NotificationSessionKey.serializer()
  ),
	
  TypeInfo(
      OrderProcessingAgreement::class,
      "sys",
      TypeModel(
	  name = "OrderProcessingAgreement",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1326,
      rootId = "A3N5cwAFLg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customerAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "signatureDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "version" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = true,
	external = false
),
"signerUserGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = false,
	external = false
)),
	  version = 61
),
      OrderProcessingAgreement.serializer()
  ),
	
  TypeInfo(
      OtpChallenge::class,
      "sys",
      TypeModel(
	  name = "OtpChallenge",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1244,
      rootId = "A3N5cwAE3A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("secondFactors" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "SecondFactor",
	final = false,
	external = false
)),
	  version = 61
),
      OtpChallenge.serializer()
  ),
	
  TypeInfo(
      PaymentDataServiceGetReturn::class,
      "sys",
      TypeModel(
	  name = "PaymentDataServiceGetReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 790,
      rootId = "A3N5cwADFg",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      PaymentDataServiceGetReturn.serializer()
  ),
	
  TypeInfo(
      PaymentDataServicePutData::class,
      "sys",
      TypeModel(
	  name = "PaymentDataServicePutData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 793,
      rootId = "A3N5cwADGQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "confirmedCountry" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "invoiceAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "invoiceCountry" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "invoiceName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "invoiceVatIdNo" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "paymentMethod" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "paymentMethodInfo" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "paymentToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
)),
	  associations = mapOf("creditCard" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreditCard",
	final = false,
	external = false
)),
	  version = 61
),
      PaymentDataServicePutData.serializer()
  ),
	
  TypeInfo(
      PaymentDataServicePutReturn::class,
      "sys",
      TypeModel(
	  name = "PaymentDataServicePutReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 805,
      rootId = "A3N5cwADJQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "result" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PaymentDataServicePutReturn.serializer()
  ),
	
  TypeInfo(
      PaymentErrorInfo::class,
      "sys",
      TypeModel(
	  name = "PaymentErrorInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1632,
      rootId = "A3N5cwAGYA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "errorCode" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "errorTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "thirdPartyErrorId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PaymentErrorInfo.serializer()
  ),
	
  TypeInfo(
      PdfInvoiceServiceData::class,
      "sys",
      TypeModel(
	  name = "PdfInvoiceServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 777,
      rootId = "A3N5cwADCQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "invoiceNumber" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("invoice" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "LegacyInvoice",
	final = false,
	external = false
)),
	  version = 61
),
      PdfInvoiceServiceData.serializer()
  ),
	
  TypeInfo(
      PdfInvoiceServiceReturn::class,
      "sys",
      TypeModel(
	  name = "PdfInvoiceServiceReturn",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 780,
      rootId = "A3N5cwADDA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerPublicEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "data" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 61
),
      PdfInvoiceServiceReturn.serializer()
  ),
	
  TypeInfo(
      Permission::class,
      "sys",
      TypeModel(
	  name = "Permission",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 132,
      rootId = "A3N5cwAAhA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bucketEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "listElementApplication" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "listElementTypeId" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "ops" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "symEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bucket" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "Bucket",
	final = false,
	external = false
),
"group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      Permission.serializer()
  ),
	
  TypeInfo(
      PhoneNumber::class,
      "sys",
      TypeModel(
	  name = "PhoneNumber",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 63,
      rootId = "A3N5cwA_",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "number" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PhoneNumber.serializer()
  ),
	
  TypeInfo(
      PlanPrices::class,
      "sys",
      TypeModel(
	  name = "PlanPrices",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1460,
      rootId = "A3N5cwAFtA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "additionalUserPriceMonthly" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "contactFormPriceMonthly" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "firstYearDiscount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "includedAliases" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "includedStorage" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "monthlyPrice" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "monthlyReferencePrice" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PlanPrices.serializer()
  ),
	
  TypeInfo(
      PremiumFeatureData::class,
      "sys",
      TypeModel(
	  name = "PremiumFeatureData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 651,
      rootId = "A3N5cwACiw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "activationCode" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "featureName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PremiumFeatureData.serializer()
  ),
	
  TypeInfo(
      PremiumFeatureReturn::class,
      "sys",
      TypeModel(
	  name = "PremiumFeatureReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 978,
      rootId = "A3N5cwAD0g",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "activatedFeature" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PremiumFeatureReturn.serializer()
  ),
	
  TypeInfo(
      PriceData::class,
      "sys",
      TypeModel(
	  name = "PriceData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 853,
      rootId = "A3N5cwADVQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "paymentInterval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "price" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "taxIncluded" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PriceItemData",
	final = false,
	external = false
)),
	  version = 61
),
      PriceData.serializer()
  ),
	
  TypeInfo(
      PriceItemData::class,
      "sys",
      TypeModel(
	  name = "PriceItemData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 847,
      rootId = "A3N5cwADTw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "count" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "price" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "singleType" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PriceItemData.serializer()
  ),
	
  TypeInfo(
      PriceRequestData::class,
      "sys",
      TypeModel(
	  name = "PriceRequestData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 836,
      rootId = "A3N5cwADRA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "accountType" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "business" to Value(
  type = BooleanType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "count" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "featureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "paymentInterval" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "reactivate" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PriceRequestData.serializer()
  ),
	
  TypeInfo(
      PriceServiceData::class,
      "sys",
      TypeModel(
	  name = "PriceServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 843,
      rootId = "A3N5cwADSw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "campaign" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("priceRequest" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceRequestData",
	final = false,
	external = false
)),
	  version = 61
),
      PriceServiceData.serializer()
  ),
	
  TypeInfo(
      PriceServiceReturn::class,
      "sys",
      TypeModel(
	  name = "PriceServiceReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 859,
      rootId = "A3N5cwADWw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "currentPeriodAddedPrice" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "periodEndDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("currentPriceNextPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceData",
	final = false,
	external = false
),
"currentPriceThisPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceData",
	final = false,
	external = false
),
"futurePriceNextPeriod" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PriceData",
	final = false,
	external = false
)),
	  version = 61
),
      PriceServiceReturn.serializer()
  ),
	
  TypeInfo(
      PublicKeyData::class,
      "sys",
      TypeModel(
	  name = "PublicKeyData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 409,
      rootId = "A3N5cwABmQ",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      PublicKeyData.serializer()
  ),
	
  TypeInfo(
      PublicKeyReturn::class,
      "sys",
      TypeModel(
	  name = "PublicKeyReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 412,
      rootId = "A3N5cwABnA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PublicKeyReturn.serializer()
  ),
	
  TypeInfo(
      PushIdentifier::class,
      "sys",
      TypeModel(
	  name = "PushIdentifier",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 625,
      rootId = "A3N5cwACcQ",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "disabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "displayName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "identifier" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "lastNotificationDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "lastUsageTime" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pushServiceType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      PushIdentifier.serializer()
  ),
	
  TypeInfo(
      PushIdentifierList::class,
      "sys",
      TypeModel(
	  name = "PushIdentifierList",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 635,
      rootId = "A3N5cwACew",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("list" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "PushIdentifier",
	final = true,
	external = false
)),
	  version = 61
),
      PushIdentifierList.serializer()
  ),
	
  TypeInfo(
      ReceivedGroupInvitation::class,
      "sys",
      TypeModel(
	  name = "ReceivedGroupInvitation",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1602,
      rootId = "A3N5cwAGQg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "capability" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "inviteeMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "inviterMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "inviterName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "sharedGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "sharedGroupName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("sentInvitation" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "SentGroupInvitation",
	final = false,
	external = false
),
"sharedGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      ReceivedGroupInvitation.serializer()
  ),
	
  TypeInfo(
      RecoverCode::class,
      "sys",
      TypeModel(
	  name = "RecoverCode",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 1407,
      rootId = "A3N5cwAFfw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "recoverCodeEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "userEncRecoverCode" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RecoverCode.serializer()
  ),
	
  TypeInfo(
      RegistrationCaptchaServiceData::class,
      "sys",
      TypeModel(
	  name = "RegistrationCaptchaServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 674,
      rootId = "A3N5cwACog",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "response" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "token" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationCaptchaServiceData.serializer()
  ),
	
  TypeInfo(
      RegistrationCaptchaServiceGetData::class,
      "sys",
      TypeModel(
	  name = "RegistrationCaptchaServiceGetData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1479,
      rootId = "A3N5cwAFxw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "businessUseSelected" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "paidSubscriptionSelected" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "signupToken" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "token" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationCaptchaServiceGetData.serializer()
  ),
	
  TypeInfo(
      RegistrationCaptchaServiceReturn::class,
      "sys",
      TypeModel(
	  name = "RegistrationCaptchaServiceReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 678,
      rootId = "A3N5cwACpg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "challenge" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "token" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationCaptchaServiceReturn.serializer()
  ),
	
  TypeInfo(
      RegistrationConfigReturn::class,
      "sys",
      TypeModel(
	  name = "RegistrationConfigReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 606,
      rootId = "A3N5cwACXg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "freeEnabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "starterEnabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationConfigReturn.serializer()
  ),
	
  TypeInfo(
      RegistrationReturn::class,
      "sys",
      TypeModel(
	  name = "RegistrationReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 326,
      rootId = "A3N5cwABRg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationReturn.serializer()
  ),
	
  TypeInfo(
      RegistrationServiceData::class,
      "sys",
      TypeModel(
	  name = "RegistrationServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 316,
      rootId = "A3N5cwABPA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "starterDomain" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "source" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "state" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RegistrationServiceData.serializer()
  ),
	
  TypeInfo(
      RejectedSender::class,
      "sys",
      TypeModel(
	  name = "RejectedSender",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 1736,
      rootId = "A3N5cwAGyA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "reason" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "recipientMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "senderHostname" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "senderIp" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "senderMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RejectedSender.serializer()
  ),
	
  TypeInfo(
      RejectedSendersRef::class,
      "sys",
      TypeModel(
	  name = "RejectedSendersRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1747,
      rootId = "A3N5cwAG0w",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "RejectedSender",
	final = true,
	external = false
)),
	  version = 61
),
      RejectedSendersRef.serializer()
  ),
	
  TypeInfo(
      RepeatRule::class,
      "sys",
      TypeModel(
	  name = "RepeatRule",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1557,
      rootId = "A3N5cwAGFQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "endType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "endValue" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "frequency" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "interval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "timeZone" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 61
),
      RepeatRule.serializer()
  ),
	
  TypeInfo(
      ResetFactorsDeleteData::class,
      "sys",
      TypeModel(
	  name = "ResetFactorsDeleteData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1419,
      rootId = "A3N5cwAFiw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authVerifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "recoverCodeVerifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      ResetFactorsDeleteData.serializer()
  ),
	
  TypeInfo(
      ResetPasswordData::class,
      "sys",
      TypeModel(
	  name = "ResetPasswordData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 584,
      rootId = "A3N5cwACSA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pwEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      ResetPasswordData.serializer()
  ),
	
  TypeInfo(
      RootInstance::class,
      "sys",
      TypeModel(
	  name = "RootInstance",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 231,
      rootId = "A3N5cwAA5w",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "reference" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      RootInstance.serializer()
  ),
	
  TypeInfo(
      SaltData::class,
      "sys",
      TypeModel(
	  name = "SaltData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 417,
      rootId = "A3N5cwABoQ",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      SaltData.serializer()
  ),
	
  TypeInfo(
      SaltReturn::class,
      "sys",
      TypeModel(
	  name = "SaltReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 420,
      rootId = "A3N5cwABpA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SaltReturn.serializer()
  ),
	
  TypeInfo(
      SecondFactor::class,
      "sys",
      TypeModel(
	  name = "SecondFactor",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 1169,
      rootId = "A3N5cwAEkQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "otpSecret" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fRegisteredDevice",
	final = true,
	external = false
)),
	  version = 61
),
      SecondFactor.serializer()
  ),
	
  TypeInfo(
      SecondFactorAuthAllowedReturn::class,
      "sys",
      TypeModel(
	  name = "SecondFactorAuthAllowedReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 546,
      rootId = "A3N5cwACIg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "allowed" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SecondFactorAuthAllowedReturn.serializer()
  ),
	
  TypeInfo(
      SecondFactorAuthData::class,
      "sys",
      TypeModel(
	  name = "SecondFactorAuthData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 541,
      rootId = "A3N5cwACHQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "otpCode" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("u2f" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "U2fResponseData",
	final = true,
	external = false
),
"session" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Session",
	final = true,
	external = false
)),
	  version = 61
),
      SecondFactorAuthData.serializer()
  ),
	
  TypeInfo(
      SecondFactorAuthGetData::class,
      "sys",
      TypeModel(
	  name = "SecondFactorAuthGetData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1233,
      rootId = "A3N5cwAE0Q",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accessToken" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SecondFactorAuthGetData.serializer()
  ),
	
  TypeInfo(
      SecondFactorAuthGetReturn::class,
      "sys",
      TypeModel(
	  name = "SecondFactorAuthGetReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1236,
      rootId = "A3N5cwAE1A",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "secondFactorPending" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SecondFactorAuthGetReturn.serializer()
  ),
	
  TypeInfo(
      SecondFactorAuthentication::class,
      "sys",
      TypeModel(
	  name = "SecondFactorAuthentication",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 54,
      rootId = "A3N5cwA2",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "finished" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "service" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifyCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SecondFactorAuthentication.serializer()
  ),
	
  TypeInfo(
      SendRegistrationCodeData::class,
      "sys",
      TypeModel(
	  name = "SendRegistrationCodeData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 341,
      rootId = "A3N5cwABVQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "language" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mobilePhoneNumber" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SendRegistrationCodeData.serializer()
  ),
	
  TypeInfo(
      SendRegistrationCodeReturn::class,
      "sys",
      TypeModel(
	  name = "SendRegistrationCodeReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 347,
      rootId = "A3N5cwABWw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SendRegistrationCodeReturn.serializer()
  ),
	
  TypeInfo(
      SentGroupInvitation::class,
      "sys",
      TypeModel(
	  name = "SentGroupInvitation",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 195,
      rootId = "A3N5cwAAww",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "capability" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "inviteeMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("receivedInvitation" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "ReceivedGroupInvitation",
	final = false,
	external = false
),
"sharedGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      SentGroupInvitation.serializer()
  ),
	
  TypeInfo(
      Session::class,
      "sys",
      TypeModel(
	  name = "Session",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1191,
      rootId = "A3N5cwAEpw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "accessKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "clientIdentifier" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "lastAccessTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "loginIpAddress" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "loginTime" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "state" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("challenges" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "Challenge",
	final = true,
	external = false
),
"user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = true,
	external = false
)),
	  version = 61
),
      Session.serializer()
  ),
	
  TypeInfo(
      SignOrderProcessingAgreementData::class,
      "sys",
      TypeModel(
	  name = "SignOrderProcessingAgreementData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1342,
      rootId = "A3N5cwAFPg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "version" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SignOrderProcessingAgreementData.serializer()
  ),
	
  TypeInfo(
      SseConnectData::class,
      "sys",
      TypeModel(
	  name = "SseConnectData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1352,
      rootId = "A3N5cwAFSA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "identifier" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("userIds" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "GeneratedIdWrapper",
	final = false,
	external = false
)),
	  version = 61
),
      SseConnectData.serializer()
  ),
	
  TypeInfo(
      StringConfigValue::class,
      "sys",
      TypeModel(
	  name = "StringConfigValue",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 515,
      rootId = "A3N5cwACAw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      StringConfigValue.serializer()
  ),
	
  TypeInfo(
      StringWrapper::class,
      "sys",
      TypeModel(
	  name = "StringWrapper",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 728,
      rootId = "A3N5cwAC2A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      StringWrapper.serializer()
  ),
	
  TypeInfo(
      SwitchAccountTypeData::class,
      "sys",
      TypeModel(
	  name = "SwitchAccountTypeData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 772,
      rootId = "A3N5cwADBA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "campaign" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "subscriptionType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      SwitchAccountTypeData.serializer()
  ),
	
  TypeInfo(
      SystemKeysReturn::class,
      "sys",
      TypeModel(
	  name = "SystemKeysReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 301,
      rootId = "A3N5cwABLQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "freeGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "premiumGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "starterGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "systemAdminPubKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "systemAdminPubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("freeGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = false,
	external = false
),
"premiumGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      SystemKeysReturn.serializer()
  ),
	
  TypeInfo(
      U2fChallenge::class,
      "sys",
      TypeModel(
	  name = "U2fChallenge",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1183,
      rootId = "A3N5cwAEnw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "challenge" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("keys" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "U2fKey",
	final = true,
	external = false
)),
	  version = 61
),
      U2fChallenge.serializer()
  ),
	
  TypeInfo(
      U2fKey::class,
      "sys",
      TypeModel(
	  name = "U2fKey",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1178,
      rootId = "A3N5cwAEmg",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "appId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "keyHandle" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("secondFactor" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactor",
	final = false,
	external = false
)),
	  version = 61
),
      U2fKey.serializer()
  ),
	
  TypeInfo(
      U2fRegisteredDevice::class,
      "sys",
      TypeModel(
	  name = "U2fRegisteredDevice",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1162,
      rootId = "A3N5cwAEig",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "appId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "compromised" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "counter" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "keyHandle" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "publicKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      U2fRegisteredDevice.serializer()
  ),
	
  TypeInfo(
      U2fResponseData::class,
      "sys",
      TypeModel(
	  name = "U2fResponseData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1225,
      rootId = "A3N5cwAEyQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "clientData" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "keyHandle" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "signatureData" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      U2fResponseData.serializer()
  ),
	
  TypeInfo(
      UpdateAdminshipData::class,
      "sys",
      TypeModel(
	  name = "UpdateAdminshipData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1288,
      rootId = "A3N5cwAFCA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "newAdminGroupEncGKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
),
"newAdminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = false
)),
	  version = 61
),
      UpdateAdminshipData.serializer()
  ),
	
  TypeInfo(
      UpdatePermissionKeyData::class,
      "sys",
      TypeModel(
	  name = "UpdatePermissionKeyData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 445,
      rootId = "A3N5cwABvQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "symEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bucketPermission" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "BucketPermission",
	final = false,
	external = false
),
"permission" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Permission",
	final = false,
	external = false
)),
	  version = 61
),
      UpdatePermissionKeyData.serializer()
  ),
	
  TypeInfo(
      UpgradePriceServiceData::class,
      "sys",
      TypeModel(
	  name = "UpgradePriceServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1456,
      rootId = "A3N5cwAFsA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "campaign" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      UpgradePriceServiceData.serializer()
  ),
	
  TypeInfo(
      UpgradePriceServiceReturn::class,
      "sys",
      TypeModel(
	  name = "UpgradePriceServiceReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1469,
      rootId = "A3N5cwAFvQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "business" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "messageTextId" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("premiumPrices" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "PlanPrices",
	final = false,
	external = false
),
"proPrices" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "PlanPrices",
	final = false,
	external = false
),
"teamsPrices" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "PlanPrices",
	final = false,
	external = false
)),
	  version = 61
),
      UpgradePriceServiceReturn.serializer()
  ),
	
  TypeInfo(
      User::class,
      "sys",
      TypeModel(
	  name = "User",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 84,
      rootId = "A3N5cwBU",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "accountType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "enabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "requirePasswordUpdate" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarmInfoList" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAlarmInfoListType",
	final = false,
	external = false
),
"auth" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserAuthentication",
	final = true,
	external = false
),
"authenticatedDevices" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AuthenticatedDevice",
	final = true,
	external = false
),
"externalAuthInfo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UserExternalAuthInfo",
	final = true,
	external = false
),
"memberships" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "GroupMembership",
	final = true,
	external = false
),
"phoneNumbers" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PhoneNumber",
	final = true,
	external = false
),
"pushIdentifierList" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PushIdentifierList",
	final = false,
	external = false
),
"userGroup" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "GroupMembership",
	final = true,
	external = false
),
"customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Customer",
	final = true,
	external = false
),
"failedLogins" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Login",
	final = true,
	external = false
),
"secondFactorAuthentications" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactorAuthentication",
	final = true,
	external = false
),
"successfulLogins" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Login",
	final = true,
	external = false
)),
	  version = 61
),
      User.serializer()
  ),
	
  TypeInfo(
      UserAlarmInfo::class,
      "sys",
      TypeModel(
	  name = "UserAlarmInfo",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1541,
      rootId = "A3N5cwAGBQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarmInfo" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "AlarmInfo",
	final = false,
	external = false
)),
	  version = 61
),
      UserAlarmInfo.serializer()
  ),
	
  TypeInfo(
      UserAlarmInfoListType::class,
      "sys",
      TypeModel(
	  name = "UserAlarmInfoListType",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1549,
      rootId = "A3N5cwAGDQ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("alarms" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "UserAlarmInfo",
	final = true,
	external = false
)),
	  version = 61
),
      UserAlarmInfoListType.serializer()
  ),
	
  TypeInfo(
      UserAreaGroups::class,
      "sys",
      TypeModel(
	  name = "UserAreaGroups",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 988,
      rootId = "A3N5cwAD3A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("list" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
)),
	  version = 61
),
      UserAreaGroups.serializer()
  ),
	
  TypeInfo(
      UserAuthentication::class,
      "sys",
      TypeModel(
	  name = "UserAuthentication",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1206,
      rootId = "A3N5cwAEtg",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("recoverCode" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "RecoverCode",
	final = false,
	external = false
),
"secondFactors" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "SecondFactor",
	final = true,
	external = false
),
"sessions" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Session",
	final = true,
	external = false
)),
	  version = 61
),
      UserAuthentication.serializer()
  ),
	
  TypeInfo(
      UserData::class,
      "sys",
      TypeModel(
	  name = "UserData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 396,
      rootId = "A3N5cwABjA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "mobilePhoneNumber" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncCustomerGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userGroupData" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CreateGroupData",
	final = false,
	external = false
)),
	  version = 61
),
      UserData.serializer()
  ),
	
  TypeInfo(
      UserDataDelete::class,
      "sys",
      TypeModel(
	  name = "UserDataDelete",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 404,
      rootId = "A3N5cwABlA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "restore" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      UserDataDelete.serializer()
  ),
	
  TypeInfo(
      UserExternalAuthInfo::class,
      "sys",
      TypeModel(
	  name = "UserExternalAuthInfo",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 77,
      rootId = "A3N5cwBN",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "authUpdateCounter" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "autoAuthenticationId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "autoTransmitPassword" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "latestSaltHash" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("variableAuthInfo" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "VariableExternalAuthInfo",
	final = true,
	external = false
)),
	  version = 61
),
      UserExternalAuthInfo.serializer()
  ),
	
  TypeInfo(
      UserGroupRoot::class,
      "sys",
      TypeModel(
	  name = "UserGroupRoot",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 1618,
      rootId = "A3N5cwAGUg",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("invitations" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "ReceivedGroupInvitation",
	final = true,
	external = false
)),
	  version = 61
),
      UserGroupRoot.serializer()
  ),
	
  TypeInfo(
      UserIdData::class,
      "sys",
      TypeModel(
	  name = "UserIdData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 424,
      rootId = "A3N5cwABqA",
      values = mapOf(                "_format" to Value(
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
	  associations = mapOf(),
	  version = 61
),
      UserIdData.serializer()
  ),
	
  TypeInfo(
      UserIdReturn::class,
      "sys",
      TypeModel(
	  name = "UserIdReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 427,
      rootId = "A3N5cwABqw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
)),
	  version = 61
),
      UserIdReturn.serializer()
  ),
	
  TypeInfo(
      UserReturn::class,
      "sys",
      TypeModel(
	  name = "UserReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 392,
      rootId = "A3N5cwABiA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("user" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "User",
	final = false,
	external = false
),
"userGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
)),
	  version = 61
),
      UserReturn.serializer()
  ),
	
  TypeInfo(
      VariableExternalAuthInfo::class,
      "sys",
      TypeModel(
	  name = "VariableExternalAuthInfo",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 66,
      rootId = "A3N5cwBC",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "authUpdateCounter" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "lastSentTimestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "loggedInIpAddressHash" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "loggedInTimestamp" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "loggedInVerifier" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "sentCount" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      VariableExternalAuthInfo.serializer()
  ),
	
  TypeInfo(
      VerifyRegistrationCodeData::class,
      "sys",
      TypeModel(
	  name = "VerifyRegistrationCodeData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 351,
      rootId = "A3N5cwABXw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      VerifyRegistrationCodeData.serializer()
  ),
	
  TypeInfo(
      Version::class,
      "sys",
      TypeModel(
	  name = "Version",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 480,
      rootId = "A3N5cwAB4A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "operation" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "timestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "version" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("author" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
),
"authorGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = false,
	external = false
)),
	  version = 61
),
      Version.serializer()
  ),
	
  TypeInfo(
      VersionData::class,
      "sys",
      TypeModel(
	  name = "VersionData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 487,
      rootId = "A3N5cwAB5w",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "application" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "listId" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "typeId" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      VersionData.serializer()
  ),
	
  TypeInfo(
      VersionInfo::class,
      "sys",
      TypeModel(
	  name = "VersionInfo",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 237,
      rootId = "A3N5cwAA7Q",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "app" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "operation" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "referenceList" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "timestamp" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "versionData" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("author" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = false
),
"authorGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "GroupInfo",
	final = true,
	external = false
)),
	  version = 61
),
      VersionInfo.serializer()
  ),
	
  TypeInfo(
      VersionReturn::class,
      "sys",
      TypeModel(
	  name = "VersionReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 493,
      rootId = "A3N5cwAB7Q",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("versions" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "Version",
	final = false,
	external = false
)),
	  version = 61
),
      VersionReturn.serializer()
  ),
	
  TypeInfo(
      WebsocketCounterData::class,
      "sys",
      TypeModel(
	  name = "WebsocketCounterData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1492,
      rootId = "A3N5cwAF1A",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailGroup" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("counterValues" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "WebsocketCounterValue",
	final = false,
	external = false
)),
	  version = 61
),
      WebsocketCounterData.serializer()
  ),
	
  TypeInfo(
      WebsocketCounterValue::class,
      "sys",
      TypeModel(
	  name = "WebsocketCounterValue",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1488,
      rootId = "A3N5cwAF0A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "count" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailListId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 61
),
      WebsocketCounterValue.serializer()
  ),
	
  TypeInfo(
      WebsocketEntityData::class,
      "sys",
      TypeModel(
	  name = "WebsocketEntityData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1483,
      rootId = "A3N5cwAFyw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "eventBatchId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "eventBatchOwner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("eventBatch" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EntityUpdate",
	final = false,
	external = false
)),
	  version = 61
),
      WebsocketEntityData.serializer()
  ),
	
  TypeInfo(
      WhitelabelChild::class,
      "sys",
      TypeModel(
	  name = "WhitelabelChild",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 1257,
      rootId = "A3N5cwAE6Q",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "comment" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "createdDate" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "deletedDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = true,
	external = false
)),
	  version = 61
),
      WhitelabelChild.serializer()
  ),
	
  TypeInfo(
      WhitelabelChildrenRef::class,
      "sys",
      TypeModel(
	  name = "WhitelabelChildrenRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1269,
      rootId = "A3N5cwAE9Q",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "WhitelabelChild",
	final = true,
	external = false
)),
	  version = 61
),
      WhitelabelChildrenRef.serializer()
  ),
	
  TypeInfo(
      WhitelabelConfig::class,
      "sys",
      TypeModel(
	  name = "WhitelabelConfig",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 1127,
      rootId = "A3N5cwAEZw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "germanLanguageCode" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "imprintUrl" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "jsonTheme" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "metaTags" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "privacyStatementUrl" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "whitelabelCode" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bootstrapCustomizations" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "BootstrapFeature",
	final = false,
	external = false
),
"certificateInfo" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CertificateInfo",
	final = false,
	external = false
),
"whitelabelRegistrationDomains" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "StringWrapper",
	final = false,
	external = false
)),
	  version = 61
),
      WhitelabelConfig.serializer()
  ),
	
  TypeInfo(
      WhitelabelParent::class,
      "sys",
      TypeModel(
	  name = "WhitelabelParent",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1272,
      rootId = "A3N5cwAE-A",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("customer" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Customer",
	final = true,
	external = false
),
"whitelabelChildInParent" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "WhitelabelChild",
	final = true,
	external = false
)),
	  version = 61
),
      WhitelabelParent.serializer()
  ),
	
  TypeInfo(
      AttachmentKeyData::class,
      "tutanota",
      TypeModel(
	  name = "AttachmentKeyData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 542,
      rootId = "CHR1dGFub3RhAAIe",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bucketEncFileSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "fileSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("file" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      AttachmentKeyData.serializer()
  ),
	
  TypeInfo(
      Birthday::class,
      "tutanota",
      TypeModel(
	  name = "Birthday",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 844,
      rootId = "CHR1dGFub3RhAANM",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "day" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "month" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "year" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      Birthday.serializer()
  ),
	
  TypeInfo(
      CalendarDeleteData::class,
      "tutanota",
      TypeModel(
	  name = "CalendarDeleteData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 982,
      rootId = "CHR1dGFub3RhAAPW",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("groupRootId" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "CalendarGroupRoot",
	final = false,
	external = false
)),
	  version = 41
),
      CalendarDeleteData.serializer()
  ),
	
  TypeInfo(
      CalendarEvent::class,
      "tutanota",
      TypeModel(
	  name = "CalendarEvent",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 933,
      rootId = "CHR1dGFub3RhAAOl",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "description" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "endTime" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "location" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "startTime" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "summary" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "uid" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
)),
	  associations = mapOf("repeatRule" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "CalendarRepeatRule",
	final = false,
	external = false
),
"alarmInfos" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "UserAlarmInfo",
	final = false,
	external = true
)),
	  version = 41
),
      CalendarEvent.serializer()
  ),
	
  TypeInfo(
      CalendarGroupData::class,
      "tutanota",
      TypeModel(
	  name = "CalendarGroupData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 956,
      rootId = "CHR1dGFub3RhAAO8",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "adminEncGroupKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "calendarEncCalendarGroupRootSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "groupInfoEncName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "ownerEncGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = true,
	external = true
)),
	  version = 41
),
      CalendarGroupData.serializer()
  ),
	
  TypeInfo(
      CalendarGroupRoot::class,
      "tutanota",
      TypeModel(
	  name = "CalendarGroupRoot",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 947,
      rootId = "CHR1dGFub3RhAAOz",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("longEvents" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "CalendarEvent",
	final = true,
	external = false
),
"shortEvents" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "CalendarEvent",
	final = true,
	external = false
)),
	  version = 41
),
      CalendarGroupRoot.serializer()
  ),
	
  TypeInfo(
      CalendarPostData::class,
      "tutanota",
      TypeModel(
	  name = "CalendarPostData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 964,
      rootId = "CHR1dGFub3RhAAPE",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("calendarData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CalendarGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      CalendarPostData.serializer()
  ),
	
  TypeInfo(
      CalendarPostReturn::class,
      "tutanota",
      TypeModel(
	  name = "CalendarPostReturn",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 985,
      rootId = "CHR1dGFub3RhAAPZ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = true
)),
	  version = 41
),
      CalendarPostReturn.serializer()
  ),
	
  TypeInfo(
      CalendarRepeatRule::class,
      "tutanota",
      TypeModel(
	  name = "CalendarRepeatRule",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 926,
      rootId = "CHR1dGFub3RhAAOe",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "endType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "endValue" to Value(
  type = NumberType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "frequency" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "interval" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "timeZone" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      CalendarRepeatRule.serializer()
  ),
	
  TypeInfo(
      Contact::class,
      "tutanota",
      TypeModel(
	  name = "Contact",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 64,
      rootId = "CHR1dGFub3RhAEA",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "autoTransmitPassword" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "birthdayIso" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "comment" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "company" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "firstName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "lastName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "nickname" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "oldBirthdayDate" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "presharedPassword" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
),
                "role" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "title" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
)),
	  associations = mapOf("addresses" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactAddress",
	final = false,
	external = false
),
"mailAddresses" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactMailAddress",
	final = false,
	external = false
),
"oldBirthdayAggregate" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "Birthday",
	final = false,
	external = false
),
"phoneNumbers" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactPhoneNumber",
	final = false,
	external = false
),
"socialIds" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactSocialId",
	final = false,
	external = false
),
"photo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = false,
	external = false
)),
	  version = 41
),
      Contact.serializer()
  ),
	
  TypeInfo(
      ContactAddress::class,
      "tutanota",
      TypeModel(
	  name = "ContactAddress",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 54,
      rootId = "CHR1dGFub3RhADY",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "address" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "customTypeName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      ContactAddress.serializer()
  ),
	
  TypeInfo(
      ContactForm::class,
      "tutanota",
      TypeModel(
	  name = "ContactForm",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 733,
      rootId = "CHR1dGFub3RhAALd",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "path" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("languages" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactFormLanguage",
	final = false,
	external = false
),
"statisticsFields_removed" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InputField",
	final = false,
	external = false
),
"statisticsLog" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "StatisticLogRef",
	final = true,
	external = false
),
"delegationGroups_removed" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "Group",
	final = false,
	external = true
),
"participantGroupInfos" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "GroupInfo",
	final = false,
	external = true
),
"targetGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = false,
	external = true
),
"targetGroupInfo" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "GroupInfo",
	final = false,
	external = true
)),
	  version = 41
),
      ContactForm.serializer()
  ),
	
  TypeInfo(
      ContactFormAccountData::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormAccountData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 791,
      rootId = "CHR1dGFub3RhAAMX",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("statisticFields" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactFormStatisticField",
	final = false,
	external = false
),
"statistics" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "ContactFormStatisticEntry",
	final = true,
	external = false
),
"userData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "ContactFormUserData",
	final = false,
	external = false
),
"userGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
),
"contactForm" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "ContactForm",
	final = false,
	external = false
)),
	  version = 41
),
      ContactFormAccountData.serializer()
  ),
	
  TypeInfo(
      ContactFormAccountReturn::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormAccountReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 750,
      rootId = "CHR1dGFub3RhAALu",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "requestMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "responseMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      ContactFormAccountReturn.serializer()
  ),
	
  TypeInfo(
      ContactFormEncryptedStatisticsField::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormEncryptedStatisticsField",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 769,
      rootId = "CHR1dGFub3RhAAMB",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      ContactFormEncryptedStatisticsField.serializer()
  ),
	
  TypeInfo(
      ContactFormLanguage::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormLanguage",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 857,
      rootId = "CHR1dGFub3RhAANZ",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "footerHtml" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "headerHtml" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "helpHtml" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pageTitle" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("statisticsFields" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InputField",
	final = false,
	external = false
)),
	  version = 41
),
      ContactFormLanguage.serializer()
  ),
	
  TypeInfo(
      ContactFormStatisticEntry::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormStatisticEntry",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 825,
      rootId = "CHR1dGFub3RhAAM5",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bucketEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customerPubEncBucketKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customerPubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("statisticFields" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactFormStatisticField",
	final = false,
	external = false
)),
	  version = 41
),
      ContactFormStatisticEntry.serializer()
  ),
	
  TypeInfo(
      ContactFormStatisticField::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormStatisticField",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 765,
      rootId = "CHR1dGFub3RhAAL9",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "encryptedValue" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      ContactFormStatisticField.serializer()
  ),
	
  TypeInfo(
      ContactFormUserData::class,
      "tutanota",
      TypeModel(
	  name = "ContactFormUserData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 754,
      rootId = "CHR1dGFub3RhAALy",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailEncMailBoxSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "ownerEncMailGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pwEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncEntropy" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncMailGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncTutanotaPropertiesSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      ContactFormUserData.serializer()
  ),
	
  TypeInfo(
      ContactList::class,
      "tutanota",
      TypeModel(
	  name = "ContactList",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 153,
      rootId = "CHR1dGFub3RhAACZ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("photos" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "PhotosRef",
	final = false,
	external = false
),
"contacts" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Contact",
	final = true,
	external = false
)),
	  version = 41
),
      ContactList.serializer()
  ),
	
  TypeInfo(
      ContactMailAddress::class,
      "tutanota",
      TypeModel(
	  name = "ContactMailAddress",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 44,
      rootId = "CHR1dGFub3RhACw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "address" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "customTypeName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      ContactMailAddress.serializer()
  ),
	
  TypeInfo(
      ContactPhoneNumber::class,
      "tutanota",
      TypeModel(
	  name = "ContactPhoneNumber",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 49,
      rootId = "CHR1dGFub3RhADE",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customTypeName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "number" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      ContactPhoneNumber.serializer()
  ),
	
  TypeInfo(
      ContactSocialId::class,
      "tutanota",
      TypeModel(
	  name = "ContactSocialId",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 59,
      rootId = "CHR1dGFub3RhADs",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customTypeName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "socialId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      ContactSocialId.serializer()
  ),
	
  TypeInfo(
      ConversationEntry::class,
      "tutanota",
      TypeModel(
	  name = "ConversationEntry",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 84,
      rootId = "CHR1dGFub3RhAFQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "conversationType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "messageId" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("mail" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Mail",
	final = true,
	external = false
),
"previous" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "ConversationEntry",
	final = true,
	external = false
)),
	  version = 41
),
      ConversationEntry.serializer()
  ),
	
  TypeInfo(
      CreateExternalUserGroupData::class,
      "tutanota",
      TypeModel(
	  name = "CreateExternalUserGroupData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 138,
      rootId = "CHR1dGFub3RhAACK",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "internalUserEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalPwEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      CreateExternalUserGroupData.serializer()
  ),
	
  TypeInfo(
      CreateFileData::class,
      "tutanota",
      TypeModel(
	  name = "CreateFileData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 346,
      rootId = "CHR1dGFub3RhAAFa",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "fileName" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "group" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mimeType" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("fileData" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "FileData",
	final = true,
	external = false
),
"parentFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      CreateFileData.serializer()
  ),
	
  TypeInfo(
      CreateLocalAdminGroupData::class,
      "tutanota",
      TypeModel(
	  name = "CreateLocalAdminGroupData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 703,
      rootId = "CHR1dGFub3RhAAK_",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("groupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      CreateLocalAdminGroupData.serializer()
  ),
	
  TypeInfo(
      CreateMailFolderData::class,
      "tutanota",
      TypeModel(
	  name = "CreateMailFolderData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 450,
      rootId = "CHR1dGFub3RhAAHC",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "folderName" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("parentFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = true,
	external = false
)),
	  version = 41
),
      CreateMailFolderData.serializer()
  ),
	
  TypeInfo(
      CreateMailFolderReturn::class,
      "tutanota",
      TypeModel(
	  name = "CreateMailFolderReturn",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 455,
      rootId = "CHR1dGFub3RhAAHH",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("newFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = false,
	external = false
)),
	  version = 41
),
      CreateMailFolderReturn.serializer()
  ),
	
  TypeInfo(
      CreateMailGroupData::class,
      "tutanota",
      TypeModel(
	  name = "CreateMailGroupData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 707,
      rootId = "CHR1dGFub3RhAALD",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailEncMailboxSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("groupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      CreateMailGroupData.serializer()
  ),
	
  TypeInfo(
      CustomerAccountCreateData::class,
      "tutanota",
      TypeModel(
	  name = "CustomerAccountCreateData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 648,
      rootId = "CHR1dGFub3RhAAKI",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "adminEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "adminEncCustomerServerPropertiesSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "authToken" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "code" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "lang" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "systemAdminPubEncAccountingInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncAccountGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncAdminGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
),
"customerGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
),
"userData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "UserAccountUserData",
	final = false,
	external = false
),
"userGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      CustomerAccountCreateData.serializer()
  ),
	
  TypeInfo(
      CustomerContactFormGroupRoot::class,
      "tutanota",
      TypeModel(
	  name = "CustomerContactFormGroupRoot",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 783,
      rootId = "CHR1dGFub3RhAAMP",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("contactFormConversations" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "DeleteContactFormConversationIndex",
	final = true,
	external = false
),
"statisticsLog" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "UnencryptedStatisticLogRef",
	final = true,
	external = false
),
"contactForms" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "ContactForm",
	final = true,
	external = false
),
"statisticsLog_encrypted_removed" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "StatisticLogEntry",
	final = true,
	external = false
)),
	  version = 41
),
      CustomerContactFormGroupRoot.serializer()
  ),
	
  TypeInfo(
      DataBlock::class,
      "tutanota",
      TypeModel(
	  name = "DataBlock",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 0,
      rootId = "CHR1dGFub3RhAAA",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "blockData" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "size" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      DataBlock.serializer()
  ),
	
  TypeInfo(
      DeleteContactFormConversationIndex::class,
      "tutanota",
      TypeModel(
	  name = "DeleteContactFormConversationIndex",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 838,
      rootId = "CHR1dGFub3RhAANG",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "DeleteContactFormConversationIndexEntry",
	final = true,
	external = false
)),
	  version = 41
),
      DeleteContactFormConversationIndex.serializer()
  ),
	
  TypeInfo(
      DeleteContactFormConversationIndexEntry::class,
      "tutanota",
      TypeModel(
	  name = "DeleteContactFormConversationIndexEntry",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 832,
      rootId = "CHR1dGFub3RhAANA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      DeleteContactFormConversationIndexEntry.serializer()
  ),
	
  TypeInfo(
      DeleteGroupData::class,
      "tutanota",
      TypeModel(
	  name = "DeleteGroupData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 713,
      rootId = "CHR1dGFub3RhAALJ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "restore" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = true
)),
	  version = 41
),
      DeleteGroupData.serializer()
  ),
	
  TypeInfo(
      DeleteMailData::class,
      "tutanota",
      TypeModel(
	  name = "DeleteMailData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 419,
      rootId = "CHR1dGFub3RhAAGj",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("folder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "MailFolder",
	final = true,
	external = false
),
"mails" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      DeleteMailData.serializer()
  ),
	
  TypeInfo(
      DeleteMailFolderData::class,
      "tutanota",
      TypeModel(
	  name = "DeleteMailFolderData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 458,
      rootId = "CHR1dGFub3RhAAHK",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("folders" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "MailFolder",
	final = false,
	external = false
)),
	  version = 41
),
      DeleteMailFolderData.serializer()
  ),
	
  TypeInfo(
      DraftAttachment::class,
      "tutanota",
      TypeModel(
	  name = "DraftAttachment",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 491,
      rootId = "CHR1dGFub3RhAAHr",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "ownerEncFileSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("newFile" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "NewDraftAttachment",
	final = true,
	external = false
),
"existingFile" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      DraftAttachment.serializer()
  ),
	
  TypeInfo(
      DraftCreateData::class,
      "tutanota",
      TypeModel(
	  name = "DraftCreateData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 508,
      rootId = "CHR1dGFub3RhAAH8",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "conversationType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "previousMessageId" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "symEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("draftData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "DraftData",
	final = false,
	external = false
)),
	  version = 41
),
      DraftCreateData.serializer()
  ),
	
  TypeInfo(
      DraftCreateReturn::class,
      "tutanota",
      TypeModel(
	  name = "DraftCreateReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 516,
      rootId = "CHR1dGFub3RhAAIE",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("draft" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      DraftCreateReturn.serializer()
  ),
	
  TypeInfo(
      DraftData::class,
      "tutanota",
      TypeModel(
	  name = "DraftData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 496,
      rootId = "CHR1dGFub3RhAAHw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bodyText" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "confidential" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "senderMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "senderName" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "subject" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("addedAttachments" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DraftAttachment",
	final = true,
	external = false
),
"bccRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DraftRecipient",
	final = true,
	external = false
),
"ccRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DraftRecipient",
	final = true,
	external = false
),
"replyTos" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EncryptedMailAddress",
	final = false,
	external = false
),
"toRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DraftRecipient",
	final = true,
	external = false
),
"removedAttachments" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      DraftData.serializer()
  ),
	
  TypeInfo(
      DraftRecipient::class,
      "tutanota",
      TypeModel(
	  name = "DraftRecipient",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 482,
      rootId = "CHR1dGFub3RhAAHi",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      DraftRecipient.serializer()
  ),
	
  TypeInfo(
      DraftUpdateData::class,
      "tutanota",
      TypeModel(
	  name = "DraftUpdateData",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 519,
      rootId = "CHR1dGFub3RhAAIH",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("draftData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "DraftData",
	final = false,
	external = false
),
"draft" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      DraftUpdateData.serializer()
  ),
	
  TypeInfo(
      DraftUpdateReturn::class,
      "tutanota",
      TypeModel(
	  name = "DraftUpdateReturn",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 523,
      rootId = "CHR1dGFub3RhAAIL",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("attachments" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      DraftUpdateReturn.serializer()
  ),
	
  TypeInfo(
      EncryptTutanotaPropertiesData::class,
      "tutanota",
      TypeModel(
	  name = "EncryptTutanotaPropertiesData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 473,
      rootId = "CHR1dGFub3RhAAHZ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "symEncSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("properties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "TutanotaProperties",
	final = false,
	external = false
)),
	  version = 41
),
      EncryptTutanotaPropertiesData.serializer()
  ),
	
  TypeInfo(
      EncryptedMailAddress::class,
      "tutanota",
      TypeModel(
	  name = "EncryptedMailAddress",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 612,
      rootId = "CHR1dGFub3RhAAJk",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "address" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      EncryptedMailAddress.serializer()
  ),
	
  TypeInfo(
      ExternalUserData::class,
      "tutanota",
      TypeModel(
	  name = "ExternalUserData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 145,
      rootId = "CHR1dGFub3RhAACR",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalMailEncMailBoxSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalMailEncMailGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalUserEncEntropy" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalUserEncMailGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalUserEncTutanotaPropertiesSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "externalUserEncUserGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "internalMailEncMailGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "internalMailEncUserGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "CreateExternalUserGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      ExternalUserData.serializer()
  ),
	
  TypeInfo(
      TutanotaFile::class,
      "tutanota",
      TypeModel(
	  name = "File",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 13,
      rootId = "CHR1dGFub3RhAA0",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "cid" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "mimeType" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "size" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("subFiles" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "Subfiles",
	final = true,
	external = false
),
"data" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "FileData",
	final = true,
	external = false
),
"parent" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      TutanotaFile.serializer()
  ),
	
  TypeInfo(
      FileData::class,
      "tutanota",
      TypeModel(
	  name = "FileData",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 4,
      rootId = "CHR1dGFub3RhAAQ",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "size" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "unreferenced" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("blocks" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "DataBlock",
	final = false,
	external = false
)),
	  version = 41
),
      FileData.serializer()
  ),
	
  TypeInfo(
      FileDataDataGet::class,
      "tutanota",
      TypeModel(
	  name = "FileDataDataGet",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 331,
      rootId = "CHR1dGFub3RhAAFL",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "base64" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("file" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = false,
	external = false
)),
	  version = 41
),
      FileDataDataGet.serializer()
  ),
	
  TypeInfo(
      FileDataDataPost::class,
      "tutanota",
      TypeModel(
	  name = "FileDataDataPost",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 335,
      rootId = "CHR1dGFub3RhAAFP",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "group" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "size" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      FileDataDataPost.serializer()
  ),
	
  TypeInfo(
      FileDataDataReturn::class,
      "tutanota",
      TypeModel(
	  name = "FileDataDataReturn",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 339,
      rootId = "CHR1dGFub3RhAAFT",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "size" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      FileDataDataReturn.serializer()
  ),
	
  TypeInfo(
      FileDataReturnPost::class,
      "tutanota",
      TypeModel(
	  name = "FileDataReturnPost",
	  encrypted = true,
      type = DATA_TRANSFER_TYPE,
      id = 342,
      rootId = "CHR1dGFub3RhAAFW",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("fileData" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "FileData",
	final = false,
	external = false
)),
	  version = 41
),
      FileDataReturnPost.serializer()
  ),
	
  TypeInfo(
      FileSystem::class,
      "tutanota",
      TypeModel(
	  name = "FileSystem",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 28,
      rootId = "CHR1dGFub3RhABw",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("files" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      FileSystem.serializer()
  ),
	
  TypeInfo(
      GroupInvitationDeleteData::class,
      "tutanota",
      TypeModel(
	  name = "GroupInvitationDeleteData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1016,
      rootId = "CHR1dGFub3RhAAP4",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("receivedInvitation" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "ReceivedGroupInvitation",
	final = false,
	external = true
)),
	  version = 41
),
      GroupInvitationDeleteData.serializer()
  ),
	
  TypeInfo(
      GroupInvitationPostData::class,
      "tutanota",
      TypeModel(
	  name = "GroupInvitationPostData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1002,
      rootId = "CHR1dGFub3RhAAPq",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("internalKeyData" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InternalRecipientKeyData",
	final = false,
	external = false
),
"sharedGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "SharedGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      GroupInvitationPostData.serializer()
  ),
	
  TypeInfo(
      GroupInvitationPostReturn::class,
      "tutanota",
      TypeModel(
	  name = "GroupInvitationPostReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1006,
      rootId = "CHR1dGFub3RhAAPu",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("existingMailAddresses" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = false,
	external = false
),
"invalidMailAddresses" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = false,
	external = false
),
"invitedMailAddresses" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = false,
	external = false
)),
	  version = 41
),
      GroupInvitationPostReturn.serializer()
  ),
	
  TypeInfo(
      GroupInvitationPutData::class,
      "tutanota",
      TypeModel(
	  name = "GroupInvitationPutData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1011,
      rootId = "CHR1dGFub3RhAAPz",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sharedGroupEncInviteeGroupInfoKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "userGroupEncGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("receivedInvitation" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "ReceivedGroupInvitation",
	final = false,
	external = true
)),
	  version = 41
),
      GroupInvitationPutData.serializer()
  ),
	
  TypeInfo(
      GroupSettings::class,
      "tutanota",
      TypeModel(
	  name = "GroupSettings",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 968,
      rootId = "CHR1dGFub3RhAAPI",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "color" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "name" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = true
)),
	  associations = mapOf("group" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Group",
	final = true,
	external = true
)),
	  version = 41
),
      GroupSettings.serializer()
  ),
	
  TypeInfo(
      ImapFolder::class,
      "tutanota",
      TypeModel(
	  name = "ImapFolder",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 190,
      rootId = "CHR1dGFub3RhAAC-",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "lastseenuid" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "uidvalidity" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("syncInfo" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "RemoteImapSyncInfo",
	final = true,
	external = false
)),
	  version = 41
),
      ImapFolder.serializer()
  ),
	
  TypeInfo(
      ImapSyncConfiguration::class,
      "tutanota",
      TypeModel(
	  name = "ImapSyncConfiguration",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 209,
      rootId = "CHR1dGFub3RhAADR",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "host" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "password" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "port" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "user" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("imapSyncState" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "ImapSyncState",
	final = false,
	external = false
)),
	  version = 41
),
      ImapSyncConfiguration.serializer()
  ),
	
  TypeInfo(
      ImapSyncState::class,
      "tutanota",
      TypeModel(
	  name = "ImapSyncState",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 196,
      rootId = "CHR1dGFub3RhAADE",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("folders" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ImapFolder",
	final = false,
	external = false
)),
	  version = 41
),
      ImapSyncState.serializer()
  ),
	
  TypeInfo(
      InboxRule::class,
      "tutanota",
      TypeModel(
	  name = "InboxRule",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 573,
      rootId = "CHR1dGFub3RhAAI9",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "type" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "value" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("targetFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = false,
	external = false
)),
	  version = 41
),
      InboxRule.serializer()
  ),
	
  TypeInfo(
      InputField::class,
      "tutanota",
      TypeModel(
	  name = "InputField",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 728,
      rootId = "CHR1dGFub3RhAALY",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "type" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("enumValues" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "Name",
	final = false,
	external = false
)),
	  version = 41
),
      InputField.serializer()
  ),
	
  TypeInfo(
      InternalGroupData::class,
      "tutanota",
      TypeModel(
	  name = "InternalGroupData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 642,
      rootId = "CHR1dGFub3RhAAKC",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "adminEncGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "groupEncPrivateKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "ownerEncGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "publicKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("adminGroup" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Group",
	final = true,
	external = true
)),
	  version = 41
),
      InternalGroupData.serializer()
  ),
	
  TypeInfo(
      InternalRecipientKeyData::class,
      "tutanota",
      TypeModel(
	  name = "InternalRecipientKeyData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 527,
      rootId = "CHR1dGFub3RhAAIP",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "pubEncBucketKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "pubKeyVersion" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      InternalRecipientKeyData.serializer()
  ),
	
  TypeInfo(
      ListUnsubscribeData::class,
      "tutanota",
      TypeModel(
	  name = "ListUnsubscribeData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 867,
      rootId = "CHR1dGFub3RhAANj",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "headers" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recipient" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("mail" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      ListUnsubscribeData.serializer()
  ),
	
  TypeInfo(
      Mail::class,
      "tutanota",
      TypeModel(
	  name = "Mail",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 97,
      rootId = "CHR1dGFub3RhAGE",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "authStatus" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "confidential" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "differentEnvelopeSender" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "listUnsubscribe" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "movedTime" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "phishingStatus" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "receivedDate" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "replyType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "sentDate" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "state" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "subject" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
),
                "trashed" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "unread" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("bccRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = true,
	external = false
),
"ccRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = true,
	external = false
),
"replyTos" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "EncryptedMailAddress",
	final = true,
	external = false
),
"restrictions" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "MailRestriction",
	final = true,
	external = false
),
"sender" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "MailAddress",
	final = true,
	external = false
),
"toRecipients" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "MailAddress",
	final = true,
	external = false
),
"attachments" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "File",
	final = true,
	external = false
),
"body" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailBody",
	final = true,
	external = false
),
"conversationEntry" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "ConversationEntry",
	final = true,
	external = false
),
"headers" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "MailHeaders",
	final = true,
	external = false
)),
	  version = 41
),
      Mail.serializer()
  ),
	
  TypeInfo(
      MailAddress::class,
      "tutanota",
      TypeModel(
	  name = "MailAddress",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 92,
      rootId = "CHR1dGFub3RhAFw",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "address" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = true
)),
	  associations = mapOf("contact" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Contact",
	final = false,
	external = false
)),
	  version = 41
),
      MailAddress.serializer()
  ),
	
  TypeInfo(
      MailBody::class,
      "tutanota",
      TypeModel(
	  name = "MailBody",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 36,
      rootId = "CHR1dGFub3RhACQ",
      values = mapOf(                "_area" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_owner" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "compressedText" to Value(
  type = CompressedStringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "text" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      MailBody.serializer()
  ),
	
  TypeInfo(
      MailBox::class,
      "tutanota",
      TypeModel(
	  name = "MailBox",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 125,
      rootId = "CHR1dGFub3RhAH0",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "lastInfoDate" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "symEncShareBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("systemFolders" to Association(
	type = AGGREGATION,
  cardinality = ZeroOrOne,
	refType = "MailFolderRef",
	final = true,
	external = false
),
"mails" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = true,
	external = false
),
"receivedAttachments" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
),
"sentAttachments" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      MailBox.serializer()
  ),
	
  TypeInfo(
      MailFolder::class,
      "tutanota",
      TypeModel(
	  name = "MailFolder",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 429,
      rootId = "CHR1dGFub3RhAAGt",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "folderType" to Value(
  type = NumberType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("mails" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = true,
	external = false
),
"parentFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "MailFolder",
	final = true,
	external = false
),
"subFolders" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = true,
	external = false
)),
	  version = 41
),
      MailFolder.serializer()
  ),
	
  TypeInfo(
      MailFolderRef::class,
      "tutanota",
      TypeModel(
	  name = "MailFolderRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 440,
      rootId = "CHR1dGFub3RhAAG4",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("folders" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = true,
	external = false
)),
	  version = 41
),
      MailFolderRef.serializer()
  ),
	
  TypeInfo(
      MailHeaders::class,
      "tutanota",
      TypeModel(
	  name = "MailHeaders",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 604,
      rootId = "CHR1dGFub3RhAAJc",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "compressedHeaders" to Value(
  type = CompressedStringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
),
                "headers" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = true
)),
	  associations = mapOf(),
	  version = 41
),
      MailHeaders.serializer()
  ),
	
  TypeInfo(
      MailRestriction::class,
      "tutanota",
      TypeModel(
	  name = "MailRestriction",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 719,
      rootId = "CHR1dGFub3RhAALP",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("delegationGroups_removed" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "Group",
	final = true,
	external = true
),
"participantGroupInfos" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "GroupInfo",
	final = true,
	external = true
)),
	  version = 41
),
      MailRestriction.serializer()
  ),
	
  TypeInfo(
      MailboxGroupRoot::class,
      "tutanota",
      TypeModel(
	  name = "MailboxGroupRoot",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 693,
      rootId = "CHR1dGFub3RhAAK1",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("contactFormUserContactForm" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "ContactForm",
	final = true,
	external = false
),
"mailbox" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailBox",
	final = true,
	external = false
),
"participatingContactForms" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "ContactForm",
	final = false,
	external = false
),
"serverProperties" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailboxServerProperties",
	final = true,
	external = false
),
"targetMailGroupContactForm" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "ContactForm",
	final = true,
	external = false
),
"whitelistRequests" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "WhitelistRequest",
	final = true,
	external = false
)),
	  version = 41
),
      MailboxGroupRoot.serializer()
  ),
	
  TypeInfo(
      MailboxServerProperties::class,
      "tutanota",
      TypeModel(
	  name = "MailboxServerProperties",
	  encrypted = false,
      type = ELEMENT_TYPE,
      id = 677,
      rootId = "CHR1dGFub3RhAAKl",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "whitelistProtectionEnabled" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      MailboxServerProperties.serializer()
  ),
	
  TypeInfo(
      MoveMailData::class,
      "tutanota",
      TypeModel(
	  name = "MoveMailData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 445,
      rootId = "CHR1dGFub3RhAAG9",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("mails" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = Any,
	refType = "Mail",
	final = false,
	external = false
),
"targetFolder" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "MailFolder",
	final = false,
	external = false
)),
	  version = 41
),
      MoveMailData.serializer()
  ),
	
  TypeInfo(
      Name::class,
      "tutanota",
      TypeModel(
	  name = "Name",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 725,
      rootId = "CHR1dGFub3RhAALV",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "name" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      Name.serializer()
  ),
	
  TypeInfo(
      NewDraftAttachment::class,
      "tutanota",
      TypeModel(
	  name = "NewDraftAttachment",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 486,
      rootId = "CHR1dGFub3RhAAHm",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "encCid" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "encFileName" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "encMimeType" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("fileData" to Association(
	type = ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "FileData",
	final = true,
	external = false
)),
	  version = 41
),
      NewDraftAttachment.serializer()
  ),
	
  TypeInfo(
      NotificationMail::class,
      "tutanota",
      TypeModel(
	  name = "NotificationMail",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 223,
      rootId = "CHR1dGFub3RhAADf",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bodyText" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailboxLink" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recipientMailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recipientName" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "subject" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      NotificationMail.serializer()
  ),
	
  TypeInfo(
      PasswordAutoAuthenticationReturn::class,
      "tutanota",
      TypeModel(
	  name = "PasswordAutoAuthenticationReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 317,
      rootId = "CHR1dGFub3RhAAE9",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordAutoAuthenticationReturn.serializer()
  ),
	
  TypeInfo(
      PasswordChannelPhoneNumber::class,
      "tutanota",
      TypeModel(
	  name = "PasswordChannelPhoneNumber",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 135,
      rootId = "CHR1dGFub3RhAACH",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "number" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordChannelPhoneNumber.serializer()
  ),
	
  TypeInfo(
      PasswordChannelReturn::class,
      "tutanota",
      TypeModel(
	  name = "PasswordChannelReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 327,
      rootId = "CHR1dGFub3RhAAFH",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("phoneNumberChannels" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PasswordChannelPhoneNumber",
	final = true,
	external = false
)),
	  version = 41
),
      PasswordChannelReturn.serializer()
  ),
	
  TypeInfo(
      PasswordMessagingData::class,
      "tutanota",
      TypeModel(
	  name = "PasswordMessagingData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 308,
      rootId = "CHR1dGFub3RhAAE0",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "language" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "numberId" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "symKeyForPasswordTransmission" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordMessagingData.serializer()
  ),
	
  TypeInfo(
      PasswordMessagingReturn::class,
      "tutanota",
      TypeModel(
	  name = "PasswordMessagingReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 313,
      rootId = "CHR1dGFub3RhAAE5",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "autoAuthenticationId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordMessagingReturn.serializer()
  ),
	
  TypeInfo(
      PasswordRetrievalData::class,
      "tutanota",
      TypeModel(
	  name = "PasswordRetrievalData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 320,
      rootId = "CHR1dGFub3RhAAFA",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "autoAuthenticationId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordRetrievalData.serializer()
  ),
	
  TypeInfo(
      PasswordRetrievalReturn::class,
      "tutanota",
      TypeModel(
	  name = "PasswordRetrievalReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 323,
      rootId = "CHR1dGFub3RhAAFD",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "transmissionKeyEncryptedPassword" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PasswordRetrievalReturn.serializer()
  ),
	
  TypeInfo(
      PhishingMarker::class,
      "tutanota",
      TypeModel(
	  name = "PhishingMarker",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 1023,
      rootId = "CHR1dGFub3RhAAP_",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "marker" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "status" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      PhishingMarker.serializer()
  ),
	
  TypeInfo(
      PhishingMarkerWebsocketData::class,
      "tutanota",
      TypeModel(
	  name = "PhishingMarkerWebsocketData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1034,
      rootId = "CHR1dGFub3RhAAQK",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "lastId" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("markers" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PhishingMarker",
	final = false,
	external = false
)),
	  version = 41
),
      PhishingMarkerWebsocketData.serializer()
  ),
	
  TypeInfo(
      PhotosRef::class,
      "tutanota",
      TypeModel(
	  name = "PhotosRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 853,
      rootId = "CHR1dGFub3RhAANV",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("files" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      PhotosRef.serializer()
  ),
	
  TypeInfo(
      ReceiveInfoServiceData::class,
      "tutanota",
      TypeModel(
	  name = "ReceiveInfoServiceData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 570,
      rootId = "CHR1dGFub3RhAAI6",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      ReceiveInfoServiceData.serializer()
  ),
	
  TypeInfo(
      RemoteImapSyncInfo::class,
      "tutanota",
      TypeModel(
	  name = "RemoteImapSyncInfo",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 183,
      rootId = "CHR1dGFub3RhAAC3",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "seen" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("message" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      RemoteImapSyncInfo.serializer()
  ),
	
  TypeInfo(
      ReportPhishingPostData::class,
      "tutanota",
      TypeModel(
	  name = "ReportPhishingPostData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 1066,
      rootId = "CHR1dGFub3RhAAQq",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "reportType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("mailId" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      ReportPhishingPostData.serializer()
  ),
	
  TypeInfo(
      SecureExternalRecipientKeyData::class,
      "tutanota",
      TypeModel(
	  name = "SecureExternalRecipientKeyData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 532,
      rootId = "CHR1dGFub3RhAAIU",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "autoTransmitPassword" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "ownerEncBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "passwordVerifier" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "pwEncCommunicationKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "saltHash" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "symEncBucketKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("passwordChannelPhoneNumbers" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "PasswordChannelPhoneNumber",
	final = true,
	external = false
)),
	  version = 41
),
      SecureExternalRecipientKeyData.serializer()
  ),
	
  TypeInfo(
      SendDraftData::class,
      "tutanota",
      TypeModel(
	  name = "SendDraftData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 547,
      rootId = "CHR1dGFub3RhAAIj",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "bucketEncMailSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "language" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "mailSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "plaintext" to Value(
  type = BooleanType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "senderNameUnencrypted" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
)),
	  associations = mapOf("attachmentKeyData" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "AttachmentKeyData",
	final = true,
	external = false
),
"internalRecipientKeyData" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InternalRecipientKeyData",
	final = true,
	external = false
),
"secureExternalRecipientKeyData" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "SecureExternalRecipientKeyData",
	final = true,
	external = false
),
"mail" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = true,
	external = false
)),
	  version = 41
),
      SendDraftData.serializer()
  ),
	
  TypeInfo(
      SendDraftReturn::class,
      "tutanota",
      TypeModel(
	  name = "SendDraftReturn",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 557,
      rootId = "CHR1dGFub3RhAAIt",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "messageId" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sentDate" to Value(
  type = DateType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf("notifications" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "NotificationMail",
	final = false,
	external = false
),
"sentMail" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "Mail",
	final = true,
	external = false
)),
	  version = 41
),
      SendDraftReturn.serializer()
  ),
	
  TypeInfo(
      SharedGroupData::class,
      "tutanota",
      TypeModel(
	  name = "SharedGroupData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 992,
      rootId = "CHR1dGFub3RhAAPg",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "bucketEncInvitationSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "capability" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sessionEncInviterName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sessionEncSharedGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sessionEncSharedGroupName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sharedGroup" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "sharedGroupEncInviterGroupInfoKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "sharedGroupEncSharedGroupInfoKey" to Value(
  type = BytesType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      SharedGroupData.serializer()
  ),
	
  TypeInfo(
      StatisticLogEntry::class,
      "tutanota",
      TypeModel(
	  name = "StatisticLogEntry",
	  encrypted = true,
      type = LIST_ELEMENT_TYPE,
      id = 773,
      rootId = "CHR1dGFub3RhAAMF",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("values" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ContactFormEncryptedStatisticsField",
	final = true,
	external = false
),
"contactForm" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = One,
	refType = "ContactForm",
	final = true,
	external = false
)),
	  version = 41
),
      StatisticLogEntry.serializer()
  ),
	
  TypeInfo(
      StatisticLogRef::class,
      "tutanota",
      TypeModel(
	  name = "StatisticLogRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 875,
      rootId = "CHR1dGFub3RhAANr",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "StatisticLogEntry",
	final = true,
	external = false
)),
	  version = 41
),
      StatisticLogRef.serializer()
  ),
	
  TypeInfo(
      Subfiles::class,
      "tutanota",
      TypeModel(
	  name = "Subfiles",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 11,
      rootId = "CHR1dGFub3RhAAs",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("files" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "File",
	final = true,
	external = false
)),
	  version = 41
),
      Subfiles.serializer()
  ),
	
  TypeInfo(
      TutanotaProperties::class,
      "tutanota",
      TypeModel(
	  name = "TutanotaProperties",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 216,
      rootId = "CHR1dGFub3RhAADY",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "customEmailSignature" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "defaultSender" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "defaultUnconfidential" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "emailSignatureType" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "groupEncEntropy" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "lastSeenAnnouncement" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "noAutomaticContacts" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "notificationMailLanguage" to Value(
  type = StringType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
),
                "sendPlaintextOnly" to Value(
  type = BooleanType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("imapSyncConfig" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "ImapSyncConfiguration",
	final = false,
	external = false
),
"inboxRules" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "InboxRule",
	final = false,
	external = false
),
"lastPushedMail" to Association(
	type = LIST_ELEMENT_ASSOCIATION,
  cardinality = ZeroOrOne,
	refType = "Mail",
	final = false,
	external = false
)),
	  version = 41
),
      TutanotaProperties.serializer()
  ),
	
  TypeInfo(
      UnencryptedStatisticLogEntry::class,
      "tutanota",
      TypeModel(
	  name = "UnencryptedStatisticLogEntry",
	  encrypted = false,
      type = LIST_ELEMENT_TYPE,
      id = 879,
      rootId = "CHR1dGFub3RhAANv",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "contactFormPath" to Value(
  type = StringType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      UnencryptedStatisticLogEntry.serializer()
  ),
	
  TypeInfo(
      UnencryptedStatisticLogRef::class,
      "tutanota",
      TypeModel(
	  name = "UnencryptedStatisticLogRef",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 887,
      rootId = "CHR1dGFub3RhAAN3",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
)),
	  associations = mapOf("items" to Association(
	type = LIST_ASSOCIATION,
  cardinality = One,
	refType = "UnencryptedStatisticLogEntry",
	final = true,
	external = false
)),
	  version = 41
),
      UnencryptedStatisticLogRef.serializer()
  ),
	
  TypeInfo(
      UserAccountCreateData::class,
      "tutanota",
      TypeModel(
	  name = "UserAccountCreateData",
	  encrypted = false,
      type = DATA_TRANSFER_TYPE,
      id = 663,
      rootId = "CHR1dGFub3RhAAKX",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "date" to Value(
  type = DateType,
  cardinality = ZeroOrOne,
  final = false,
  encrypted = false
)),
	  associations = mapOf("userData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "UserAccountUserData",
	final = false,
	external = false
),
"userGroupData" to Association(
	type = AGGREGATION,
  cardinality = One,
	refType = "InternalGroupData",
	final = false,
	external = false
)),
	  version = 41
),
      UserAccountCreateData.serializer()
  ),
	
  TypeInfo(
      UserAccountUserData::class,
      "tutanota",
      TypeModel(
	  name = "UserAccountUserData",
	  encrypted = false,
      type = AGGREGATED_TYPE,
      id = 622,
      rootId = "CHR1dGFub3RhAAJu",
      values = mapOf(                "_id" to Value(
  type = CustomIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "contactEncContactListSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerEncContactGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerEncFileGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "customerEncMailGroupInfoSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "encryptedName" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "fileEncFileSystemSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailAddress" to Value(
  type = StringType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "mailEncMailBoxSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "pwEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recoverCodeEncUserGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "recoverCodeVerifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "salt" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncClientKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncContactGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncCustomerGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncEntropy" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncFileGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncMailGroupKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncRecoverCode" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "userEncTutanotaPropertiesSessionKey" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "verifier" to Value(
  type = BytesType,
  cardinality = One,
  final = false,
  encrypted = false
)),
	  associations = mapOf(),
	  version = 41
),
      UserAccountUserData.serializer()
  ),
	
  TypeInfo(
      UserSettingsGroupRoot::class,
      "tutanota",
      TypeModel(
	  name = "UserSettingsGroupRoot",
	  encrypted = true,
      type = ELEMENT_TYPE,
      id = 972,
      rootId = "CHR1dGFub3RhAAPM",
      values = mapOf(                "_format" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = false
),
                "_id" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "_ownerEncSessionKey" to Value(
  type = BytesType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_ownerGroup" to Value(
  type = GeneratedIdType,
  cardinality = ZeroOrOne,
  final = true,
  encrypted = false
),
                "_permissions" to Value(
  type = GeneratedIdType,
  cardinality = One,
  final = true,
  encrypted = false
),
                "startOfTheWeek" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
),
                "timeFormat" to Value(
  type = NumberType,
  cardinality = One,
  final = false,
  encrypted = true
)),
	  associations = mapOf("groupSettings" to Association(
	type = AGGREGATION,
  cardinality = Any,
	refType = "GroupSettings",
	final = false,
	external = false
)),
	  version = 41
),
      UserSettingsGroupRoot.serializer()
  )
)