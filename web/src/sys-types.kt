data class KeyPair(
	_id: Id,
	pubKey: Uint8Array,
	symEncPrivKey: Uint8Array,
	version: NumberString
)

data class Group(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	adminGroupEncGKey: ?Uint8Array,
	enabled: boolean,
	external: boolean,
	type: NumberString,

	administratedGroups: ?AdministratedGroupsRef,
	keys: KeyPair[],
	admin: ?Id,
	customer: ?Id,
	groupInfo: IdTuple,
	invitations: Id,
	members: Id,
	user: ?Id
)

data class GroupInfo(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_listEncSessionKey: ?Uint8Array,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	created: Date,
	deleted: ?Date,
	groupType: ?NumberString,
	mailAddress: ?string,
	name: string,

	mailAddressAliases: MailAddressAlias[],
	group: Id,
	localAdmin: ?Id
)

data class GroupMembership(
	_id: Id,
	admin: boolean,
	groupType: ?NumberString,
	symEncGKey: Uint8Array,

	group: Id,
	groupInfo: IdTuple,
	groupMember: IdTuple
)

data class Customer(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	approvalStatus: NumberString,
	canceledPremiumAccount: boolean,
	orderProcessingAgreementNeeded: boolean,
	type: NumberString,

	auditLog: ?AuditLogRef,
	contactFormUserAreaGroups: ?UserAreaGroups,
	contactFormUserGroups: ?UserAreaGroups,
	customizations: Feature[],
	userAreaGroups: ?UserAreaGroups,
	whitelabelChildren: ?WhitelabelChildrenRef,
	whitelabelParent: ?WhitelabelParent,
	adminGroup: Id,
	adminGroups: Id,
	customerGroup: Id,
	customerGroups: Id,
	customerInfo: IdTuple,
	orderProcessingAgreement: ?IdTuple,
	properties: ?Id,
	serverProperties: ?Id,
	teamGroups: Id,
	userGroups: Id
)

data class AuthenticatedDevice(
	_id: Id,
	authType: NumberString,
	deviceKey: Uint8Array,
	deviceToken: string
)

data class Login(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	time: Date
)

data class SecondFactorAuthentication(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	code: string,
	finished: boolean,
	service: string,
	verifyCount: NumberString
)

data class PhoneNumber(
	_id: Id,
	number: string
)

data class VariableExternalAuthInfo(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	authUpdateCounter: NumberString,
	lastSentTimestamp: Date,
	loggedInIpAddressHash: ?Uint8Array,
	loggedInTimestamp: ?Date,
	loggedInVerifier: ?Uint8Array,
	sentCount: NumberString
)

data class UserExternalAuthInfo(
	_id: Id,
	authUpdateCounter: NumberString,
	autoAuthenticationId: Id,
	autoTransmitPassword: ?string,
	latestSaltHash: ?Uint8Array,

	variableAuthInfo: Id
)

data class User(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	accountType: NumberString,
	enabled: boolean,
	requirePasswordUpdate: boolean,
	salt: ?Uint8Array,
	userEncClientKey: Uint8Array,
	verifier: Uint8Array,

	alarmInfoList: ?UserAlarmInfoListType,
	auth: ?UserAuthentication,
	authenticatedDevices: AuthenticatedDevice[],
	externalAuthInfo: ?UserExternalAuthInfo,
	memberships: GroupMembership[],
	phoneNumbers: PhoneNumber[],
	pushIdentifierList: ?PushIdentifierList,
	userGroup: GroupMembership,
	customer: ?Id,
	failedLogins: Id,
	secondFactorAuthentications: Id,
	successfulLogins: Id
)

data class ExternalUserReference(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,

	user: Id,
	userGroup: Id
)

data class GroupRoot(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,

	externalUserAreaGroupInfos: ?UserAreaGroups,
	externalGroupInfos: Id,
	externalUserReferences: Id
)

data class BucketPermission(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	ownerEncBucketKey: ?Uint8Array,
	pubEncBucketKey: ?Uint8Array,
	pubKeyVersion: ?NumberString,
	symEncBucketKey: ?Uint8Array,
	type: NumberString,

	group: Id
)

