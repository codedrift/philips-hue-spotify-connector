package alphabetapeter.events

import alphabetapeter.clients.PhilipsHueApiClient
import alphabetapeter.color.ColorConverter
import alphabetapeter.util.EventBus
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.CompositeFuture
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj
import java.util.*


class ColorChangeEventConsumer(
		private val vertx: Vertx,
		private val apiClient: PhilipsHueApiClient)
	: Loggable {

	companion object {
		private const val ERROR_MESSAGE = "Failed to match philips hue lights"
	}

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
				val spotifyStatus = LocalMap(vertx).getSpotifyStatus()
//				val color = spotifyStatus.mainColor

				if(spotifyStatus.eligibleColors.isNotEmpty()) {
					val lights = it.result().map {
						val color = spotifyStatus.eligibleColors[Random().nextInt(spotifyStatus.eligibleColors.size)].color
						val light = it.value as JsonObject
						val modelId = light.getString("modelid")
						val name = light.getString("name")
						val xy = ColorConverter.convertRgbToXy(color.rgb, modelId)
						val setting = Json.obj("xy" to xy)
						logger.info("Setting light $name to ${color.hex} - Model $modelId")
						apiClient.setLight(it.key, setting)
					}
					CompositeFuture.all(lights).setHandler({
						if (it.succeeded()) {
							EventBus(vertx).publish(EventBus.Type.PHILIPS_HUE_MATCHED.name)
						} else {
							logger.info(ERROR_MESSAGE, it.cause())
						}
					})
				} else {
					logger.warn("$ERROR_MESSAGE - No color found")
				}
			} else {
				logger.error(ERROR_MESSAGE, it.cause())
			}
		})
	}

	private fun canMatchLights(): Boolean {
		return LocalMap(vertx).getLightMatchingEnabled()
				&& apiClient.isConfigured()
	}

}