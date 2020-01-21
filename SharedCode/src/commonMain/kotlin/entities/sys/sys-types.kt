@file:UseSerializers(
    ByteArraySerializer::class,
    DateSerializer::class
)

package com.charlag.tuta.entities.sys

import com.charlag.tuta.entities.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringSerializer

@Serializable
data class IdTuple(val listId: Id, val elementId: Id) {
    @Serializer(forClass = IdTuple::class)
    companion object : KSerializer<IdTuple> {
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("IdTuple") {
            override val kind: SerialKind = StructureKind.LIST
        }

        override fun serialize(encoder: Encoder, obj: IdTuple) {
            val listDecoder = encoder.beginCollection(
                ArrayListSerializer(StringSerializer).descriptor,
                2
            )
            listDecoder.encodeStringElement(descriptor, 0, obj.listId.asString())
            listDecoder.encodeStringElement(descriptor, 1, obj.elementId.asString())
            listDecoder.endStructure(descriptor)
        }

        override fun deserialize(decoder: Decoder): IdTuple {
            val listDecoder =
                decoder.beginStructure(ArrayListSerializer(StringSerializer).descriptor)
            val idTuple = IdTuple(
                GeneratedId(listDecoder.decodeStringElement(descriptor, 0)),
                GeneratedId(listDecoder.decodeStringElement(descriptor, 1))
            )
            listDecoder.endStructure(descriptor)
            return idTuple
        }
    }
}

@Serializable
data class KeyPair(
    val _id: Id,
    val pubKey: ByteArray,
    val symEncPrivKey: ByteArray,
    val version: Long
)

@Serializable
data class Group(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val adminGroupEncGKey: ByteArray?,
    val enabled: Boolean,
    val external: Boolean,
    val type: Long,

    val administratedGroups: AdministratedGroupsRef?,
    val keys: List<KeyPair>,
    val admin: Id?,
    val customer: Id?,
    val groupInfo: IdTuple,
    val invitations: Id,
    val members: Id,
    val user: Id?
) : Entity

@Serializable
data class GroupInfo(
    val _id: IdTuple,
    val _listEncSessionKey: ByteArray?,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val created: Date,
    val deleted: Date?,
    val groupType: Long?,
    val mailAddress: String?,
    val name: String,

    val mailAddressAliases: List<MailAddressAlias>,
    val group: Id,
    val localAdmin: Id?
) : Entity

@Serializable
data class GroupMembership(
    val _id: Id,
    val admin: Boolean,
    val groupType: Long?,
    val symEncGKey: ByteArray,

    val group: Id,
    val groupInfo: IdTuple,
    val groupMember: IdTuple
)

@Serializable
data class Customer(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val approvalStatus: Long,
    val canceledPremiumAccount: Boolean,
    val orderProcessingAgreementNeeded: Boolean,
    val type: Long,

    val auditLog: AuditLogRef?,
    val contactFormUserAreaGroups: UserAreaGroups?,
    val contactFormUserGroups: UserAreaGroups?,
    val customizations: List<Feature>,
    val userAreaGroups: UserAreaGroups?,
    val whitelabelChildren: WhitelabelChildrenRef?,
    val whitelabelParent: WhitelabelParent?,
    val adminGroup: Id,
    val adminGroups: Id,
    val customerGroup: Id,
    val customerGroups: Id,
    val customerInfo: IdTuple,
    val orderProcessingAgreement: IdTuple?,
    val properties: Id?,
    val serverProperties: Id?,
    val teamGroups: Id,
    val userGroups: Id
)

@Serializable
data class AuthenticatedDevice(
    val _id: Id,
    val authType: Long,
    val deviceKey: ByteArray,
    val deviceToken: String
)

@Serializable
data class Login(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val time: Date
)

@Serializable
data class SecondFactorAuthentication(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val code: String,
    val finished: Boolean,
    val service: String,
    val verifyCount: Long
)

@Serializable
data class PhoneNumber(
    val _id: Id,
    val number: String
)

@Serializable
data class VariableExternalAuthInfo(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val authUpdateCounter: Long,
    val lastSentTimestamp: Date,
    val loggedInIpAddressHash: ByteArray?,
    val loggedInTimestamp: Date?,
    val loggedInVerifier: ByteArray?,
    val sentCount: Long
)