data class Bucket(
	_id: Id,

	bucketPermissions: Id
)

data class Permission(
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	bucketEncSessionKey: ?Uint8Array,
	listElementApplication: ?string,
	listElementTypeId: ?NumberString,
	ops: ?string,
	symEncSessionKey: ?Uint8Array,
	type: NumberString,

	bucket: ?Bucket,
	group: ?Id
)

data class AccountingInfo(
	_errors: Object,
	_format: NumberString,
	_id: Id,
	_modified: Date,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	business: boolean,
	invoiceAddress: string,
	invoiceCountry: ?string,
	invoiceName: string,
	invoiceVatIdNo: string,
	lastInvoiceNbrOfSentSms: NumberString,
	lastInvoiceTimestamp: ?Date,
	paymentAccountIdentifier: ?string,
	paymentInterval: NumberString,
	paymentMethod: ?NumberString,
	paymentMethodInfo: ?string,
	paymentProviderCustomerId: ?string,
	paypalBillingAgreement: ?string,
	secondCountryInfo: NumberString,

	invoiceInfo: ?Id
)

data class CustomerInfo(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	activationTime: ?Date,
	company: ?string,
	creationTime: Date,
	deletionReason: ?string,
	deletionTime: ?Date,
	domain: string,
	erased: boolean,
	includedEmailAliases: NumberString,
	includedStorageCapacity: NumberString,
	promotionEmailAliases: NumberString,
	promotionStorageCapacity: NumberString,
	registrationMailAddress: string,
	source: NumberString,
	testEndTime: ?Date,
	usedSharedEmailAliases: NumberString,

	bookings: ?BookingsRef,
	domainInfos: DomainInfo[],
	accountingInfo: Id,
	customer: Id,
	takeoverCustomer: ?Id
)

data class MailAddressToGroup(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,

	internalGroup: ?Id
)

data class GroupMember(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,

	group: Id,
	user: Id,
	userGroupInfo: IdTuple
)

data class RootInstance(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	reference: Id
)

data class VersionInfo(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	app: string,
	operation: string,
	referenceList: ?Id,
	timestamp: Date,
	type: NumberString,
	versionData: ?Uint8Array,

	author: Id,
	authorGroupInfo: IdTuple
)

data class SystemKeysReturn(
	_format: NumberString,
	freeGroupKey: Uint8Array,
	premiumGroupKey: Uint8Array,
	starterGroupKey: Uint8Array,
	systemAdminPubKey: Uint8Array,
	systemAdminPubKeyVersion: NumberString,

	freeGroup: ?Id,
	premiumGroup: ?Id
)

data class MailAddressAvailabilityData(
	_format: NumberString,
	mailAddress: string
)

data class MailAddressAvailabilityReturn(
	_format: NumberString,
	available: boolean
)

data class RegistrationServiceData(
	_format: NumberString,
	starterDomain: string,
	source: ?NumberString,
	state: NumberString
)

data class RegistrationReturn(
	_format: NumberString,
	authToken: string
)

data class SendRegistrationCodeData(
	_format: NumberString,
	accountType: NumberString,
	authToken: string,
	language: string,
	mobilePhoneNumber: string
)

data class SendRegistrationCodeReturn(
	_format: NumberString,
	authToken: string
)

data class VerifyRegistrationCodeData(
	_format: NumberString,
	authToken: string,
	code: string
)

data class CreateGroupData(
	_id: Id,
	adminEncGKey: Uint8Array,
	customerEncUserGroupInfoSessionKey: ?Uint8Array,
	encryptedName: Uint8Array,
	listEncSessionKey: Uint8Array,
	mailAddress: ?string,
	pubKey: Uint8Array,
	symEncGKey: Uint8Array,
	symEncPrivKey: Uint8Array
)

data class CreateGroupListData(
	_id: Id,
	adminEncGroupInfoListKey: Uint8Array,
	customerEncGroupInfoListKey: Uint8Array,

	createGroupData: ?CreateGroupData
)

data class CustomerReturn(
	_format: NumberString,

	adminUser: Id,
	adminUserGroup: Id
)

