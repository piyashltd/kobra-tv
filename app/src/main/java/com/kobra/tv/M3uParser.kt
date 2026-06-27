package com.kobra.tv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object M3uParser {
    suspend fun fetchChannels(m3uUrl: String): List<Channel> = withContext(Dispatchers.IO) {
        val channels = mutableListOf<Channel>()
        try {
            val content = URL(m3uUrl).readText()
            val lines = content.split("\n")
            
            var currentName = "Unknown"
            var currentGroup = "TITAN"
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("#EXTINF:")) {
                    val commaIndex = trimmed.lastIndexOf(',')
                    if (commaIndex != -1) {
                        currentName = trimmed.substring(commaIndex + 1).trim()
                    }
                    
                    val groupMatch = Regex("group-title=\"([^\"]+)\"").find(trimmed)
                    if (groupMatch != null) {
                        currentGroup = groupMatch.groupValues[1].uppercase()
                    }
                } else if (trimmed.startsWith("http")) {
                    channels.add(Channel(currentName, currentGroup, trimmed))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext channels
    }
}