@Serializable
data class UserExternalAuthInfo(
    val _id: Id,
    val authUpdateCounter: Long,
    val autoAuthenticationId: Id,
    val autoTransmitPassword: String?,
    val latestSaltHash: ByteArray?,

    val variableAuthInfo: Id
)

@Serializable
data class User(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val accountType: Long,
    val enabled: Boolean,
    val requirePasswordUpdate: Boolean,
    val salt: ByteArray?,
    val userEncClientKey: ByteArray,
    val verifier: ByteArray,

    val alarmInfoList: UserAlarmInfoListType?,
    val auth: UserAuthentication?,
    val authenticatedDevices: List<AuthenticatedDevice>,
    val externalAuthInfo: UserExternalAuthInfo?,
    val memberships: List<GroupMembership>,
    val phoneNumbers: List<PhoneNumber>,
    val pushIdentifierList: PushIdentifierList?,
    val userGroup: GroupMembership,
    val customer: Id?,
    val failedLogins: Id,
    val secondFactorAuthentications: Id,
    val successfulLogins: Id
) : Entity

@Serializable
data class ExternalUserReference(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val user: Id,
    val userGroup: Id
)

@Serializable
data class GroupRoot(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val externalUserAreaGroupInfos: UserAreaGroups?,
    val externalGroupInfos: Id,
    val externalUserReferences: Id
)

@Serializable
data class BucketPermission(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val ownerEncBucketKey: ByteArray?,
    val pubEncBucketKey: ByteArray?,
    val pubKeyVersion: Long?,
    val symEncBucketKey: ByteArray?,
    val type: Long,

    val group: Id
) : Entity

@Serializable
data class Bucket(
    val _id: Id,

    val bucketPermissions: Id
)

@Serializable
data class Permission(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val bucketEncSessionKey: ByteArray?,
    val listElementApplication: String?,
    val listElementTypeId: Long?,
    val ops: String?,
    val symEncSessionKey: ByteArray?,
    val type: Long,

    val bucket: Bucket?,
    val group: Id?
) : Entity

@Serializable
data class AccountingInfo(
    val _id: Id,
    val _modified: Date,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val business: Boolean,
    val invoiceAddress: String,
    val invoiceCountry: String?,
    val invoiceName: String,
    val invoiceVatIdNo: String,
    val lastInvoiceNbrOfSentSms: Long,
    val lastInvoiceTimestamp: Date?,
    val paymentAccountIdentifier: String?,
    val paymentInterval: Long,
    val paymentMethod: Long?,
    val paymentMethodInfo: String?,
    val paymentProviderCustomerId: String?,
    val paypalBillingAgreement: String?,
    val secondCountryInfo: Long,

    val invoiceInfo: Id?
)

@Serializable
data class CustomerInfo(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val activationTime: Date?,
    val company: String?,
    val creationTime: Date,
    val deletionReason: String?,
    val deletionTime: Date?,
    val domain: String,
    val erased: Boolean,
    val includedEmailAliases: Long,
    val includedStorageCapacity: Long,
    val promotionEmailAliases: Long,
    val promotionStorageCapacity: Long,
    val registrationMailAddress: String,
    val source: Long,
    val testEndTime: Date?,
    val usedSharedEmailAliases: Long,

    val bookings: BookingsRef?,
    val domainInfos: List<DomainInfo>,
    val accountingInfo: Id,
    val customer: Id,
    val takeoverCustomer: Id?
)

@Serializable
data class MailAddressToGroup(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val internalGroup: Id?
)

@Serializable
data class GroupMember(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val group: Id,
    val user: Id,
    val userGroupInfo: IdTuple
)

@Serializable
data class RootInstance(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val reference: Id
)

@Serializable
data class VersionInfo(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val app: String,
    val operation: String,
    val referenceList: Id?,
    val timestamp: Date,
    val type: Long,
    val versionData: ByteArray?,

    val author: Id,
    val authorGroupInfo: IdTuple
)

@Serializable
data class SystemKeysReturn(
    val freeGroupKey: ByteArray,
    val premiumGroupKey: ByteArray,
    val starterGroupKey: ByteArray,
    val systemAdminPubKey: ByteArray,
    val systemAdminPubKeyVersion: Long,

    val freeGroup: Id?,
    val premiumGroup: Id?
)

@Serializable
data class MailAddressAvailabilityData(
    val mailAddress: String
)

@Serializable
data class MailAddressAvailabilityReturn(
    val available: Boolean
)

