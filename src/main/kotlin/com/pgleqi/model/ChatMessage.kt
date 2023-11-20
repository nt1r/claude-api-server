package com.pgleqi.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val uuid: String,
    val text: String,
    val sender: Sender,
    val index: Int,
)

enum class Sender {
    @SerializedName("human")
    Human,
    @SerializedName("assistant")
    Assistant,
}
