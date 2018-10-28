package alphabetapeter.clients

import alphabetapeter.util.EventBus
import alphabetapeter.timer.SpotifyTokenRefreshTimer
import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Future
import io.vertx.core.MultiMap
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import org.apache.commons.codec.binary.Base64


class SpotifyApiClient(private val vertx: Vertx) : Loggable {

	companion object {
		private const val AUTH_URL = "accounts.spotify.com"
		private const val API_URL = "api.spotify.com"
	}

	private val webClient: WebClient = createWebClient()

	fun authenticateWithCode(code: String, redirectUri: String): Future<JsonObject> {
		logger.debug("Requesting Spotify auth tokens")

		val future = Future.future<JsonObject>()
		val formData = MultiMap.caseInsensitiveMultiMap()

		formData.set("grant_type", "authorization_code")
		formData.set("code", code)
		formData.set("redirect_uri", redirectUri)
		webClient
				.post(443, AUTH_URL, "/api/token")
				.putHeader(HttpHeaders.AUTHORIZATION.toString(), getTokenBasicAuth())
				.putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded")
				.sendForm(formData) { asyncResult ->
					if (asyncResult.succeeded()) {
						val tokenResult = asyncResult.result().bodyAsJsonObject()
						logger.info(tokenResult.encodePrettily())
						handleAuthentication(tokenResult)
						val expiresIn = tokenResult.getInteger("expires_in")
						SpotifyTokenRefreshTimer(vertx).create(expiresIn)
						future.complete(tokenResult)
					} else {
						future.fail(asyncResult.cause())
					}
				}

		return future
	}


	fun refreshTokens (): Future<JsonObject>? {
		logger.debug("Refreshing spotify tokens")

		val future = Future.future<JsonObject>()
		val formData = MultiMap.caseInsensitiveMultiMap()

		formData.set("grant_type", "refresh_token")
		formData.set("refresh_token", LocalMap(vertx).getSpotifyRefreshToken())
		webClient
				.post(443, AUTH_URL, "/api/token")
				.putHeader(HttpHeaders.AUTHORIZATION.toString(), getTokenBasicAuth())
				.putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded")
				.sendForm(formData) { asyncResult ->
					if (asyncResult.succeeded()) {
						val tokenResult = asyncResult.result().bodyAsJsonObject()
						handleAuthentication(tokenResult)
						future.complete(tokenResult)
					} else {
						future.fail(asyncResult.cause())
					}
				}
		return future
	}

	private fun handleAuthentication(authResult: JsonObject) {
		logger.info("Got spotify auth result ${authResult.encodePrettily()}")

		val accessToken = authResult.getString("access_token")
		LocalMap(vertx).putSpotifyAccessToken(accessToken)

		val refreshToken = authResult.getString("refresh_token")
		if(refreshToken != null){
			LocalMap(vertx).putSpotifyRefreshToken(refreshToken)
		}
		EventBus(vertx).publish(EventBus.Type.SPOTIFY_UPDATE_TICK.name)
	}

	fun isAuthenticated() : Boolean {
		return LocalMap(vertx).getSpotifyAccessToken() != null
		&& LocalMap(vertx).getSpotifyRefreshToken() != null
	}


	private fun getTokenBasicAuth(): String {
		val clientId = LocalMap(vertx).getSpotifyClientId()
		val clientSecret = LocalMap(vertx).getSpotifyClientSecret()
		val clearAuth = "$clientId:$clientSecret"
		val encodedAuth = Base64.encodeBase64(clearAuth.toByteArray())
		return "Basic ${String(encodedAuth)}"
	}

	private fun getAuthorizationHeader(): String? {
		val accessToken = LocalMap(vertx).getSpotifyAccessToken()
		return "Bearer $accessToken"
	}

	private fun createWebClient(): WebClient {
		val options = WebClientOptions()
				.setSsl(true)
				.setKeepAlive(false)
		return WebClient.create(vertx, options)
	}

	private fun get(requestUri: String): Future<JsonObject> {
		val future = Future.future<JsonObject>()
		logger.debug("GET https://$API_URL$requestUri")
		val authorization = getAuthorizationHeader()
		webClient
				.get(443, API_URL, requestUri)
				.putHeader(HttpHeaders.AUTHORIZATION.toString(), authorization)
				.send { asyncResult ->
					if (asyncResult.succeeded()) {
						future.complete(asyncResult.result().bodyAsJsonObject())
					} else {
						future.fail(asyncResult.cause())
					}
				}
		return future
	}
	fun getUserInfo(): Future<JsonObject> {
		return get("/v1/me")
	}

	fun getPlayerStatus(): Future<JsonObject> {
		return get("/v1/me/player")
	}

	fun getUserInfo(username: String): Future<JsonObject> {
		return get("/v1/users/$username")
	}

	fun getPlaylists(username: String, limit: Int, offset: Int): Future<JsonObject> {
		return get("/v1/users/$username/playlists?limit=$limit&offset=$offset")
	}

}
