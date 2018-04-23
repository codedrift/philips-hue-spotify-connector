package alphabetapeter.router.handler

import alphabetapeter.util.EventBus
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

class LightMatchingSettingUpdateHandler(private val vertx: Vertx) : Handler<RoutingContext>, Loggable {

	override fun handle(routingContext: RoutingContext) {
		routingContext.request().bodyHandler({
			val requestBody = it.toJsonObject()
			val enabled = requestBody.getBoolean("enabled")
			logger.info("Light matching ${requestBody.encode()}")
			LocalMap(vertx).putLightMatchingEnabled(enabled)
			if(enabled) {
				EventBus(vertx).publish(EventBus.Type.COLOR_CHANGE.name)
			}
			routingContext.response().end()
		})
	}

}