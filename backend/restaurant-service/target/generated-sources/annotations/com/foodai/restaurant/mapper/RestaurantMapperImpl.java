package com.foodai.restaurant.mapper;

import com.foodai.restaurant.domain.model.AddressVO;
import com.foodai.restaurant.domain.model.BankAccountVO;
import com.foodai.restaurant.domain.model.ContactVO;
import com.foodai.restaurant.domain.model.ContractVO;
import com.foodai.restaurant.domain.model.DeliveryFeeConfig;
import com.foodai.restaurant.domain.model.DocumentStatus;
import com.foodai.restaurant.domain.model.DocumentVO;
import com.foodai.restaurant.domain.model.OnboardingType;
import com.foodai.restaurant.domain.model.OperatingHoursVO;
import com.foodai.restaurant.domain.model.OutletStatus;
import com.foodai.restaurant.domain.model.OwnerVO;
import com.foodai.restaurant.domain.model.PaymentGatewayFeeConfig;
import com.foodai.restaurant.domain.model.PenaltyConfig;
import com.foodai.restaurant.domain.model.PlatformFeeConfig;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.model.RestaurantStatus;
import com.foodai.restaurant.domain.model.ServiceabilityConfig;
import com.foodai.restaurant.domain.model.ServiceabilityRules;
import com.foodai.restaurant.domain.model.TATConfig;
import com.foodai.restaurant.dto.request.AddressDTO;
import com.foodai.restaurant.dto.request.ContactDTO;
import com.foodai.restaurant.dto.request.ContractDTO;
import com.foodai.restaurant.dto.request.CreateOutletRequest;
import com.foodai.restaurant.dto.request.CreateRestaurantRequest;
import com.foodai.restaurant.dto.request.DeliveryFeeDTO;
import com.foodai.restaurant.dto.request.DocumentDTO;
import com.foodai.restaurant.dto.request.OperatingHoursDTO;
import com.foodai.restaurant.dto.request.OwnerDTO;
import com.foodai.restaurant.dto.request.PaymentGatewayFeeDTO;
import com.foodai.restaurant.dto.request.PenaltyConfigDTO;
import com.foodai.restaurant.dto.request.PlatformFeeDTO;
import com.foodai.restaurant.dto.request.ServiceabilityConfigDTO;
import com.foodai.restaurant.dto.request.TATConfigDTO;
import com.foodai.restaurant.dto.response.AddressResponse;
import com.foodai.restaurant.dto.response.BankAccountResponse;
import com.foodai.restaurant.dto.response.ContactResponse;
import com.foodai.restaurant.dto.response.ContractResponse;
import com.foodai.restaurant.dto.response.DocumentResponse;
import com.foodai.restaurant.dto.response.OperatingHoursResponse;
import com.foodai.restaurant.dto.response.OutletResponse;
import com.foodai.restaurant.dto.response.OwnerResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.dto.response.ServiceabilityResponse;
import com.foodai.restaurant.dto.response.TATConfigResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T15:29:42+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public Restaurant toEntity(CreateRestaurantRequest request) {
        if ( request == null ) {
            return null;
        }

        Restaurant.RestaurantBuilder restaurant = Restaurant.builder();

        restaurant.contacts( toContactVOList( request.getContacts() ) );
        restaurant.contract( toContractVO( request.getContract() ) );
        restaurant.coverImage( maskAccountNumber( request.getCoverImage() ) );
        restaurant.createdBy( maskAccountNumber( request.getCreatedBy() ) );
        List<String> list1 = request.getCuisineTypes();
        if ( list1 != null ) {
            restaurant.cuisineTypes( new ArrayList<String>( list1 ) );
        }
        restaurant.description( maskAccountNumber( request.getDescription() ) );
        restaurant.documents( toDocumentVOList( request.getDocuments() ) );
        restaurant.logo( maskAccountNumber( request.getLogo() ) );
        restaurant.name( maskAccountNumber( request.getName() ) );
        restaurant.owners( toOwnerVOList( request.getOwners() ) );

        restaurant.id( generateId() );
        restaurant.averageRating( (double) 0.0 );
        restaurant.totalReviews( 0 );
        restaurant.totalOrders( 0 );
        restaurant.acceptsOrders( true );
        restaurant.status( RestaurantStatus.PENDING );
        restaurant.createdAt( now() );
        restaurant.updatedAt( now() );
        restaurant.deleted( false );
        restaurant.onboardingType( OnboardingType.SELF_SERVICE );

        return restaurant.build();
    }

    @Override
    public RestaurantResponse toResponse(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }

        RestaurantResponse.RestaurantResponseBuilder restaurantResponse = RestaurantResponse.builder();

        restaurantResponse.acceptsOrders( restaurant.getAcceptsOrders() );
        restaurantResponse.averageRating( restaurant.getAverageRating() );
        restaurantResponse.bankAccount( toBankAccountResponse( restaurant.getBankAccount() ) );
        restaurantResponse.contacts( toContactResponseList( restaurant.getContacts() ) );
        restaurantResponse.contract( toContractResponse( restaurant.getContract() ) );
        restaurantResponse.coverImage( maskAccountNumber( restaurant.getCoverImage() ) );
        restaurantResponse.createdAt( restaurant.getCreatedAt() );
        restaurantResponse.createdBy( maskAccountNumber( restaurant.getCreatedBy() ) );
        List<String> list1 = restaurant.getCuisineTypes();
        if ( list1 != null ) {
            restaurantResponse.cuisineTypes( new ArrayList<String>( list1 ) );
        }
        restaurantResponse.description( maskAccountNumber( restaurant.getDescription() ) );
        restaurantResponse.documents( toDocumentResponseList( restaurant.getDocuments() ) );
        restaurantResponse.fssaiLicenseNumber( maskAccountNumber( restaurant.getFssaiLicenseNumber() ) );
        restaurantResponse.gstNumber( maskAccountNumber( restaurant.getGstNumber() ) );
        restaurantResponse.id( maskAccountNumber( restaurant.getId() ) );
        restaurantResponse.logo( maskAccountNumber( restaurant.getLogo() ) );
        restaurantResponse.name( maskAccountNumber( restaurant.getName() ) );
        restaurantResponse.onboardedBy( maskAccountNumber( restaurant.getOnboardedBy() ) );
        restaurantResponse.onboardingType( restaurant.getOnboardingType() );
        restaurantResponse.outlets( toOutletResponseList( restaurant.getOutlets() ) );
        restaurantResponse.owners( toOwnerResponseList( restaurant.getOwners() ) );
        restaurantResponse.panNumber( maskAccountNumber( restaurant.getPanNumber() ) );
        restaurantResponse.status( restaurant.getStatus() );
        restaurantResponse.totalOrders( restaurant.getTotalOrders() );
        restaurantResponse.totalReviews( restaurant.getTotalReviews() );
        restaurantResponse.updatedAt( restaurant.getUpdatedAt() );
        restaurantResponse.updatedBy( maskAccountNumber( restaurant.getUpdatedBy() ) );

        return restaurantResponse.build();
    }

    @Override
    public List<RestaurantResponse> toResponseList(List<Restaurant> restaurants) {
        if ( restaurants == null ) {
            return null;
        }

        List<RestaurantResponse> list = new ArrayList<RestaurantResponse>( restaurants.size() );
        for ( Restaurant restaurant : restaurants ) {
            list.add( toResponse( restaurant ) );
        }

        return list;
    }

    @Override
    public RestaurantOutlet toOutletEntity(CreateOutletRequest request) {
        if ( request == null ) {
            return null;
        }

        RestaurantOutlet.RestaurantOutletBuilder restaurantOutlet = RestaurantOutlet.builder();

        restaurantOutlet.address( toAddressVO( request.getAddress() ) );
        restaurantOutlet.contacts( toContactVOList( request.getContacts() ) );
        restaurantOutlet.createdBy( maskAccountNumber( request.getCreatedBy() ) );
        restaurantOutlet.deliveryFee( request.getDeliveryFee() );
        restaurantOutlet.minimumOrderValue( request.getMinimumOrderValue() );
        restaurantOutlet.operatingHours( toOperatingHoursVOList( request.getOperatingHours() ) );
        restaurantOutlet.outletCode( maskAccountNumber( request.getOutletCode() ) );
        restaurantOutlet.outletName( maskAccountNumber( request.getOutletName() ) );
        restaurantOutlet.selfDelivery( request.getSelfDelivery() );
        restaurantOutlet.serviceabilityConfig( toServiceabilityConfig( request.getServiceabilityConfig() ) );
        restaurantOutlet.tatConfig( toTATConfig( request.getTatConfig() ) );

        restaurantOutlet.outletId( generateId() );
        restaurantOutlet.status( OutletStatus.NOT_ONBOARDED );
        restaurantOutlet.acceptsOrders( false );
        restaurantOutlet.averageRating( (double) 0.0 );
        restaurantOutlet.totalReviews( 0 );
        restaurantOutlet.totalOrders( 0 );
        restaurantOutlet.currentOrderQueue( 0 );
        restaurantOutlet.createdAt( now() );
        restaurantOutlet.updatedAt( now() );
        restaurantOutlet.deleted( false );

        return restaurantOutlet.build();
    }

    @Override
    public OutletResponse toOutletResponse(RestaurantOutlet outlet) {
        if ( outlet == null ) {
            return null;
        }

        OutletResponse.OutletResponseBuilder outletResponse = OutletResponse.builder();

        outletResponse.acceptsOrders( outlet.getAcceptsOrders() );
        outletResponse.address( toAddressResponse( outlet.getAddress() ) );
        outletResponse.approvedAt( outlet.getApprovedAt() );
        outletResponse.approvedBy( maskAccountNumber( outlet.getApprovedBy() ) );
        outletResponse.averageRating( outlet.getAverageRating() );
        outletResponse.contacts( toContactResponseList( outlet.getContacts() ) );
        outletResponse.createdAt( outlet.getCreatedAt() );
        outletResponse.createdBy( maskAccountNumber( outlet.getCreatedBy() ) );
        outletResponse.currentOrderQueue( outlet.getCurrentOrderQueue() );
        outletResponse.deliveryFee( outlet.getDeliveryFee() );
        outletResponse.minimumOrderValue( outlet.getMinimumOrderValue() );
        outletResponse.operatingHours( toOperatingHoursResponseList( outlet.getOperatingHours() ) );
        outletResponse.outletCode( maskAccountNumber( outlet.getOutletCode() ) );
        outletResponse.outletId( maskAccountNumber( outlet.getOutletId() ) );
        outletResponse.outletName( maskAccountNumber( outlet.getOutletName() ) );
        outletResponse.rejectionReason( maskAccountNumber( outlet.getRejectionReason() ) );
        outletResponse.selfDelivery( outlet.getSelfDelivery() );
        outletResponse.serviceabilityConfig( toServiceabilityResponse( outlet.getServiceabilityConfig() ) );
        outletResponse.status( outlet.getStatus() );
        outletResponse.tatConfig( toTATConfigResponse( outlet.getTatConfig() ) );
        outletResponse.totalOrders( outlet.getTotalOrders() );
        outletResponse.totalReviews( outlet.getTotalReviews() );
        outletResponse.updatedAt( outlet.getUpdatedAt() );
        outletResponse.updatedBy( maskAccountNumber( outlet.getUpdatedBy() ) );

        return outletResponse.build();
    }

    @Override
    public List<OutletResponse> toOutletResponseList(List<RestaurantOutlet> outlets) {
        if ( outlets == null ) {
            return null;
        }

        List<OutletResponse> list = new ArrayList<OutletResponse>( outlets.size() );
        for ( RestaurantOutlet restaurantOutlet : outlets ) {
            list.add( toOutletResponse( restaurantOutlet ) );
        }

        return list;
    }

    @Override
    public OwnerVO toOwnerVO(OwnerDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OwnerVO.OwnerVOBuilder ownerVO = OwnerVO.builder();

        ownerVO.isPrimaryContact( dto.getIsPrimaryContact() );
        ownerVO.ownerEmail( maskAccountNumber( dto.getOwnerEmail() ) );
        ownerVO.ownerId( maskAccountNumber( dto.getOwnerId() ) );
        ownerVO.ownerName( maskAccountNumber( dto.getOwnerName() ) );
        ownerVO.ownerPhone( maskAccountNumber( dto.getOwnerPhone() ) );
        ownerVO.ownershipPercentage( dto.getOwnershipPercentage() );
        ownerVO.role( dto.getRole() );

        return ownerVO.build();
    }

    @Override
    public OwnerResponse toOwnerResponse(OwnerVO vo) {
        if ( vo == null ) {
            return null;
        }

        OwnerResponse.OwnerResponseBuilder ownerResponse = OwnerResponse.builder();

        ownerResponse.addedAt( vo.getAddedAt() );
        ownerResponse.addedBy( maskAccountNumber( vo.getAddedBy() ) );
        ownerResponse.isPrimaryContact( vo.getIsPrimaryContact() );
        ownerResponse.ownerEmail( maskAccountNumber( vo.getOwnerEmail() ) );
        ownerResponse.ownerId( maskAccountNumber( vo.getOwnerId() ) );
        ownerResponse.ownerName( maskAccountNumber( vo.getOwnerName() ) );
        ownerResponse.ownerPhone( maskAccountNumber( vo.getOwnerPhone() ) );
        ownerResponse.ownershipPercentage( vo.getOwnershipPercentage() );
        ownerResponse.role( vo.getRole() );

        return ownerResponse.build();
    }

    @Override
    public List<OwnerVO> toOwnerVOList(List<OwnerDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<OwnerVO> list = new ArrayList<OwnerVO>( dtos.size() );
        for ( OwnerDTO ownerDTO : dtos ) {
            list.add( toOwnerVO( ownerDTO ) );
        }

        return list;
    }

    @Override
    public List<OwnerResponse> toOwnerResponseList(List<OwnerVO> vos) {
        if ( vos == null ) {
            return null;
        }

        List<OwnerResponse> list = new ArrayList<OwnerResponse>( vos.size() );
        for ( OwnerVO ownerVO : vos ) {
            list.add( toOwnerResponse( ownerVO ) );
        }

        return list;
    }

    @Override
    public ContactVO toContactVO(ContactDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ContactVO.ContactVOBuilder contactVO = ContactVO.builder();

        contactVO.alternatePhone( maskAccountNumber( dto.getAlternatePhone() ) );
        contactVO.contactId( maskAccountNumber( dto.getContactId() ) );
        contactVO.contactType( dto.getContactType() );
        contactVO.designation( maskAccountNumber( dto.getDesignation() ) );
        contactVO.email( maskAccountNumber( dto.getEmail() ) );
        contactVO.isActive( dto.getIsActive() );
        contactVO.name( maskAccountNumber( dto.getName() ) );
        contactVO.phone( maskAccountNumber( dto.getPhone() ) );

        return contactVO.build();
    }

    @Override
    public ContactResponse toContactResponse(ContactVO vo) {
        if ( vo == null ) {
            return null;
        }

        ContactResponse.ContactResponseBuilder contactResponse = ContactResponse.builder();

        contactResponse.addedAt( vo.getAddedAt() );
        contactResponse.addedBy( maskAccountNumber( vo.getAddedBy() ) );
        contactResponse.alternatePhone( maskAccountNumber( vo.getAlternatePhone() ) );
        contactResponse.contactId( maskAccountNumber( vo.getContactId() ) );
        contactResponse.contactType( vo.getContactType() );
        contactResponse.designation( maskAccountNumber( vo.getDesignation() ) );
        contactResponse.email( maskAccountNumber( vo.getEmail() ) );
        contactResponse.isActive( vo.getIsActive() );
        contactResponse.name( maskAccountNumber( vo.getName() ) );
        contactResponse.phone( maskAccountNumber( vo.getPhone() ) );

        return contactResponse.build();
    }

    @Override
    public List<ContactVO> toContactVOList(List<ContactDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<ContactVO> list = new ArrayList<ContactVO>( dtos.size() );
        for ( ContactDTO contactDTO : dtos ) {
            list.add( toContactVO( contactDTO ) );
        }

        return list;
    }

    @Override
    public List<ContactResponse> toContactResponseList(List<ContactVO> vos) {
        if ( vos == null ) {
            return null;
        }

        List<ContactResponse> list = new ArrayList<ContactResponse>( vos.size() );
        for ( ContactVO contactVO : vos ) {
            list.add( toContactResponse( contactVO ) );
        }

        return list;
    }

    @Override
    public DocumentVO toDocumentVO(DocumentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DocumentVO.DocumentVOBuilder documentVO = DocumentVO.builder();

        documentVO.remarks( maskAccountNumber( dto.getRemarks() ) );
        documentVO.type( dto.getType() );
        documentVO.url( maskAccountNumber( dto.getUrl() ) );

        documentVO.verificationStatus( DocumentStatus.PENDING );
        documentVO.uploadedAt( now() );

        return documentVO.build();
    }

    @Override
    public DocumentResponse toDocumentResponse(DocumentVO vo) {
        if ( vo == null ) {
            return null;
        }

        DocumentResponse.DocumentResponseBuilder documentResponse = DocumentResponse.builder();

        documentResponse.remarks( maskAccountNumber( vo.getRemarks() ) );
        documentResponse.type( vo.getType() );
        documentResponse.uploadedAt( vo.getUploadedAt() );
        documentResponse.url( maskAccountNumber( vo.getUrl() ) );
        documentResponse.verificationStatus( vo.getVerificationStatus() );

        return documentResponse.build();
    }

    @Override
    public List<DocumentVO> toDocumentVOList(List<DocumentDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<DocumentVO> list = new ArrayList<DocumentVO>( dtos.size() );
        for ( DocumentDTO documentDTO : dtos ) {
            list.add( toDocumentVO( documentDTO ) );
        }

        return list;
    }

    @Override
    public List<DocumentResponse> toDocumentResponseList(List<DocumentVO> vos) {
        if ( vos == null ) {
            return null;
        }

        List<DocumentResponse> list = new ArrayList<DocumentResponse>( vos.size() );
        for ( DocumentVO documentVO : vos ) {
            list.add( toDocumentResponse( documentVO ) );
        }

        return list;
    }

    @Override
    public AddressVO toAddressVO(AddressDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AddressVO.AddressVOBuilder addressVO = AddressVO.builder();

        addressVO.city( maskAccountNumber( dto.getCity() ) );
        addressVO.landmark( maskAccountNumber( dto.getLandmark() ) );
        addressVO.latitude( dto.getLatitude() );
        addressVO.longitude( dto.getLongitude() );
        addressVO.pincode( maskAccountNumber( dto.getPincode() ) );
        addressVO.state( maskAccountNumber( dto.getState() ) );
        addressVO.street( maskAccountNumber( dto.getStreet() ) );

        return addressVO.build();
    }

    @Override
    public AddressResponse toAddressResponse(AddressVO vo) {
        if ( vo == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.city( maskAccountNumber( vo.getCity() ) );
        addressResponse.landmark( maskAccountNumber( vo.getLandmark() ) );
        addressResponse.latitude( vo.getLatitude() );
        addressResponse.longitude( vo.getLongitude() );
        addressResponse.pincode( maskAccountNumber( vo.getPincode() ) );
        addressResponse.state( maskAccountNumber( vo.getState() ) );
        addressResponse.street( maskAccountNumber( vo.getStreet() ) );

        return addressResponse.build();
    }

    @Override
    public BankAccountResponse toBankAccountResponse(BankAccountVO vo) {
        if ( vo == null ) {
            return null;
        }

        BankAccountResponse.BankAccountResponseBuilder bankAccountResponse = BankAccountResponse.builder();

        bankAccountResponse.accountHolderName( maskAccountNumber( vo.getAccountHolderName() ) );
        bankAccountResponse.bankName( maskAccountNumber( vo.getBankName() ) );
        bankAccountResponse.ifscCode( maskAccountNumber( vo.getIfscCode() ) );

        bankAccountResponse.accountNumberLast4( maskAccountNumber(vo != null ? vo.getAccountNumber() : null) );

        return bankAccountResponse.build();
    }

    @Override
    public OperatingHoursVO toOperatingHoursVO(OperatingHoursDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OperatingHoursVO.OperatingHoursVOBuilder operatingHoursVO = OperatingHoursVO.builder();

        operatingHoursVO.closeTime( dto.getCloseTime() );
        operatingHoursVO.dayOfWeek( dto.getDayOfWeek() );
        operatingHoursVO.isClosed( dto.getIsClosed() );
        operatingHoursVO.openTime( dto.getOpenTime() );

        return operatingHoursVO.build();
    }

    @Override
    public OperatingHoursResponse toOperatingHoursResponse(OperatingHoursVO vo) {
        if ( vo == null ) {
            return null;
        }

        OperatingHoursResponse.OperatingHoursResponseBuilder operatingHoursResponse = OperatingHoursResponse.builder();

        operatingHoursResponse.closeTime( vo.getCloseTime() );
        operatingHoursResponse.dayOfWeek( vo.getDayOfWeek() );
        operatingHoursResponse.isClosed( vo.getIsClosed() );
        operatingHoursResponse.openTime( vo.getOpenTime() );

        return operatingHoursResponse.build();
    }

    @Override
    public List<OperatingHoursVO> toOperatingHoursVOList(List<OperatingHoursDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<OperatingHoursVO> list = new ArrayList<OperatingHoursVO>( dtos.size() );
        for ( OperatingHoursDTO operatingHoursDTO : dtos ) {
            list.add( toOperatingHoursVO( operatingHoursDTO ) );
        }

        return list;
    }

    @Override
    public List<OperatingHoursResponse> toOperatingHoursResponseList(List<OperatingHoursVO> vos) {
        if ( vos == null ) {
            return null;
        }

        List<OperatingHoursResponse> list = new ArrayList<OperatingHoursResponse>( vos.size() );
        for ( OperatingHoursVO operatingHoursVO : vos ) {
            list.add( toOperatingHoursResponse( operatingHoursVO ) );
        }

        return list;
    }

    @Override
    public ContractVO toContractVO(ContractDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ContractVO.ContractVOBuilder contractVO = ContractVO.builder();

        contractVO.autoRenewal( dto.getAutoRenewal() );
        contractVO.contractUrl( maskAccountNumber( dto.getContractUrl() ) );
        contractVO.deliveryFee( toDeliveryFeeConfig( dto.getDeliveryFee() ) );
        contractVO.paymentGatewayFee( toPaymentGatewayFeeConfig( dto.getPaymentGatewayFee() ) );
        contractVO.penalties( penaltyConfigDTOToPenaltyConfig( dto.getPenalties() ) );
        contractVO.platformFee( toPlatformFeeConfig( dto.getPlatformFee() ) );
        contractVO.signedAt( dto.getSignedAt() );
        contractVO.signedBy( maskAccountNumber( dto.getSignedBy() ) );

        contractVO.contractId( generateId() );
        contractVO.signed( false );
        contractVO.validFrom( now() );
        contractVO.createdAt( now() );
        contractVO.updatedAt( now() );

        return contractVO.build();
    }

    @Override
    public ContractResponse toContractResponse(ContractVO vo) {
        if ( vo == null ) {
            return null;
        }

        ContractResponse.ContractResponseBuilder contractResponse = ContractResponse.builder();

        contractResponse.autoRenewal( vo.getAutoRenewal() );
        contractResponse.contractId( maskAccountNumber( vo.getContractId() ) );
        contractResponse.contractUrl( maskAccountNumber( vo.getContractUrl() ) );
        contractResponse.createdAt( vo.getCreatedAt() );
        contractResponse.createdBy( maskAccountNumber( vo.getCreatedBy() ) );
        contractResponse.deliveryFee( vo.getDeliveryFee() );
        contractResponse.paymentGatewayFee( vo.getPaymentGatewayFee() );
        contractResponse.penalties( vo.getPenalties() );
        contractResponse.platformFee( vo.getPlatformFee() );
        contractResponse.signed( vo.getSigned() );
        contractResponse.signedAt( vo.getSignedAt() );
        contractResponse.signedBy( maskAccountNumber( vo.getSignedBy() ) );
        contractResponse.updatedAt( vo.getUpdatedAt() );
        contractResponse.updatedBy( maskAccountNumber( vo.getUpdatedBy() ) );
        contractResponse.validFrom( vo.getValidFrom() );
        contractResponse.validUntil( vo.getValidUntil() );

        return contractResponse.build();
    }

    @Override
    public PlatformFeeConfig toPlatformFeeConfig(PlatformFeeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PlatformFeeConfig.PlatformFeeConfigBuilder platformFeeConfig = PlatformFeeConfig.builder();

        platformFeeConfig.feeType( dto.getFeeType() );
        platformFeeConfig.fixedAmountPerOrder( dto.getFixedAmountPerOrder() );
        platformFeeConfig.maxFeePerOrder( dto.getMaxFeePerOrder() );
        platformFeeConfig.minFeePerOrder( dto.getMinFeePerOrder() );
        platformFeeConfig.percentageRate( dto.getPercentageRate() );

        return platformFeeConfig.build();
    }

    @Override
    public DeliveryFeeConfig toDeliveryFeeConfig(DeliveryFeeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DeliveryFeeConfig.DeliveryFeeConfigBuilder deliveryFeeConfig = DeliveryFeeConfig.builder();

        deliveryFeeConfig.customerSharePercentage( dto.getCustomerSharePercentage() );
        deliveryFeeConfig.feeType( dto.getFeeType() );
        deliveryFeeConfig.fixedAmountPerOrder( dto.getFixedAmountPerOrder() );
        deliveryFeeConfig.maxFeePerOrder( dto.getMaxFeePerOrder() );
        deliveryFeeConfig.minFeePerOrder( dto.getMinFeePerOrder() );
        deliveryFeeConfig.payor( dto.getPayor() );
        deliveryFeeConfig.percentageRate( dto.getPercentageRate() );
        deliveryFeeConfig.restaurantSharePercentage( dto.getRestaurantSharePercentage() );

        return deliveryFeeConfig.build();
    }

    @Override
    public PaymentGatewayFeeConfig toPaymentGatewayFeeConfig(PaymentGatewayFeeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PaymentGatewayFeeConfig.PaymentGatewayFeeConfigBuilder paymentGatewayFeeConfig = PaymentGatewayFeeConfig.builder();

        paymentGatewayFeeConfig.feeType( dto.getFeeType() );
        paymentGatewayFeeConfig.fixedAmount( dto.getFixedAmount() );
        paymentGatewayFeeConfig.maxFee( dto.getMaxFee() );
        paymentGatewayFeeConfig.minFee( dto.getMinFee() );
        paymentGatewayFeeConfig.payor( dto.getPayor() );
        paymentGatewayFeeConfig.percentageRate( dto.getPercentageRate() );

        return paymentGatewayFeeConfig.build();
    }

    @Override
    public ServiceabilityConfig toServiceabilityConfig(ServiceabilityConfigDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ServiceabilityConfig.ServiceabilityConfigBuilder serviceabilityConfig = ServiceabilityConfig.builder();

        serviceabilityConfig.maxDeliveryRadius( dto.getMaxDeliveryRadius() );
        List<String> list = dto.getServiceablePincodes();
        if ( list != null ) {
            serviceabilityConfig.serviceablePincodes( new ArrayList<String>( list ) );
        }
        serviceabilityConfig.useRadiusBased( dto.getUseRadiusBased() );
        serviceabilityConfig.rules( serviceabilityConfigDTOToServiceabilityRules( dto ) );

        return serviceabilityConfig.build();
    }

    @Override
    public ServiceabilityResponse toServiceabilityResponse(ServiceabilityConfig config) {
        if ( config == null ) {
            return null;
        }

        ServiceabilityResponse.ServiceabilityResponseBuilder serviceabilityResponse = ServiceabilityResponse.builder();

        serviceabilityResponse.maxDeliveryRadius( config.getMaxDeliveryRadius() );
        List<String> list = config.getServiceablePincodes();
        if ( list != null ) {
            serviceabilityResponse.serviceablePincodes( new ArrayList<String>( list ) );
        }
        serviceabilityResponse.useRadiusBased( config.getUseRadiusBased() );

        return serviceabilityResponse.build();
    }

    @Override
    public TATConfig toTATConfig(TATConfigDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TATConfig.TATConfigBuilder tATConfig = TATConfig.builder();

        tATConfig.basePrepTimeMinutes( dto.getBasePrepTimeMinutes() );
        tATConfig.bufferTimeMinutes( dto.getBufferTimeMinutes() );
        tATConfig.considerBatching( dto.getConsiderBatching() );
        tATConfig.considerDistance( dto.getConsiderDistance() );
        tATConfig.considerRiderAvailability( dto.getConsiderRiderAvailability() );
        tATConfig.maxTATMinutes( dto.getMaxTATMinutes() );
        tATConfig.peakHourExtraMinutes( dto.getPeakHourExtraMinutes() );
        tATConfig.tatPerOrderInQueue( dto.getTatPerOrderInQueue() );

        return tATConfig.build();
    }

    @Override
    public TATConfigResponse toTATConfigResponse(TATConfig config) {
        if ( config == null ) {
            return null;
        }

        TATConfigResponse.TATConfigResponseBuilder tATConfigResponse = TATConfigResponse.builder();

        tATConfigResponse.basePrepTimeMinutes( config.getBasePrepTimeMinutes() );
        tATConfigResponse.bufferTimeMinutes( config.getBufferTimeMinutes() );
        tATConfigResponse.considerBatching( config.getConsiderBatching() );
        tATConfigResponse.considerDistance( config.getConsiderDistance() );
        tATConfigResponse.considerRiderAvailability( config.getConsiderRiderAvailability() );
        tATConfigResponse.maxTATMinutes( config.getMaxTATMinutes() );
        tATConfigResponse.peakHourExtraMinutes( config.getPeakHourExtraMinutes() );
        tATConfigResponse.tatPerOrderInQueue( config.getTatPerOrderInQueue() );

        return tATConfigResponse.build();
    }

    @Override
    public void updateEntityFromRequest(Restaurant restaurant, CreateRestaurantRequest request) {
        if ( request == null ) {
            return;
        }

        if ( restaurant.getContacts() != null ) {
            List<ContactVO> list = toContactVOList( request.getContacts() );
            if ( list != null ) {
                restaurant.getContacts().clear();
                restaurant.getContacts().addAll( list );
            }
        }
        else {
            List<ContactVO> list = toContactVOList( request.getContacts() );
            if ( list != null ) {
                restaurant.setContacts( list );
            }
        }
        if ( request.getContract() != null ) {
            restaurant.setContract( toContractVO( request.getContract() ) );
        }
        if ( request.getCoverImage() != null ) {
            restaurant.setCoverImage( maskAccountNumber( request.getCoverImage() ) );
        }
        if ( restaurant.getCuisineTypes() != null ) {
            List<String> list1 = request.getCuisineTypes();
            if ( list1 != null ) {
                restaurant.getCuisineTypes().clear();
                restaurant.getCuisineTypes().addAll( list1 );
            }
        }
        else {
            List<String> list1 = request.getCuisineTypes();
            if ( list1 != null ) {
                restaurant.setCuisineTypes( new ArrayList<String>( list1 ) );
            }
        }
        if ( request.getDescription() != null ) {
            restaurant.setDescription( maskAccountNumber( request.getDescription() ) );
        }
        if ( restaurant.getDocuments() != null ) {
            List<DocumentVO> list2 = toDocumentVOList( request.getDocuments() );
            if ( list2 != null ) {
                restaurant.getDocuments().clear();
                restaurant.getDocuments().addAll( list2 );
            }
        }
        else {
            List<DocumentVO> list2 = toDocumentVOList( request.getDocuments() );
            if ( list2 != null ) {
                restaurant.setDocuments( list2 );
            }
        }
        if ( request.getLogo() != null ) {
            restaurant.setLogo( maskAccountNumber( request.getLogo() ) );
        }
        if ( request.getName() != null ) {
            restaurant.setName( maskAccountNumber( request.getName() ) );
        }
        if ( restaurant.getOwners() != null ) {
            List<OwnerVO> list3 = toOwnerVOList( request.getOwners() );
            if ( list3 != null ) {
                restaurant.getOwners().clear();
                restaurant.getOwners().addAll( list3 );
            }
        }
        else {
            List<OwnerVO> list3 = toOwnerVOList( request.getOwners() );
            if ( list3 != null ) {
                restaurant.setOwners( list3 );
            }
        }

        restaurant.setUpdatedAt( now() );
    }

    protected PenaltyConfig penaltyConfigDTOToPenaltyConfig(PenaltyConfigDTO penaltyConfigDTO) {
        if ( penaltyConfigDTO == null ) {
            return null;
        }

        PenaltyConfig.PenaltyConfigBuilder penaltyConfig = PenaltyConfig.builder();

        return penaltyConfig.build();
    }

    protected ServiceabilityRules serviceabilityConfigDTOToServiceabilityRules(ServiceabilityConfigDTO serviceabilityConfigDTO) {
        if ( serviceabilityConfigDTO == null ) {
            return null;
        }

        ServiceabilityRules.ServiceabilityRulesBuilder serviceabilityRules = ServiceabilityRules.builder();

        return serviceabilityRules.build();
    }
}
