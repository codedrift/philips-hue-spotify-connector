package alphabetapeter.util

import io.vertx.core.json.JsonObject

interface JsonMappable {

	fun toJsonObject(): JsonObject {
		return io.vertx.core.json.JsonObject(io.vertx.core.json.Json.encode(this))
	}
}