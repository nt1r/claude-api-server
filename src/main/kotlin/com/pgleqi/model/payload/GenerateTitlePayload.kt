package com.pgleqi.model.payload

data class GenerateTitlePayload(
    val organizationUuid: String,
    val conversationUuid: String,
    val messageContent: String,
    val recentTitles: List<String> = emptyList(),
)
