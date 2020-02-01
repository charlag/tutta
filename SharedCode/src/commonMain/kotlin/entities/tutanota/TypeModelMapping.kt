package com.charlag.tuta.entities.tutanota

import com.charlag.tuta.entities.tutanota.*
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.MetamodelType.*
import com.charlag.tuta.entities.Cardinality.*
import com.charlag.tuta.entities.ValueType.*
import com.charlag.tuta.entities.AssociationType.*

val typeInfos = listOf(

    TypeInfo(
        AttachmentKeyData::class,
        "tutanota",
        TypeModel(
            name = "AttachmentKeyData",
            encrypted = false,
            type = AGGREGATED_TYPE,
            id = 542,
            rootId = "CHR1dGFub3RhAAIe",
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "file" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "File",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "groupRootId" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "CalendarGroupRoot",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "repeatRule" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "adminGroup" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = ZeroOrOne,
                    refType = "Group",
                    final = true,
                    external = true
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "longEvents" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "calendarData" to Association(
                    type = AGGREGATION,
                    cardinality = One,
                    refType = "CalendarGroupData",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "group" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Group",
                    final = true,
                    external = true
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_area" to Value(
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
                "oldBirthday" to Value(
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
                )
            ),
            associations = mapOf(
                "addresses" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "ContactAddress",
                    final = false,
                    external = false
                ),
                "birthday" to Association(
                    type = AGGREGATION,
                    cardinality = ZeroOrOne,
                    refType = "Birthday",
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "languages" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "statisticFields" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "statisticsFields" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "InputField",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "statisticFields" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "ContactFormStatisticField",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "photos" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "mail" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "fileData" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "groupData" to Association(
                    type = AGGREGATION,
                    cardinality = One,
                    refType = "InternalGroupData",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "parentFolder" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "MailFolder",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "newFolder" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "MailFolder",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "groupData" to Association(
                    type = AGGREGATION,
                    cardinality = One,
                    refType = "InternalGroupData",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "adminGroupData" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "contactFormConversations" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "items" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "DeleteContactFormConversationIndexEntry",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "group" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Group",
                    final = true,
                    external = true
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "folder" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "folders" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = Any,
                    refType = "MailFolder",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "newFile" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "draftData" to Association(
                    type = AGGREGATION,
                    cardinality = One,
                    refType = "DraftData",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "draft" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Mail",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "addedAttachments" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "draftData" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "attachments" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = Any,
                    refType = "File",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "properties" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "TutanotaProperties",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "userGroupData" to Association(
                    type = AGGREGATION,
                    cardinality = One,
                    refType = "CreateExternalUserGroupData",
                    final = false,
                    external = false
                )
            ),
            version = 36
        ),
        ExternalUserData.serializer()
    ),

    TypeInfo(
        File::class,
        "tutanota",
        TypeModel(
            name = "File",
            encrypted = true,
            type = LIST_ELEMENT_TYPE,
            id = 13,
            rootId = "CHR1dGFub3RhAA0",
            values = mapOf(
                "_area" to Value(
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
                )
            ),
            associations = mapOf(
                "subFiles" to Association(
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
                )
            ),
            version = 36
        ),
        File.serializer()
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "blocks" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "DataBlock",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "file" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "File",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "fileData" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "FileData",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "files" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "File",
                    final = true,
                    external = false
                )
            ),
            version = 36
        ),
        FileSystem.serializer()
    ),

    TypeInfo(
        GroupColor::class,
        "tutanota",
        TypeModel(
            name = "GroupColor",
            encrypted = false,
            type = AGGREGATED_TYPE,
            id = 968,
            rootId = "CHR1dGFub3RhAAPI",
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "group" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Group",
                    final = true,
                    external = true
                )
            ),
            version = 36
        ),
        GroupColor.serializer()
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "syncInfo" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "RemoteImapSyncInfo",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "imapSyncState" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = ZeroOrOne,
                    refType = "ImapSyncState",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "folders" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "ImapFolder",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "targetFolder" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "MailFolder",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "enumValues" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "Name",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "adminGroup" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = ZeroOrOne,
                    refType = "Group",
                    final = true,
                    external = true
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "mail" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Mail",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_area" to Value(
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
                )
            ),
            associations = mapOf(
                "bccRecipients" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "contact" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = ZeroOrOne,
                    refType = "Contact",
                    final = false,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_area" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "systemFolders" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "mails" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "folders" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "MailFolder",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "delegationGroups_removed" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "contactFormUserContactForm" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "mails" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "fileData" to Association(
                    type = ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "FileData",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "phoneNumberChannels" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "PasswordChannelPhoneNumber",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
        ),
        PasswordRetrievalReturn.serializer()
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "files" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "File",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
                    type = NumberType,
                    cardinality = One,
                    final = false,
                    encrypted = false
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "message" to Association(
                    type = LIST_ELEMENT_ASSOCIATION,
                    cardinality = One,
                    refType = "Mail",
                    final = false,
                    external = false
                )
            ),
            version = 36
        ),
        RemoteImapSyncInfo.serializer()
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(
                "passwordChannelPhoneNumbers" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "PasswordChannelPhoneNumber",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "attachmentKeyData" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
                "notifications" to Association(
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
                )
            ),
            version = 36
        ),
        SendDraftReturn.serializer()
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "values" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "items" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "StatisticLogEntry",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "files" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "File",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "imapSyncConfig" to Association(
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
                )
            ),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
                "_id" to Value(
                    type = CustomIdType,
                    cardinality = One,
                    final = true,
                    encrypted = false
                )
            ),
            associations = mapOf(
                "items" to Association(
                    type = LIST_ASSOCIATION,
                    cardinality = One,
                    refType = "UnencryptedStatisticLogEntry",
                    final = true,
                    external = false
                )
            ),
            version = 36
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
            values = mapOf(
                "_format" to Value(
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
                )
            ),
            associations = mapOf(
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
                )
            ),
            version = 36
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
            values = mapOf(
                "_id" to Value(
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
                )
            ),
            associations = mapOf(),
            version = 36
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
            values = mapOf(
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
                )
            ),
            associations = mapOf(
                "groupColors" to Association(
                    type = AGGREGATION,
                    cardinality = Any,
                    refType = "GroupColor",
                    final = false,
                    external = false
                )
            ),
            version = 36
        ),
        UserSettingsGroupRoot.serializer()
    )
)