@Serializable
data class RegistrationServiceData(
    val starterDomain: String,
    val source: Long?,
    val state: Long
)

@Serializable
data class RegistrationReturn(
    val authToken: String
)

@Serializable
data class SendRegistrationCodeData(
    val accountType: Long,
    val authToken: String,
    val language: String,
    val mobilePhoneNumber: String
)

@Serializable
data class SendRegistrationCodeReturn(
    val authToken: String
)

@Serializable
data class VerifyRegistrationCodeData(
    val authToken: String,
    val code: String
)

@Serializable
data class CreateGroupData(
    val _id: Id,
    val adminEncGKey: ByteArray,
    val customerEncUserGroupInfoSessionKey: ByteArray?,
    val encryptedName: ByteArray,
    val listEncSessionKey: ByteArray,
    val mailAddress: String?,
    val pubKey: ByteArray,
    val symEncGKey: ByteArray,
    val symEncPrivKey: ByteArray
)

@Serializable
data class CreateGroupListData(
    val _id: Id,
    val adminEncGroupInfoListKey: ByteArray,
    val customerEncGroupInfoListKey: ByteArray,

    val createGroupData: CreateGroupData?
)

@Serializable
data class CustomerReturn(

    val adminUser: Id,
    val adminUserGroup: Id
)

@Serializable
data class CustomerData(
    val accountingInfoBucketEncAccountingInfoSessionKey: ByteArray,
    val adminEncAccountingInfoSessionKey: ByteArray,
    val authToken: String,
    val company: String,
    val date: Date?,
    val domain: String,
    val salt: ByteArray,
    val symEncAccountGroupKey: ByteArray,
    val systemCustomerPubEncAccountingInfoBucketKey: ByteArray,
    val systemCustomerPubKeyVersion: Long,
    val userEncClientKey: ByteArray,
    val verifier: ByteArray,

    val adminGroupList: CreateGroupListData,
    val customerGroupList: CreateGroupListData,
    val teamGroupList: CreateGroupListData,
    val userGroupList: CreateGroupListData
)

@Serializable
data class UserReturn(

    val user: Id,
    val userGroup: Id
)

@Serializable
data class UserData(
    val date: Date?,
    val mobilePhoneNumber: String,
    val salt: ByteArray,
    val userEncClientKey: ByteArray,
    val userEncCustomerGroupKey: ByteArray,
    val verifier: ByteArray,

    val userGroupData: CreateGroupData?
)

@Serializable
data class UserDataDelete(
    val date: Date?,
    val restore: Boolean,

    val user: Id
)

@Serializable
data class PublicKeyData(
    val mailAddress: String
)

@Serializable
data class PublicKeyReturn(
    val pubKey: ByteArray,
    val pubKeyVersion: Long
)

@Serializable
data class SaltData(
    val mailAddress: String
) : Entity

@Serializable
data class SaltReturn(
    @Serializable(with = ByteArraySerializer::class)
    val salt: ByteArray
) : Entity

@Serializable
data class UserIdData(
    val mailAddress: String
)

@Serializable
data class UserIdReturn(

    val userId: Id
)

@Serializable
data class AutoLoginDataGet(
    val deviceToken: String,

    val userId: Id
)

@Serializable
data class AutoLoginDataDelete(
    val deviceToken: String
)

@Serializable
data class AutoLoginDataReturn(
    val deviceKey: ByteArray
)

@Serializable
data class AutoLoginPostReturn(
    val deviceToken: String
)

@Serializable
data class UpdatePermissionKeyData(
    val ownerEncSessionKey: ByteArray?,
    val symEncSessionKey: ByteArray?,

    val bucketPermission: IdTuple,
    val permission: IdTuple
)

@Serializable
data class Authentication(
    val _id: Id,
    val accessToken: String?,
    val authVerifier: String?,
    val externalAuthToken: String?,

    val userId: Id
)

@Serializable
data class Chat(
    val _id: Id,
    val recipient: Id,
    val sender: Id,
    val text: String
)

@Serializable
data class EntityUpdate(
    val _id: Id,
    val application: String,
    val instanceId: String,
    val instanceListId: String,
    val operation: Long,
    val type: String
)

@Serializable
data class Exception(
    val _id: Id,
    val msg: String,
    val type: String
)

@Serializable
data class Version(
    val _id: Id,
    val operation: String,
    val timestamp: Date,
    val version: Id,

    val author: Id,
    val authorGroupInfo: IdTuple
)

