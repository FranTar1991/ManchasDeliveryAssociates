package com.manchasdelivery_associates.utils

import java.lang.System.currentTimeMillis


data class MDServer( val associate: String? = null, val phoneNumber: String? = null, val lastTimeUsed: Long? = currentTimeMillis(), val serverStatus: String? = "away" )