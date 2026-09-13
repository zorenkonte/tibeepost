package com.zorenkonte.tibeepost.support

import com.zorenkonte.tibeepost.http.ContentTypes
import com.zorenkonte.tibeepost.http.StaticAsset
import com.zorenkonte.tibeepost.http.StaticAssets

class FakeAssets(private val files: Map<String, String>) : StaticAssets {
    override fun read(path: String): StaticAsset? =
        files[path]?.let { StaticAsset(it.toByteArray(), ContentTypes.forPath(path)) }
}