data class CustomerData(
	_format: NumberString,
	accountingInfoBucketEncAccountingInfoSessionKey: Uint8Array,
	adminEncAccountingInfoSessionKey: Uint8Array,
	authToken: string,
	company: string,
	date: ?Date,
	domain: string,
	salt: Uint8Array,
	symEncAccountGroupKey: Uint8Array,
	systemCustomerPubEncAccountingInfoBucketKey: Uint8Array,
	systemCustomerPubKeyVersion: NumberString,
	userEncClientKey: Uint8Array,
	verifier: Uint8Array,

	adminGroupList: CreateGroupListData,
	customerGroupList: CreateGroupListData,
	teamGroupList: CreateGroupListData,
	userGroupList: CreateGroupListData
)

data class UserReturn(
	_format: NumberString,

	user: Id,
	userGroup: Id
)

data class UserData(
	_format: NumberString,
	date: ?Date,
	mobilePhoneNumber: string,
	salt: Uint8Array,
	userEncClientKey: Uint8Array,
	userEncCustomerGroupKey: Uint8Array,
	verifier: Uint8Array,

	userGroupData: ?CreateGroupData
)

data class UserDataDelete(
	_format: NumberString,
	date: ?Date,
	restore: boolean,

	user: Id
)

data class PublicKeyData(
	_format: NumberString,
	mailAddress: string
)

data class PublicKeyReturn(
	_format: NumberString,
	pubKey: Uint8Array,
	pubKeyVersion: NumberString
)

data class SaltData(
	_format: NumberString,
	mailAddress: string
)

data class SaltReturn(
	_format: NumberString,
	salt: Uint8Array
)

data class UserIdData(
	_format: NumberString,
	mailAddress: string
)

data class UserIdReturn(
	_format: NumberString,

	userId: Id
)

data class AutoLoginDataGet(
	_format: NumberString,
	deviceToken: string,

	userId: Id
)

data class AutoLoginDataDelete(
	_format: NumberString,
	deviceToken: string
)

data class AutoLoginDataReturn(
	_format: NumberString,
	deviceKey: Uint8Array
)

data class AutoLoginPostReturn(
	_format: NumberString,
	deviceToken: string
)

data class UpdatePermissionKeyData(
	_format: NumberString,
	ownerEncSessionKey: ?Uint8Array,
	symEncSessionKey: ?Uint8Array,

	bucketPermission: IdTuple,
	permission: IdTuple
)

data class Authentication(
	_id: Id,
	accessToken: ?string,
	authVerifier: ?string,
	externalAuthToken: ?string,

	userId: Id
)

data class Chat(
	_id: Id,
	recipient: Id,
	sender: Id,
	text: string
)

data class EntityUpdate(
	_id: Id,
	application: string,
	instanceId: string,
	instanceListId: string,
	operation: NumberString,
	type: string
)

data class Exception(
	_id: Id,
	msg: string,
	type: string
)

data class Version(
	_id: Id,
	operation: string,
	timestamp: Date,
	version: Id,

	author: Id,
	authorGroupInfo: IdTuple
)

data class VersionData(
	_format: NumberString,
	application: string,
	id: Id,
	listId: ?Id,
	typeId: NumberString
)

data class VersionReturn(
	_format: NumberString,

	versions: Version[]
)

data class MembershipAddData(
	_format: NumberString,
	symEncGKey: Uint8Array,

	group: Id,
	user: Id
)

data class StringConfigValue(
	_id: Id,
	name: string,
	value: string
)

data class ChangePasswordData(
	_format: NumberString,
	code: ?string,
	oldVerifier: ?Uint8Array,
	pwEncUserGroupKey: Uint8Array,
	recoverCodeVerifier: ?Uint8Array,
	salt: Uint8Array,
	verifier: Uint8Array
)

data class SecondFactorAuthData(
	_format: NumberString,
	otpCode: ?NumberString,
	type: ?NumberString,

	u2f: ?U2fResponseData,
	session: ?IdTuple
)

