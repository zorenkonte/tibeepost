package com.zorenkonte.tibeepost.support

import com.zorenkonte.tibeepost.http.Router
import com.zorenkonte.tibeepost.http.ServerInfo
import com.zorenkonte.tibeepost.http.StaticAssets
import com.zorenkonte.tibeepost.model.NotificationDefaults
import com.zorenkonte.tibeepost.model.NotificationPayloadParser

fun testRouter(
    sink: FakeSink = FakeSink(),
    info: ServerInfo = FakeServerInfo(),
    defaults: NotificationDefaults = NotificationDefaults(),
    token: String = "",
    assets: StaticAssets = StaticAssets.Empty,
) = Router(info, sink, NotificationPayloadParser { "generated-id" }, { defaults }, { token }, assets)
