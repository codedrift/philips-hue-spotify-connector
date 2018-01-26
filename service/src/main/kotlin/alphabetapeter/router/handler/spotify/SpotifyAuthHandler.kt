package alphabetapeter.router.handler.spotify

import alphabetapeter.clients.SpotifyApiClient
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext

class SpotifyAuthHandler(private val spotifyApiClient: SpotifyApiClient) : Handler<RoutingContext> {

	override fun handle(routingContext: RoutingContext) {
		routingContext.request().bodyHandler({
			val requestBody = it.toJsonObject()
			val authCode = requestBody.getString("code")
			val redirectUri = requestBody.getString("redirectUri")
			spotifyApiClient.authenticateWithCode(authCode, redirectUri)
					.setHandler({
						if (it.succeeded()) {
							routingContext.response().end(it.result().encode())
						} else {
							routingContext.fail(it.cause())
						}
					})
		})
	}

}