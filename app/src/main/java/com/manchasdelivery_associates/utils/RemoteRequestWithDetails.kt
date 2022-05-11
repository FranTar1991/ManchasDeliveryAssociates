package com.manchasdelivery_associates.utils

data class RemoteRequestWithDetails(val details: String? = null,
                                    var id: String? = null,
                                    var userName: String? = null,

                                    var userPhone: String? = null,
                                    val locationBAddressLat: Double? = null,
                                    val locationBAddressLong: Double? = null,

                                    val trackingLat: Double? = null,
                                    val trackingLong: Double? = null,

                                    val price: String? = null,
                                    var agentName: String? = null,
                                    var agentPhone: String? = null,

                                    var date: String? = null,
                                    val status: String? = null,
                                    val title: String? = null,

                                    val type: String? = null,
                                    val userAddressReference: String? = null,
                                    val locationBAddressReference: String? = null,
                                    val userAddressLat: Double? = null,
                                    val userAddressLong: Double? = null  )