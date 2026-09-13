package com.zorenkonte.tibeepost.bridge

import android.content.res.AssetManager
import com.zorenkonte.tibeepost.http.ContentTypes
import com.zorenkonte.tibeepost.http.StaticAsset
import com.zorenkonte.tibeepost.http.StaticAssets
import java.io.IOException

class AssetStaticAssets(private val assetManager: AssetManager, private val root: String) : StaticAssets {
    override fun read(path: String): StaticAsset? = try {
        assetManager.open("$root/$path").use { StaticAsset(it.readBytes(), ContentTypes.forPath(path)) }
    } catch (_: IOException) {
        null
    }
}
