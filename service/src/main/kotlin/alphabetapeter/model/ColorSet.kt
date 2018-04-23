package alphabetapeter.model

import alphabetapeter.util.JsonMappable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.core.shareddata.Shareable

class ColorSet (
		@JsonProperty("hex") var hex: String = "",
		@JsonProperty("rgb") var rgb: RGB = RGB()
): JsonMappable, Shareable {
	companion object {
		fun fromJsonObject(playJson: JsonObject): ColorSet =
				jacksonObjectMapper().readValue<ColorSet>(playJson.encode(), ColorSet::class.java)
	}
}