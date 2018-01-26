package alphabetapeter.events

import alphabetapeter.clients.SpotifyApiClient
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx


class SpotifyTokenRefreshEventConsumer(
		private val vertx: Vertx,
		private val apiClient: SpotifyApiClient)
	: Loggable {

	fun create() {
		EventBus(vertx)
				.addConsumer(
						EventBus.Type.SPOTIFY_TOKEN_REFRESH_TICK.name,
						Handler {
							apiClient.refreshTokens()
						})
	}
}