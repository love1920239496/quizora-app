package com.example

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

object SafeAssetHelper {
    suspend fun loadJsonAsset(context: Context, fileName: String): String? = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            return@withContext inputStream.bufferedReader().use { it.readText() }
        } catch(e: Exception) { null }
        finally { try { inputStream?.close() } catch(e: Exception) {} }
    }
}
