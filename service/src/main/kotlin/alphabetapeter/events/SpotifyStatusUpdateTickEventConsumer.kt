package alphabetapeter.events

import alphabetapeter.clients.SpotifyApiClient
import alphabetapeter.color.ColorPaletteBuilder
import alphabetapeter.model.ColorSet
import alphabetapeter.util.EventBus
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject


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
					updatePlayStatus(body)
				}
			} else {
				logger.error("Failed to update spotify status", it.cause().message)
			}
		})
	}

	private fun updatePlayStatus(body: JsonObject) {
		val artworkUrl = selectAlbumArtwork(body)

		val playStatus = LocalMap(vertx).getSpotifyStatus()

		val item = body
				.getJsonObject("item")

		playStatus.artists = item
				.getJsonArray("artists")
				.map{ it as JsonObject}
				.joinToString(", ") { it.getString("name")}

		playStatus.album = item.getJsonObject("album").getString("name")
		playStatus.song = item.getString("name")

		if(!artworkUrl.isBlank() && playStatus.artworkUrl != artworkUrl) {
			playStatus.artworkUrl = artworkUrl

			val colorPaletteBuilder = ColorPaletteBuilder()
			val colorPalettes = colorPaletteBuilder.buildPalettesFromUrl(artworkUrl)
			val eligibleColors = colorPaletteBuilder.chooseHueColor(colorPalettes)
			val mainColor = eligibleColors.lastOrNull()

			playStatus.colors = colorPalettes.map {
				ColorSet(it.hex, it.rgb)
			}

			playStatus.eligibleColors = eligibleColors

			if (mainColor != null) {
				playStatus.mainColor = mainColor.color
				logger.info("Album color changed to ${mainColor.color.hex}")
				EventBus(vertx).publish(EventBus.Type.COLOR_CHANGE.name)
			}
		}

		LocalMap(vertx).putSpotifyStatus(playStatus)
		val playStatusJson = playStatus.toJsonObject()
		EventBus(vertx).publish(EventBus.Type.SPOTIFY_STATE_CHANGE.name, playStatusJson)
		logger.debug("Spotify player status:\n$playStatusJson")

	}

	private fun selectAlbumArtwork(album: JsonObject): String {
		val maybeAlbumArt = album
				.getJsonObject("item").getJsonObject("album").getJsonArray("images")
				.map { it as JsonObject }
				.sortedBy { it.getInteger("width") }
				.lastOrNull()
		return if(maybeAlbumArt != null) {
			maybeAlbumArt.getString("url")
		} else {
			""
		}
	}

}