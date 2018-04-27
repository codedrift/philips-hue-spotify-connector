package alphabetapeter.router.handler.philipshue

import alphabetapeter.util.EventBus
import alphabetapeter.util.Loggable
import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext

class PhilipsHueStatusShuffleHandler(private val vertx: Vertx) : Handler<RoutingContext>, Loggable {

	override fun handle(routingContext: RoutingContext) {
		LOGGER.info("Shuffleing lights")
		EventBus(vertx).publish(EventBus.Type.COLOR_CHANGE.name)
						routingContext.response().end()

	}

}