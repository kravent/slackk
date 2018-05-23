package me.agaman.slackk.bot.result

import com.google.gson.annotations.SerializedName
import me.agaman.slackk.bot.data.User

data class UserListResult(
        val members: List<User>,
        @SerializedName("response_metadata")
        val responseMetadata: Metadata
) {
    data class Metadata(
            @SerializedName("next_cursor")
            val nextCursor: String?
    )
}
