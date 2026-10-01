package com.flatcode.littlemusic.utils

import android.content.Context
import com.flatcode.littlemusic.db.SongDao
import com.flatcode.littlemusic.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object SongCacheManager {

    fun cacheSongAudio(context: Context, song: Song, songDao: SongDao) {
        val link = song.songLink ?: return
        if (link.isEmpty() || !link.startsWith("http")) return

        val songsDir = File(context.cacheDir, "songs").apply { mkdirs() }
        val targetFile = File(songsDir, "${song.id}.mp3")

        if (targetFile.exists() && targetFile.length() > 0) {
            if (song.localPath != targetFile.absolutePath) {
                song.localPath = targetFile.absolutePath
                CoroutineScope(Dispatchers.IO).launch {
                    songDao.insertSong(song)
                }
            }
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(link)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 15000
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val outputStream = FileOutputStream(targetFile)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    song.localPath = targetFile.absolutePath
                    songDao.insertSong(song)
                    Timber.d("Song cached successfully: ${song.name} at ${targetFile.absolutePath}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error caching song audio for ${song.name}")
            }
        }
    }

    fun getJcAudio(song: Song): com.example.jean.jcplayer.model.JcAudio {
        val localPath = song.localPath
        if (!localPath.isNullOrEmpty()) {
            val file = File(localPath)
            if (file.exists() && file.length() > 0) {
                return com.example.jean.jcplayer.model.JcAudio.createFromFilePath(
                    song.name ?: "",
                    file.absolutePath
                )
            }
        }
        return com.example.jean.jcplayer.model.JcAudio.createFromURL(
            song.name ?: "",
            song.songLink ?: ""
        )
    }
}