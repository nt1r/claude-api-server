package com.pgleqi.model.dto

data class TokenStreamDto(
    val completion: String,
    val stopReason: String?,
)