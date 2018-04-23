package alphabetapeter.model

import alphabetapeter.util.JsonMappable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Shareable

class PlayStatus (
		@JsonProperty("artworkUrl") var artworkUrl: String = "",
		@JsonProperty("colors") var colors: List<ColorSet> = listOf(),
		@JsonProperty("artists") var artists: String = "",
		@JsonProperty("song") var song: String = "",
		@JsonProperty("album") var album: String = "",
		@JsonProperty("mainColor") var mainColor: ColorSet = ColorSet(),
		@JsonProperty("eligibleColors") var eligibleColors: List<RatedColor> = listOf()
): JsonMappable, Shareable {
	companion object {
		fun fromJsonObject(playJson: JsonObject): PlayStatus =
				jacksonObjectMapper().readValue<PlayStatus>(playJson.encode(), PlayStatus::class.java)
	}
}