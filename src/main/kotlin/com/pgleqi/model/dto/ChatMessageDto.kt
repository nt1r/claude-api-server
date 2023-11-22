package com.pgleqi.model.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    val uuid: String,
    val text: String,
    val sender: Sender,
    val index: Int,
    val updatedAt: String,
)

enum class Sender {
    @SerializedName("human")
    Human,

    @SerializedName("assistant")
    Assistant,
}