@Serializable
data class VersionData(
    val application: String,
    val id: Id,
    val listId: Id?,
    val typeId: Long
)

@Serializable
data class VersionReturn(

    val versions: List<Version>
)

@Serializable
data class MembershipAddData(
    val symEncGKey: ByteArray,

    val group: Id,
    val user: Id
)

@Serializable
data class StringConfigValue(
    val _id: Id,
    val name: String,
    val value: String
)

@Serializable
data class ChangePasswordData(
    val code: String?,
    val oldVerifier: ByteArray?,
    val pwEncUserGroupKey: ByteArray,
    val recoverCodeVerifier: ByteArray?,
    val salt: ByteArray,
    val verifier: ByteArray
)

@Serializable
data class SecondFactorAuthData(
    val otpCode: Long?,
    val type: Long?,

    val u2f: U2fResponseData?,
    val session: IdTuple?
)

@Serializable
data class SecondFactorAuthAllowedReturn(
    val allowed: Boolean
)

@Serializable
data class CustomerInfoReturn(
    val sendMailDisabled: Boolean
)

@Serializable
data class ResetPasswordData(
    val pwEncUserGroupKey: ByteArray,
    val salt: ByteArray,
    val verifier: ByteArray,

    val user: Id
)

@Serializable
data class DomainMailAddressAvailabilityData(
    val mailAddress: String
)

@Serializable
data class DomainMailAddressAvailabilityReturn(
    val available: Boolean
)

@Serializable
data class RegistrationConfigReturn(
    val freeEnabled: Boolean,
    val starterEnabled: Boolean
)

@Serializable
data class PushIdentifier(
    val _area: Long,
    val _id: IdTuple,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val disabled: Boolean,
    val displayName: String,
    val identifier: String,
    val language: String,
    val lastNotificationDate: Date?,
    val pushServiceType: Long
)

@Serializable
data class PushIdentifierList(
    val _id: Id,

    val list: Id
)

@Serializable
data class DeleteCustomerData(
    val authVerifier: ByteArray?,
    val reason: String,
    val takeoverMailAddress: String?,
    val undelete: Boolean,

    val customer: Id
)

@Serializable
data class PremiumFeatureData(
    val activationCode: String,
    val featureName: String
)

@Serializable
data class CustomerProperties(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val externalUserWelcomeMessage: String,
    val lastUpgradeReminder: Date?,

    val bigLogo: File?,
    val notificationMailTemplates: List<NotificationMailTemplate>,
    val smallLogo: File?
)

@Serializable
data class ExternalPropertiesReturn(
    val accountType: Long,
    val message: String,

    val bigLogo: File?,
    val smallLogo: File?
)

@Serializable
data class RegistrationCaptchaServiceData(
    val response: String,
    val token: String
)

@Serializable
data class RegistrationCaptchaServiceReturn(
    val challenge: ByteArray?,
    val token: String
)

@Serializable
data class MailAddressAlias(
    val _id: Id,
    val enabled: Boolean,
    val mailAddress: String
)

@Serializable
data class MailAddressAliasServiceData(
    val mailAddress: String,

    val group: Id
)

@Serializable
data class MailAddressAliasServiceReturn(
    val enabledAliases: Long,
    val nbrOfFreeAliases: Long,
    val totalAliases: Long,
    val usedAliases: Long
)

@Serializable
data class DomainInfo(
    val _id: Id,
    val certificateExpiryDate: Date?,
    val domain: String,
    val validatedMxRecord: Boolean,

    val catchAllMailGroup: Id?,
    val certificate: Id?,
    val whitelabelConfig: Id?
)

@Serializable
data class BookingItem(
    val _id: Id,
    val currentCount: Long,
    val currentInvoicedCount: Long,
    val featureType: Long,
    val maxCount: Long,
    val price: Long,
    val priceType: Long,
    val totalInvoicedCount: Long
)

@Serializable
data class Booking(
    val _area: Long,
    val _id: IdTuple,
    val _owner: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val business: Boolean,
    val createDate: Date,
    val endDate: Date?,
    val paymentInterval: Long,
    val paymentMonths: Long,

    val items: List<BookingItem>
)

@Serializable
data class BookingsRef(
    val _id: Id,

    val items: Id
)

@Serializable
data class StringWrapper(
    val _id: Id,
    val value: String
)

