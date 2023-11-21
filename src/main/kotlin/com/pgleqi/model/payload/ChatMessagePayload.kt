package com.pgleqi.model.payload

data class AppendMessagePayload(
    val completion: Completion,
    val conversationUuid: String,
    val organizationUuid: String,
    val text: String,
    val attachments: List<String> = emptyList(),
)

data class Completion(
    val prompt: String,
    val timezone: String = "Asia/Shanghai",
    val model: String = "claude-2.0-magenta"
)
