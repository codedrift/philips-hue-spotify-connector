package alphabetapeter.router.handler

import alphabetapeter.util.LocalMap
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj

class ConfigGetHandler(private val vertx: Vertx) : Handler<RoutingContext> {

	override fun handle(routingContext: RoutingContext) {
		val localMap = LocalMap(vertx)
		val response = Json.obj(
				"lightMatching" to localMap.getLightMatchingEnabled(),

				"philipsHue" to Json.obj(
						"bridgeIp" to localMap.getHueBridgeIp(),
						"userName" to localMap.getHueUsername()
				),

				"spotify" to Json.obj(
						"clientId" to localMap.getSpotifyClientId(),
						"clientSecret" to localMap.getSpotifyClientSecret(),
						"accessToken" to localMap.getSpotifyAccessToken(),
						"refreshToken" to localMap.getSpotifyRefreshToken()
				)
		)
		routingContext.response().end(response.encode())
	}
}