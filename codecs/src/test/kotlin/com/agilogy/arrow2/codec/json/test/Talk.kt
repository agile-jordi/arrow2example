package com.agilogy.arrow2.codec.json.test

import java.time.Instant

data class Talk(val title: String, val startTime: Instant)
data class RoomSchedule(val room: String, val talks: List<Talk>)