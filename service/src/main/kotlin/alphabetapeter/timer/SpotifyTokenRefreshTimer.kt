package alphabetapeter.timer

import alphabetapeter.events.EventBus
import alphabetapeter.util.Loggable
import io.vertx.core.Vertx

class SpotifyTokenRefreshTimer(private val vertx: Vertx): Loggable {

	fun create(expiresIn: Int) {
		val interval = ((expiresIn - 60) * 1000).toLong()
		logger.info("Starting spotify token refresh timer. Running $interval ms")
		vertx.setPeriodic(interval) {
			EventBus(vertx).publish(EventBus.Type.SPOTIFY_TOKEN_REFRESH_TICK.name)
		}
	}

}