data class SecondFactorAuthAllowedReturn(
	_format: NumberString,
	allowed: boolean
)

data class CustomerInfoReturn(
	_format: NumberString,
	sendMailDisabled: boolean
)

data class ResetPasswordData(
	_format: NumberString,
	pwEncUserGroupKey: Uint8Array,
	salt: Uint8Array,
	verifier: Uint8Array,

	user: Id
)

data class DomainMailAddressAvailabilityData(
	_format: NumberString,
	mailAddress: string
)

data class DomainMailAddressAvailabilityReturn(
	_format: NumberString,
	available: boolean
)

data class RegistrationConfigReturn(
	_format: NumberString,
	freeEnabled: boolean,
	starterEnabled: boolean
)

data class PushIdentifier(
	_errors: Object,
	_area: NumberString,
	_format: NumberString,
	_id: IdTuple,
	_owner: Id,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	disabled: boolean,
	displayName: string,
	identifier: string,
	language: string,
	lastNotificationDate: ?Date,
	pushServiceType: NumberString
)

data class PushIdentifierList(
	_id: Id,

	list: Id
)

data class DeleteCustomerData(
	_format: NumberString,
	authVerifier: ?Uint8Array,
	reason: string,
	takeoverMailAddress: ?string,
	undelete: boolean,

	customer: Id
)

data class PremiumFeatureData(
	_format: NumberString,
	activationCode: string,
	featureName: string
)

data class CustomerProperties(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	externalUserWelcomeMessage: string,
	lastUpgradeReminder: ?Date,

	bigLogo: ?SysFile,
	notificationMailTemplates: NotificationMailTemplate[],
	smallLogo: ?SysFile
)

data class ExternalPropertiesReturn(
	_format: NumberString,
	accountType: NumberString,
	message: string,

	bigLogo: ?SysFile,
	smallLogo: ?SysFile
)

data class RegistrationCaptchaServiceData(
	_format: NumberString,
	response: string,
	token: string
)

data class RegistrationCaptchaServiceReturn(
	_format: NumberString,
	challenge: ?Uint8Array,
	token: string
)

data class MailAddressAlias(
	_id: Id,
	enabled: boolean,
	mailAddress: string
)

data class MailAddressAliasServiceData(
	_format: NumberString,
	mailAddress: string,

	group: Id
)

data class MailAddressAliasServiceReturn(
	_format: NumberString,
	enabledAliases: NumberString,
	nbrOfFreeAliases: NumberString,
	totalAliases: NumberString,
	usedAliases: NumberString
)

data class DomainInfo(
	_id: Id,
	certificateExpiryDate: ?Date,
	domain: string,
	validatedMxRecord: boolean,

	catchAllMailGroup: ?Id,
	certificate: ?Id,
	whitelabelConfig: ?Id
)

data class BookingItem(
	_id: Id,
	currentCount: NumberString,
	currentInvoicedCount: NumberString,
	featureType: NumberString,
	maxCount: NumberString,
	price: NumberString,
	priceType: NumberString,
	totalInvoicedCount: NumberString
)

data class Booking(
	_area: NumberString,
	_format: NumberString,
	_id: IdTuple,
	_owner: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	business: boolean,
	createDate: Date,
	endDate: ?Date,
	paymentInterval: NumberString,
	paymentMonths: NumberString,

	items: BookingItem[]
)

data class BookingsRef(
	_id: Id,

	items: Id
)

data class StringWrapper(
	_id: Id,
	value: string
)

data class CustomDomainReturn(
	_format: NumberString,
	validationResult: NumberString,

	invalidDnsRecords: StringWrapper[]
)

data class CustomDomainData(
	_format: NumberString,
	domain: string,

	catchAllMailGroup: ?Id
)

data class Invoice(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_listEncSessionKey: ?Uint8Array,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	country: string,
	date: Date,
	grandTotal: NumberString,
	number: NumberString,
	paymentMethod: NumberString,
	source: string,
	status: NumberString,
	vat: NumberString,
	vatRate: NumberString,

	bookings: IdTuple[],
	changes: IdTuple[]
)

