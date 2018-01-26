package alphabetapeter.router.handler.spotify

import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

class SpotifyPlayerStatusGetHandler(
		private val vertx: Vertx)
	: Handler<RoutingContext>, Loggable {

	override fun handle(routingContext: RoutingContext) {
		routingContext.response().end(LocalMap(vertx).getSpotifyStatus().encode())
	}



}