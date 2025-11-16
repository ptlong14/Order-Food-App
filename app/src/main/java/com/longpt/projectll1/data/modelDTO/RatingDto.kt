package com.longpt.projectll1.data.modelDTO

import com.google.firebase.Timestamp

class RatingDto(
    val userId: String = "",
    val userName: String = "",
    val avatarUrl: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val updatedAt: Timestamp = Timestamp.now()
) {
}