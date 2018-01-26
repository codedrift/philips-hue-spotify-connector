package alphabetapeter.events

import alphabetapeter.clients.PhilipsHueApiClient
import alphabetapeter.color.ColorConverter
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.CompositeFuture
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj


class ColorChangeEventConsumer(
		private val vertx: Vertx,
		private val apiClient: PhilipsHueApiClient)
	: Loggable {

	fun create() {
		EventBus(vertx)
				.addConsumer(
						EventBus.Type.COLOR_CHANGE.name,
						Handler {
							matchLights()
						})
	}

	private fun matchLights() {
		if(!canMatchLights()){
			return
		}
		logger.info("Matching philips hue lights")
		apiClient.getLights().setHandler({
			if (it.succeeded()) {
				val color = LocalMap(vertx).getHueColor()
				if (color != null) {
					val lights = it.result().map {
						val light = it.value as JsonObject
						val modelId = light.getString("modelid")
						val name = light.getString("name")
						val xy = ColorConverter.convertRgbToXy(color.rgb, modelId)
						val setting = Json.obj("xy" to xy)
						logger.info("Setting light $name to ${color.rgb} - Model $modelId")
						apiClient.setLight(it.key, setting)
					}
					CompositeFuture.all(lights).setHandler({
						if (it.succeeded()) {
							EventBus(vertx).publish(EventBus.Type.PHILIPS_HUE_MATCHED.name)
						} else {
							logger.info("Failed to match philips hue lights", it.cause())
						}
					})
				} else {
					logger.warn("Failed to match philips hue lights - No color found")
				}
			} else {
				logger.error("Failed to fetch philips hue lights", it.cause())
			}
		})
	}

	private fun canMatchLights(): Boolean {
		return LocalMap(vertx).getLightMatchingEnabled()
				&& apiClient.isConfigured()
	}

}