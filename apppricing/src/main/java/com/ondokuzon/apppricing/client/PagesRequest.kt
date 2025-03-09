package com.ondokuzon.apppricing.client

import com.ondokuzon.apppricing.api.datasource.remote.model.PagesApiRequest

data class PagesRequest(
    val deviceId: String,
    val pageName: String
)

internal fun PagesRequest.toApiModel(): PagesApiRequest {
    return PagesApiRequest(
        device_id = deviceId,
        page_name = pageName
    )
}
