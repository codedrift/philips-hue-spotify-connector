package alphabetapeter.timer

import alphabetapeter.util.EventBus
import alphabetapeter.util.Loggable
import io.vertx.core.Vertx

class SpotifyPlayerStatusUpdateTimer(private val vertx: Vertx): Loggable {

	private val intervalMillis = 2000L

	fun create() {
		logger.info("Starting spotify status update timer. Running every $intervalMillis ms")
		vertx.setPeriodic(intervalMillis) {
			EventBus(vertx).publish(EventBus.Type.SPOTIFY_UPDATE_TICK.name)
		}
	}

}