data class InvoiceInfo(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	publishInvoices: boolean,
	specialPriceBrandingPerUser: ?NumberString,
	specialPriceContactFormSingle: ?NumberString,
	specialPriceSharedGroupSingle: ?NumberString,
	specialPriceUserSingle: ?NumberString,
	specialPriceUserTotal: ?NumberString,

	invoices: Id
)

data class SwitchAccountTypeData(
	_format: NumberString,
	accountType: NumberString,
	campaign: ?string,
	date: ?Date,
	proUpgrade: boolean
)

data class PdfInvoiceServiceData(
	_format: NumberString,

	invoice: IdTuple
)

data class PdfInvoiceServiceReturn(
	_errors: Object,
	_format: NumberString,
	data: Uint8Array
)

data class MailAddressAliasServiceDataDelete(
	_format: NumberString,
	mailAddress: string,
	restore: boolean,

	group: Id
)

data class PaymentDataServiceGetReturn(
	_format: NumberString,
	loginUrl: string
)

data class PaymentDataServicePutData(
	_errors: Object,
	_format: NumberString,
	business: boolean,
	confirmedCountry: ?string,
	invoiceAddress: string,
	invoiceCountry: string,
	invoiceName: string,
	invoiceVatIdNo: string,
	paymentInterval: NumberString,
	paymentMethod: NumberString,
	paymentMethodInfo: ?string,
	paymentToken: ?string,

	creditCard: ?CreditCard
)

data class PaymentDataServicePutReturn(
	_format: NumberString,
	result: NumberString
)

data class PriceRequestData(
	_id: Id,
	accountType: ?NumberString,
	business: ?boolean,
	count: NumberString,
	featureType: NumberString,
	paymentInterval: ?NumberString,
	reactivate: boolean
)

data class PriceServiceData(
	_format: NumberString,
	campaign: ?string,
	date: ?Date,

	priceRequest: ?PriceRequestData
)

data class PriceItemData(
	_id: Id,
	count: NumberString,
	featureType: NumberString,
	price: NumberString,
	singleType: boolean
)

data class PriceData(
	_id: Id,
	paymentInterval: NumberString,
	price: NumberString,
	taxIncluded: boolean,

	items: PriceItemData[]
)

data class PriceServiceReturn(
	_format: NumberString,
	currentPeriodAddedPrice: ?NumberString,
	periodEndDate: Date,

	currentPriceNextPeriod: ?PriceData,
	currentPriceThisPeriod: ?PriceData,
	futurePriceNextPeriod: ?PriceData
)

data class MembershipRemoveData(
	_format: NumberString,

	group: Id,
	user: Id
)

data class SysFile(
	_id: Id,
	data: Uint8Array,
	mimeType: string,
	name: string
)

data class EmailSenderListElement(
	_id: Id,
	hashedValue: string,
	type: NumberString,
	value: string
)

data class CustomerServerProperties(
	_errors: Object,
	_format: NumberString,
	_id: Id,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	requirePasswordUpdateAfterReset: boolean,
	saveEncryptedIpAddressInSession: boolean,
	whitelabelCode: string,

	emailSenderList: EmailSenderListElement[],
	whitelabelRegistrationDomains: StringWrapper[],
	whitelistedDomains: ?DomainsRef
)

data class CreateCustomerServerPropertiesData(
	_format: NumberString,
	adminGroupEncSessionKey: Uint8Array
)

data class CreateCustomerServerPropertiesReturn(
	_format: NumberString,

	id: Id
)

data class PremiumFeatureReturn(
	_format: NumberString,
	activatedFeature: NumberString
)

data class UserAreaGroups(
	_id: Id,

	list: Id
)

data class DebitServicePutData(
	_format: NumberString,

	invoice: IdTuple
)

data class BookingServiceData(
	_format: NumberString,
	amount: NumberString,
	date: ?Date,
	featureType: NumberString
)

data class EntityEventBatch(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,

	events: EntityUpdate[]
)

data class DomainsRef(
	_id: Id,

	items: Id
)