@Serializable
data class CustomDomainReturn(
    val validationResult: Long,

    val invalidDnsRecords: List<StringWrapper>
)

@Serializable
data class CustomDomainData(
    val domain: String,

    val catchAllMailGroup: Id?
)

@Serializable
data class Invoice(
    val _id: IdTuple,
    val _listEncSessionKey: ByteArray?,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val country: String,
    val date: Date,
    val grandTotal: Long,
    val number: Long,
    val paymentMethod: Long,
    val source: String,
    val status: Long,
    val vat: Long,
    val vatRate: Long,

    val bookings: List<IdTuple>,
    val changes: List<IdTuple>
)

@Serializable
data class InvoiceInfo(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val publishInvoices: Boolean,
    val specialPriceBrandingPerUser: Long?,
    val specialPriceContactFormSingle: Long?,
    val specialPriceSharedGroupSingle: Long?,
    val specialPriceUserSingle: Long?,
    val specialPriceUserTotal: Long?,

    val invoices: Id
)

@Serializable
data class SwitchAccountTypeData(
    val accountType: Long,
    val campaign: String?,
    val date: Date?,
    val proUpgrade: Boolean
)

@Serializable
data class PdfInvoiceServiceData(

    val invoice: IdTuple
)

@Serializable
data class PdfInvoiceServiceReturn(
    val data: ByteArray
)

@Serializable
data class MailAddressAliasServiceDataDelete(
    val mailAddress: String,
    val restore: Boolean,

    val group: Id
)

@Serializable
data class PaymentDataServiceGetReturn(
    val loginUrl: String
)

@Serializable
data class PaymentDataServicePutData(
    val business: Boolean,
    val confirmedCountry: String?,
    val invoiceAddress: String,
    val invoiceCountry: String,
    val invoiceName: String,
    val invoiceVatIdNo: String,
    val paymentInterval: Long,
    val paymentMethod: Long,
    val paymentMethodInfo: String?,
    val paymentToken: String?,

    val creditCard: CreditCard?
)

@Serializable
data class PaymentDataServicePutReturn(
    val result: Long
)

@Serializable
data class PriceRequestData(
    val _id: Id,
    val accountType: Long?,
    val business: Boolean?,
    val count: Long,
    val featureType: Long,
    val paymentInterval: Long?,
    val reactivate: Boolean
)

@Serializable
data class PriceServiceData(
    val campaign: String?,
    val date: Date?,

    val priceRequest: PriceRequestData?
)

@Serializable
data class PriceItemData(
    val _id: Id,
    val count: Long,
    val featureType: Long,
    val price: Long,
    val singleType: Boolean
)

@Serializable
data class PriceData(
    val _id: Id,
    val paymentInterval: Long,
    val price: Long,
    val taxIncluded: Boolean,

    val items: List<PriceItemData>
)

@Serializable
data class PriceServiceReturn(
    val currentPeriodAddedPrice: Long?,
    val periodEndDate: Date,

    val currentPriceNextPeriod: PriceData?,
    val currentPriceThisPeriod: PriceData?,
    val futurePriceNextPeriod: PriceData?
)

@Serializable
data class MembershipRemoveData(

    val group: Id,
    val user: Id
)

@Serializable
data class File(
    val _id: Id,
    val data: ByteArray,
    val mimeType: String,
    val name: String
)

@Serializable
data class EmailSenderListElement(
    val _id: Id,
    val hashedValue: String,
    val type: Long,
    val value: String
)

@Serializable
data class CustomerServerProperties(
    val _id: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val requirePasswordUpdateAfterReset: Boolean,
    val saveEncryptedIpAddressInSession: Boolean,
    val whitelabelCode: String,

    val emailSenderList: List<EmailSenderListElement>,
    val whitelabelRegistrationDomains: List<StringWrapper>,
    val whitelistedDomains: DomainsRef?
)

@Serializable
data class CreateCustomerServerPropertiesData(
    val adminGroupEncSessionKey: ByteArray
)

@Serializable
data class CreateCustomerServerPropertiesReturn(

    val id: Id
)

@Serializable
data class PremiumFeatureReturn(
    val activatedFeature: Long
)

@Serializable
data class UserAreaGroups(
    val _id: Id,

    val list: Id
)

@Serializable
data class DebitServicePutData(

    val invoice: IdTuple
)

@Serializable
data class BookingServiceData(
    val amount: Long,
    val date: Date?,
    val featureType: Long
)

