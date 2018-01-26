package alphabetapeter.events

import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class EventBus(private val vertx: Vertx) {

	enum class Type {
		PHILIPS_HUE_MATCHED,
		PHILIPS_UPDATE_TICK,
		SPOTIFY_STATE_CHANGE,
		SPOTIFY_UPDATE_TICK,
		SPOTIFY_TOKEN_REFRESH_TICK,
		COLOR_CHANGE,
	}

	fun addConsumer (key: String, handler: Handler<Message<JsonObject>>): EventBus {
		vertx.eventBus().consumer<JsonObject>(key, handler)
		return this
	}

	fun publish (key: String, message: JsonObject = JsonObject()) {
		vertx.eventBus().publish(key, message)
	}
}