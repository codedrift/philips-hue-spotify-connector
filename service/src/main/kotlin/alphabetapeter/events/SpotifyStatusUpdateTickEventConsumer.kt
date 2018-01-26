package alphabetapeter.events

import alphabetapeter.clients.SpotifyApiClient
import alphabetapeter.color.ColorPaletteBuilder
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.obj


class SpotifyStatusUpdateTickEventConsumer(
		private val vertx: Vertx,
		private val apiClient: SpotifyApiClient)
	: Loggable {

	fun create() {
		EventBus(vertx)
				.addConsumer(
						EventBus.Type.SPOTIFY_UPDATE_TICK.name,
						Handler {
							updateSpotifyStatus()
						})
	}

	private fun updateSpotifyStatus() {
		if(!apiClient.isAuthenticated()) {
			return
		}
		logger.info("Updating spotify player status")
		apiClient.getPlayerStatus().setHandler({
			if (it.succeeded()) {
				val body = it.result()
				if(body != null){
					val playStatus = parsePlayStatus(body)
					logger.debug("Spotify player status:\n${playStatus.encodePrettily()}")
					LocalMap(vertx).putSpotifyStatus(playStatus)
					EventBus(vertx).publish(EventBus.Type.SPOTIFY_STATE_CHANGE.name, playStatus)
				}
			} else {
				logger.error("Failed to update spotify status", it.cause().message)
			}
		})
	}

	private fun parsePlayStatus(body: JsonObject): JsonObject {
		val album = body.getJsonObject("item").getJsonObject("album")

		val image = selectCoverImage(album)

		val playStatus = JsonObject()
				.put("status", body)
				.put("artwork", image)

		if (image != null) {
			val colorPaletteBuilder = ColorPaletteBuilder()
			val colorPalettes = colorPaletteBuilder.buildPalettesFromUrl(image)
			val chosenPalette = colorPaletteBuilder.chooseHueColor(colorPalettes)

			playStatus.put("colors", colorPalettes.map {
				Json.obj(
						"hex" to it.hex
				)
			})

			if (chosenPalette != null) {

				val newColor = ColorPaletteBuilder.HueColor(
						chosenPalette.hex,
						chosenPalette.rgb
				)


				playStatus.put("hue_color", newColor.hex)

				updateColor(newColor)
			}
		}
		return playStatus
	}

	private fun updateColor(newColor: ColorPaletteBuilder.HueColor) {
		val lastColor = LocalMap(vertx).getHueColor()
		if (lastColor == null || !lastColor.hex.equals(newColor.hex, true)) {
			logger.info("Color changed: \nCurrent - $lastColor\nNew - $newColor")
			LocalMap(vertx).putHueColor(newColor)
			EventBus(vertx).publish(EventBus.Type.COLOR_CHANGE.name)
		}
	}

	private fun selectCoverImage(album: JsonObject): String? {
		return album
				.getJsonArray("images")
				.map { it as JsonObject }
				.sortedBy { it.getInteger("width") }
				.lastOrNull()?.getString("url")
	}

}