data class AuditLogEntry(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	action: string,
	actorIpAddress: ?string,
	actorMailAddress: string,
	date: Date,
	modifiedEntity: string,

	groupInfo: ?IdTuple,
	modifiedGroupInfo: ?IdTuple
)

data class AuditLogRef(
	_id: Id,

	items: Id
)

data class WhitelabelConfig(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	germanLanguageCode: ?string,
	imprintUrl: ?string,
	jsonTheme: string,
	metaTags: string,
	privacyStatementUrl: ?string,

	bootstrapCustomizations: BootstrapFeature[],
	certificateInfo: CertificateInfo
)

data class BrandingDomainData(
	_format: NumberString,
	domain: string,
	sessionEncPemCertificateChain: ?Uint8Array,
	sessionEncPemPrivateKey: ?Uint8Array,
	systemAdminPubEncSessionKey: Uint8Array
)

data class BrandingDomainDeleteData(
	_format: NumberString,
	domain: string
)

data class U2fRegisteredDevice(
	_id: Id,
	appId: string,
	compromised: boolean,
	counter: NumberString,
	keyHandle: Uint8Array,
	publicKey: Uint8Array
)

data class SecondFactor(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	name: string,
	otpSecret: ?Uint8Array,
	type: NumberString,

	u2f: ?U2fRegisteredDevice
)

data class U2fKey(
	_id: Id,
	appId: string,
	keyHandle: Uint8Array,

	secondFactor: IdTuple
)

data class U2fChallenge(
	_id: Id,
	challenge: Uint8Array,

	keys: U2fKey[]
)

data class Challenge(
	_id: Id,
	type: NumberString,

	otp: ?OtpChallenge,
	u2f: ?U2fChallenge
)

data class Session(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	accessKey: ?Uint8Array,
	clientIdentifier: string,
	lastAccessTime: Date,
	loginIpAddress: ?string,
	loginTime: Date,
	state: NumberString,

	challenges: Challenge[],
	user: Id
)

data class UserAuthentication(
	_id: Id,

	recoverCode: ?Id,
	secondFactors: Id,
	sessions: Id
)

data class CreateSessionData(
	_format: NumberString,
	accessKey: ?Uint8Array,
	authToken: ?string,
	authVerifier: ?string,
	clientIdentifier: string,
	mailAddress: ?string,
	recoverCodeVerifier: ?string,

	user: ?Id
)

data class CreateSessionReturn(
	_format: NumberString,
	accessToken: string,

	challenges: Challenge[],
	user: Id
)

data class U2fResponseData(
	_id: Id,
	clientData: string,
	keyHandle: string,
	signatureData: string
)

data class SecondFactorAuthGetData(
	_format: NumberString,
	accessToken: string
)

data class SecondFactorAuthGetReturn(
	_format: NumberString,
	secondFactorPending: boolean
)

data class OtpChallenge(
	_id: Id,

	secondFactors: IdTuple[]
)

data class BootstrapFeature(
	_id: Id,
	feature: NumberString
)

data class Feature(
	_id: Id,
	feature: NumberString
)

data class WhitelabelChild(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	comment: string,
	createdDate: Date,
	deletedDate: ?Date,
	mailAddress: string,

	customer: Id
)

data class WhitelabelChildrenRef(
	_id: Id,

	items: Id
)

data class WhitelabelParent(
	_id: Id,

	customer: Id,
	whitelabelChildInParent: IdTuple
)

data class UpdateAdminshipData(
	_format: NumberString,
	newAdminGroupEncGKey: Uint8Array,

	group: Id,
	newAdminGroup: Id
)

data class AdministratedGroup(
	_format: NumberString,
	_id: IdTuple,
	_ownerGroup: ?Id,
	_permissions: Id,
	groupType: NumberString,

	groupInfo: IdTuple,
	localAdminGroup: Id
)

data class AdministratedGroupsRef(
	_id: Id,

	items: Id
)

data class CreditCard(
	_id: Id,
	cardHolderName: string,
	cvv: string,
	expirationMonth: string,
	expirationYear: string,
	number: string
)

data class LocationServiceGetReturn(
	_format: NumberString,
	country: string
)

