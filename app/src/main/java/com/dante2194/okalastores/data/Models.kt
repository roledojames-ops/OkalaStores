package com.dante2194.okalastores.data

import kotlinx.serialization.Serializable

@Serializable
data class NearbyResponse(
    val success: Boolean = false,
    val data: NearbyData? = null,
    val message: String? = null,
)

@Serializable
data class NearbyData(
    val partnerCount: Int = 0,
    val cityId: Int? = null,
    val emptyStatus: String? = null,
    val stores: List<StoreDto> = emptyList(),
    val filters: FiltersDto? = null,
    val isIran: Boolean = false,
)

@Serializable
data class FiltersDto(
    val storeTypeFilters: List<StoreTypeFilterDto> = emptyList(),
)

@Serializable
data class StoreTypeFilterDto(
    val title: String = "",
    val description: String = "",
    val priority: Int = 0,
    val storeCount: Int = 0,
)

@Serializable
data class StoreDto(
    val id: String? = null,
    val storeId: Int = 0,
    val storeName: String = "",
    val storeType: String? = null,
    val storeCategoryType: String? = null,
    val storeCategoryName: String? = null,
    val partnerName: String? = null,
    val logoUrl: String? = null,
    val logo: String? = null,
    val rate: Double = 0.0,
    val csatCount: Int = 0,
    val distance: Double? = null,
    val earliestSlot: SlotDto? = null,
    val deliveryPrice: Double? = null,
    val operationPrice: Double? = null,
    val packagingPrice: Double? = null,
    val minimumOrder: Double? = null,
    val storeLabels: List<StoreLabelDto> = emptyList(),
    val deliveryMethods: List<DeliveryMethodDto> = emptyList(),
    val isFastDelivery: Boolean = false,
    val isHighPriorityStore: Boolean = false,
    val isActive: Boolean = true,
    val isExist: Boolean = true,
    val isServes: Boolean = true,
    val successOrdersCount: Int = 0,
) {
    val displayLogo: String?
        get() = logoUrl ?: logo

    val isClosed: Boolean
        get() = earliestSlot?.isClosed == true

    val etaCaption: String?
        get() = earliestSlot?.caption

    val categoryKey: String
        get() = storeCategoryType ?: storeType ?: "other"
}

@Serializable
data class SlotDto(
    val isOnDemand: Boolean = false,
    val isScheduled: Boolean = false,
    val isClosed: Boolean = false,
    val caption: String? = null,
    val from: String? = null,
    val to: String? = null,
)

@Serializable
data class StoreLabelDto(
    val id: String? = null,
    val title: String = "",
    val color: String? = null,
    val backgroundColor: String? = null,
)

@Serializable
data class DeliveryMethodDto(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val radius: Double? = null,
    val defaultRadius: Double? = null,
    val premiumAvailable: Boolean = false,
    val premiumCaption: String? = null,
    val premiumEta: String? = null,
)

@Serializable
data class DetailResponse(
    val success: Boolean = false,
    val data: StoreDetailDto? = null,
    val message: String? = null,
)

@Serializable
data class StoreDetailDto(
    val storeId: Int = 0,
    val storeName: String = "",
    val storeType: String? = null,
    val storeCategoryName: String? = null,
    val partnerName: String? = null,
    val logoUrl: String? = null,
    val headerImageUrl: String? = null,
    val earliestSlot: SlotDto? = null,
    val minimumOrder: Double? = null,
    val rate: Double = 0.0,
    val csatCount: Int = 0,
    val distance: Double? = null,
    val storeLabels: List<StoreLabelDto> = emptyList(),
    val deliveryMethods: List<DeliveryMethodDto> = emptyList(),
    val storeLat: Double? = null,
    val storeLng: Double? = null,
    val storeAddress: String? = null,
    val storeStatusMessage: String? = null,
    val isActive: Boolean = true,
    val isExist: Boolean = true,
    val isServes: Boolean = true,
    val packagingPrice: Double? = null,
    val operationPrice: Double? = null,
    val deliveryPrice: Double? = null,
) {
    val isClosed: Boolean
        get() = earliestSlot?.isClosed == true

    val etaCaption: String?
        get() = earliestSlot?.caption
}
