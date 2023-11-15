package com.pgleqi.service

import java.io.File

object FileService {
    const val APP_SETTINGS_FILE_PATH = "settings.json"

    fun readTextFile(filePath: String): String? {
        return try {
            File(filePath).readText()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}