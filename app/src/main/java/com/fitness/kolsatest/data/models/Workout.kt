package com.fitness.kolsatest.data.models

data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String
)