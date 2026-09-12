package com.zorenkonte.tibeepost.support

import com.zorenkonte.tibeepost.http.Router
import com.zorenkonte.tibeepost.http.ServerInfo
import com.zorenkonte.tibeepost.model.NotificationDefaults
import com.zorenkonte.tibeepost.model.NotificationPayloadParser

fun testRouter(
    sink: FakeSink = FakeSink(),
    info: ServerInfo = FakeServerInfo(),
    defaults: NotificationDefaults = NotificationDefaults(),
    token: String = "",
) = Router(info, sink, NotificationPayloadParser { "generated-id" }, { defaults }, { token })