@Serializable
data class EntityEventBatch(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val events: List<EntityUpdate>
)

@Serializable
data class DomainsRef(
    val _id: Id,

    val items: Id
)

@Serializable
data class AuditLogEntry(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val action: String,
    val actorIpAddress: String?,
    val actorMailAddress: String,
    val date: Date,
    val modifiedEntity: String,

    val groupInfo: IdTuple?,
    val modifiedGroupInfo: IdTuple?
)

@Serializable
data class AuditLogRef(
    val _id: Id,

    val items: Id
)

@Serializable
data class WhitelabelConfig(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val germanLanguageCode: String?,
    val imprintUrl: String?,
    val jsonTheme: String,
    val metaTags: String,
    val privacyStatementUrl: String?,

    val bootstrapCustomizations: List<BootstrapFeature>,
    val certificateInfo: CertificateInfo
)

@Serializable
data class BrandingDomainData(
    val domain: String,
    val sessionEncPemCertificateChain: ByteArray?,
    val sessionEncPemPrivateKey: ByteArray?,
    val systemAdminPubEncSessionKey: ByteArray
)

@Serializable
data class BrandingDomainDeleteData(
    val domain: String
)

@Serializable
data class U2fRegisteredDevice(
    val _id: Id,
    val appId: String,
    val compromised: Boolean,
    val counter: Long,
    val keyHandle: ByteArray,
    val publicKey: ByteArray
)

@Serializable
data class SecondFactor(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val name: String,
    val otpSecret: ByteArray?,
    val type: Long,

    val u2f: U2fRegisteredDevice?
)

@Serializable
data class U2fKey(
    val _id: Id,
    val appId: String,
    val keyHandle: ByteArray,

    val secondFactor: IdTuple
)

@Serializable
data class U2fChallenge(
    val _id: Id,
    val challenge: ByteArray,

    val keys: List<U2fKey>
)

@Serializable
data class Challenge(
    val _id: Id,
    val type: Long,

    val otp: OtpChallenge?,
    val u2f: U2fChallenge?
)

@Serializable
data class Session(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val accessKey: ByteArray?,
    val clientIdentifier: String,
    val lastAccessTime: Date,
    val loginIpAddress: String?,
    val loginTime: Date,
    val state: Long,

    val challenges: List<Challenge>,
    val user: Id
)

@Serializable
data class UserAuthentication(
    val _id: Id,

    val recoverCode: Id?,
    val secondFactors: Id,
    val sessions: Id
)

@Serializable
data class CreateSessionData(
    val accessKey: ByteArray? = null,
    val authToken: String? = null,
    val authVerifier: String? = null,
    val clientIdentifier: String,
    val mailAddress: String? = null,
    val recoverCodeVerifier: String? = null,

    val user: Id? = null
) : Entity

@Serializable
data class CreateSessionReturn(
    val accessToken: String,

    val challenges: List<Challenge>,
    val user: Id
) : Entity

@Serializable
data class U2fResponseData(
    val _id: Id,
    val clientData: String,
    val keyHandle: String,
    val signatureData: String
)

@Serializable
data class SecondFactorAuthGetData(
    val accessToken: String
)

@Serializable
data class SecondFactorAuthGetReturn(
    val secondFactorPending: Boolean
)

@Serializable
data class OtpChallenge(
    val _id: Id,

    val secondFactors: List<IdTuple>
)

@Serializable
data class BootstrapFeature(
    val _id: Id,
    val feature: Long
)

@Serializable
data class Feature(
    val _id: Id,
    val feature: Long
)

@Serializable
data class WhitelabelChild(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val comment: String,
    val createdDate: Date,
    val deletedDate: Date?,
    val mailAddress: String,

    val customer: Id
)

@Serializable
data class WhitelabelChildrenRef(
    val _id: Id,

    val items: Id
)

@Serializable
data class WhitelabelParent(
    val _id: Id,

    val customer: Id,
    val whitelabelChildInParent: IdTuple
)

@Serializable
data class UpdateAdminshipData(
    val newAdminGroupEncGKey: ByteArray,

    val group: Id,
    val newAdminGroup: Id
)

@Serializable
data class AdministratedGroup(
    val _id: IdTuple,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val groupType: Long,

    val groupInfo: IdTuple,
    val localAdminGroup: Id
)

@Serializable
data class AdministratedGroupsRef(
    val _id: Id,


    val items: Id
)

