package alphabetapeter.model

import alphabetapeter.util.JsonMappable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Shareable

class RatedColor(
		@JsonProperty("color") val color: ColorSet,
		@JsonProperty("highestDeviation") val highestDeviation: Int,
		@JsonProperty("deviationAverage") val deviationAverage: Double,
		@JsonProperty("colorful") val colorful: Boolean
) : JsonMappable, Shareable {
	companion object {
		fun fromJsonObject(playJson: JsonObject): RatedColor =
				jacksonObjectMapper().readValue<RatedColor>(playJson.encode(), RatedColor::class.java)
	}
}