data class OrderProcessingAgreement(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,
	customerAddress: string,
	signatureDate: Date,
	version: string,

	customer: Id,
	signerUserGroupInfo: IdTuple
)

data class SignOrderProcessingAgreementData(
	_format: NumberString,
	customerAddress: string,
	version: string
)

data class GeneratedIdWrapper(
	_id: Id,
	value: Id
)

data class SseConnectData(
	_format: NumberString,
	identifier: string,

	userIds: GeneratedIdWrapper[]
)

data class RecoverCode(
	_format: NumberString,
	_id: Id,
	_ownerGroup: ?Id,
	_permissions: Id,
	recoverCodeEncUserGroupKey: Uint8Array,
	userEncRecoverCode: Uint8Array,
	verifier: Uint8Array
)

data class ResetFactorsDeleteData(
	_format: NumberString,
	authVerifier: string,
	mailAddress: string,
	recoverCodeVerifier: string
)

data class UpgradePriceServiceData(
	_format: NumberString,
	campaign: ?string,
	date: ?Date
)

data class PlanPrices(
	_id: Id,
	additionalUserPriceMonthly: NumberString,
	contactFormPriceMonthly: NumberString,
	firstYearDiscount: NumberString,
	includedAliases: NumberString,
	includedStorage: NumberString,
	monthlyPrice: NumberString,
	monthlyReferencePrice: NumberString
)

data class UpgradePriceServiceReturn(
	_format: NumberString,
	business: boolean,
	messageTextId: ?string,

	premiumPrices: PlanPrices,
	proPrices: PlanPrices
)

data class RegistrationCaptchaServiceGetData(
	_format: NumberString,
	mailAddress: string,
	token: ?string
)

data class WebsocketEntityData(
	_format: NumberString,
	eventBatchId: Id,
	eventBatchOwner: Id,

	eventBatch: EntityUpdate[]
)

data class WebsocketCounterValue(
	_id: Id,
	count: NumberString,
	mailListId: Id
)

data class WebsocketCounterData(
	_format: NumberString,
	mailGroup: Id,

	counterValues: WebsocketCounterValue[]
)

data class CertificateInfo(
	_id: Id,
	expiryDate: ?Date,
	state: NumberString,
	type: NumberString,

	certificate: ?Id
)

data class NotificationMailTemplate(
	_id: Id,
	body: string,
	language: string,
	subject: string
)

data class CalendarEventRef(
	_id: Id,
	elementId: Id,
	listId: Id
)

data class AlarmInfo(
	_id: Id,
	alarmIdentifier: string,
	trigger: string,

	calendarRef: CalendarEventRef
)

data class UserAlarmInfo(
	_errors: Object,
	_format: NumberString,
	_id: IdTuple,
	_ownerEncSessionKey: ?Uint8Array,
	_ownerGroup: ?Id,
	_permissions: Id,

	alarmInfo: AlarmInfo
)

data class UserAlarmInfoListdata class(
	_id: Id,

	alarms: Id
)

data class NotificationSessionKey(
	_id: Id,
	pushIdentifierSessionEncSessionKey: Uint8Array,

	pushIdentifier: IdTuple
)

data class RepeatRule(
	_id: Id,
	endType: NumberString,
	endValue: ?NumberString,
	frequency: NumberString,
	interval: NumberString,
	timeZone: string
)

data class AlarmNotification(
	_id: Id,
	eventEnd: Date,
	eventStart: Date,
	operation: NumberString,
	summary: string,

	alarmInfo: AlarmInfo,
	notificationSessionKeys: NotificationSessionKey[],
	repeatRule: ?RepeatRule,
	user: Id
)

data class AlarmServicePost(
	_errors: Object,
	_format: NumberString,

	alarmNotifications: AlarmNotification[]
)

data class DnsRecord(
	_id: Id,
	subdomain: ?string,
	type: NumberString,
	value: string
)

data class CustomDomainCheckData(
	_format: NumberString,
	domain: string
)

data class CustomDomainCheckReturn(
	_format: NumberString,
	checkResult: NumberString,

	invalidRecords: DnsRecord[],
	missingRecords: DnsRecord[]
)