@Serializable
data class CreditCard(
    val _id: Id,
    val cardHolderName: String,
    val cvv: String,
    val expirationMonth: String,
    val expirationYear: String,
    val number: String
)

@Serializable
data class LocationServiceGetReturn(
    val country: String
)

@Serializable
data class OrderProcessingAgreement(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val customerAddress: String,
    val signatureDate: Date,
    val version: String,

    val customer: Id,
    val signerUserGroupInfo: IdTuple
)

@Serializable
data class SignOrderProcessingAgreementData(
    val customerAddress: String,
    val version: String
)

@Serializable
data class GeneratedIdWrapper(
    val _id: Id,
    val value: Id
)

@Serializable
data class SseConnectData(
    val identifier: String,

    val userIds: List<GeneratedIdWrapper>
)

@Serializable
data class RecoverCode(
    val _id: Id,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val recoverCodeEncUserGroupKey: ByteArray,
    val userEncRecoverCode: ByteArray,
    val verifier: ByteArray
)

@Serializable
data class ResetFactorsDeleteData(
    val authVerifier: String,
    val mailAddress: String,
    val recoverCodeVerifier: String
)

@Serializable
data class UpgradePriceServiceData(
    val campaign: String?,
    val date: Date?
)

@Serializable
data class PlanPrices(
    val _id: Id,
    val additionalUserPriceMonthly: Long,
    val contactFormPriceMonthly: Long,
    val firstYearDiscount: Long,
    val includedAliases: Long,
    val includedStorage: Long,
    val monthlyPrice: Long,
    val monthlyReferencePrice: Long
)

@Serializable
data class UpgradePriceServiceReturn(
    val business: Boolean,
    val messageTextId: String?,

    val premiumPrices: PlanPrices,
    val proPrices: PlanPrices
)

@Serializable
data class RegistrationCaptchaServiceGetData(
    val mailAddress: String,
    val token: String?
)

@Serializable
data class WebsocketEntityData(
    val eventBatchId: Id,
    val eventBatchOwner: Id,

    val eventBatch: List<EntityUpdate>
)

@Serializable
data class WebsocketCounterValue(
    val _id: Id,
    val count: Long,
    val mailListId: Id
)

@Serializable
data class WebsocketCounterData(
    val mailGroup: Id,

    val counterValues: List<WebsocketCounterValue>
)

@Serializable
data class CertificateInfo(
    val _id: Id,
    val expiryDate: Date?,
    val state: Long,
    val type: Long,

    val certificate: Id?
)

@Serializable
data class NotificationMailTemplate(
    val _id: Id,
    val body: String,
    val language: String,
    val subject: String
)

@Serializable
data class CalendarEventRef(
    val _id: Id,
    val elementId: Id,
    val listId: Id
)

@Serializable
data class AlarmInfo(
    val _id: Id,
    val alarmIdentifier: String,
    val trigger: String,

    val calendarRef: CalendarEventRef
)

@Serializable
data class UserAlarmInfo(
    val _id: IdTuple,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,

    val alarmInfo: AlarmInfo
)

@Serializable
data class UserAlarmInfoListType(
    val _id: Id,

    val alarms: Id
)

@Serializable
data class NotificationSessionKey(
    val _id: Id,
    val pushIdentifierSessionEncSessionKey: ByteArray,

    val pushIdentifier: IdTuple
)

@Serializable
data class RepeatRule(
    val _id: Id,
    val endType: Long,
    val endValue: Long?,
    val frequency: Long,
    val interval: Long,
    val timeZone: String
)

@Serializable
data class AlarmNotification(
    val _id: Id,
    val eventEnd: Date,
    val eventStart: Date,
    val operation: Long,
    val summary: String,

    val alarmInfo: AlarmInfo,
    val notificationSessionKeys: List<NotificationSessionKey>,
    val repeatRule: RepeatRule?,
    val user: Id
)

@Serializable
data class AlarmServicePost(

    val alarmNotifications: List<AlarmNotification>
)

@Serializable
data class DnsRecord(
    val _id: Id,
    val subdomain: String?,
    val type: Long,
    val value: String
)

@Serializable
data class CustomDomainCheckData(
    val domain: String
)

@Serializable
data class CustomDomainCheckReturn(
    val checkResult: Long,

    val invalidRecords: List<DnsRecord>,
    val missingRecords: List<DnsRecord>
)
