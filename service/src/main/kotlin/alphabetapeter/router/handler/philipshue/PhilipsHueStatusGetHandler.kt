package alphabetapeter.router.handler.philipshue

import alphabetapeter.clients.PhilipsHueApiClient
import alphabetapeter.color.ColorConverter
import alphabetapeter.util.Loggable
import com.philips.lighting.hue.sdk.utilities.PHUtilities
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.CompositeFuture
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj

class PhilipsHueStatusGetHandler(private val philipsHueApiClient: PhilipsHueApiClient) : Handler<RoutingContext>, Loggable {

	override fun handle(routingContext: RoutingContext) {
		if(!philipsHueApiClient.isConfigured()){
			val errorMessage = "Philips hue not configured"
			routingContext.response()
					.setStatusCode(HttpResponseStatus.UNAUTHORIZED.code())
					.end(errorMessage)
			logger.warn(errorMessage)
			return
		}
		philipsHueApiClient.getLights().setHandler {
			if (it.succeeded()) {
				val lights = it.result().map {
					philipsHueApiClient.getLight(it.key)
				}
				CompositeFuture.all(lights).setHandler {
					if (it.succeeded()) {
						val result = it.result().list<JsonObject>() as List<JsonObject>
						val combinedResult = result.map {

							logger.debug("light ${it.encodePrettily()}")

							val modelId = it.getString("modelid")
							val state = it.getJsonObject("state")

							val xy = state.getJsonArray("xy")
									.map { it as Double }
									.map { it.toFloat() }
									.toFloatArray()

							val colorFromXY = PHUtilities.colorFromXY(xy, modelId)
							val hexColor = ColorConverter.convertIntToHex(colorFromXY)

							state.put("hex", hexColor)

							Json.obj(
									"name" to it.getString("name"),
									"state" to state
							)
						}
						val status = Json.obj("lights" to combinedResult)
						routingContext.response().end(status.encode())
					}
				}
			} else {
				logger.error("Failed to get philips hue state", it.cause())
				routingContext.fail(it.cause())
			}
		}
	}

}