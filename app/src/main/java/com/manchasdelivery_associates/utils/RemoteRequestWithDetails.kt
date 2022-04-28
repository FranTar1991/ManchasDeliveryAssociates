package com.manchasdelivery_associates.utils

data class RemoteRequestWithDetails(val details: String? = null,
                                    val id: String? = null,
                                    val locationBAddressLat: Double? = null,
                                    val locationBAddressLong: Double? = null,
                                    val price: String? = null,
                                    val status: String? = null,
                                    val title: String? = null,
                                    val trackingLat: Double? = null,
                                    val trackingLong: Double? = null,
                                    val type: String? = null,
                                    val userAddressLat: Double? = null,
                                    val userAddressLong: Double? = null  )