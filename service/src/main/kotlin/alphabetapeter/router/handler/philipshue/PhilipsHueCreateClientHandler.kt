package alphabetapeter.router.handler.philipshue

import alphabetapeter.clients.PhilipsHueApiClient
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj
import org.apache.commons.lang3.StringUtils

class PhilipsHueCreateClientHandler(
		private val vertx: Vertx,
		private val philipsHueApiClient: PhilipsHueApiClient
		) : Handler<RoutingContext>, Loggable {

	override fun handle(routingContext: RoutingContext) {

		routingContext.request().bodyHandler {
			val requestBody = it.toJsonObject()
			val clientName = requestBody.getString("clientName")
			val bridgeIp = requestBody.getString("bridgeIp")
			val userName = requestBody.getString("userName")

			if(StringUtils.isNotBlank(bridgeIp)){
				LocalMap(vertx).putHueBridgeIp(bridgeIp)
			}

			if(StringUtils.isNotBlank(userName)) {
				LocalMap(vertx).putHueUserName(userName)
				routingContext.response().end(Json.obj(
						"userName" to userName,
						"bridgeIp" to bridgeIp
				).encode())
			}

			philipsHueApiClient.authenticate(clientName).setHandler {
				if (it.succeeded()) {
					routingContext.response().end(it.result().encode())
				} else {
					routingContext.fail(it.cause())
				}
			}
		}
	}
}