package alphabetapeter.model

import alphabetapeter.util.JsonMappable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Shareable

class RGB (
		@JsonProperty("r") var r: Int = 0,
		@JsonProperty("g") var g: Int = 0,
		@JsonProperty("b") var b: Int = 0
): JsonMappable, Shareable {
	companion object {
		fun fromJsonObject(playJson: JsonObject): RGB =
				jacksonObjectMapper().readValue<RGB>(playJson.encode(), RGB::class.java)
	}
}