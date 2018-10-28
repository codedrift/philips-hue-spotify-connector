package alphabetapeter.clients

import alphabetapeter.util.LocalMap
import alphabetapeter.util.Loggable
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.kotlin.core.json.Json
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.obj
import org.apache.commons.lang3.StringUtils

class PhilipsHueApiClient(private val vertx: Vertx) : Loggable {

	private val webClient = createWebClient()

	private fun createWebClient(): WebClient {
		val options = WebClientOptions()
				.setKeepAlive(false)
		return WebClient.create(vertx, options)
	}

	private fun get(requestUri: String): Future<JsonObject> {
		val future = Future.future<JsonObject>()
		val userName = LocalMap(vertx).getHueUsername()
		val ip = LocalMap(vertx).getHueBridgeIp()
		logger.debug("GET http://$ip/api/$userName$requestUri")
		webClient
				.get(80, ip, "/api/$userName$requestUri")
				.send { asyncResult ->
					if (asyncResult.succeeded()) {
						val bodyAsJsonObject = asyncResult.result().bodyAsJsonObject()
						future.complete(bodyAsJsonObject)
					} else {
						future.fail(asyncResult.cause())
					}
				}
		return future
	}

	private fun put(requestUri: String, body: JsonObject): Future<JsonObject> {
		val future = Future.future<JsonObject>()
		val userName = LocalMap(vertx).getHueUsername()
		val ip = LocalMap(vertx).getHueBridgeIp()
		logger.debug("PUT http://$ip/api/$userName$requestUri $body")
		webClient
				.put(80, ip, "/api/$userName$requestUri")
				.sendJson(body) { asyncResult ->
					if (asyncResult.succeeded()) {
						future.complete(JsonObject())
					} else {
						future.fail(asyncResult.cause())
					}
				}
		return future
	}

	fun authenticate(clientName: String): Future<JsonObject> {
		logger.info("Authenticating philips hue bridge")
		val future = Future.future<JsonObject>()
		val body = Json.obj(
				"devicetype" to clientName
		)
		val ip = LocalMap(vertx).getHueBridgeIp()
		logger.debug("POST http://$ip/api $body")
		webClient
				.post(80, ip, "/api")
				.sendJson(body) { asyncResult ->
					if (asyncResult.succeeded()) {
						val result = asyncResult.result()
						val resultObject = JsonObject().put("result", result.bodyAsJsonArray())
						val isSuccess = result.bodyAsString().contains("success")
						if (isSuccess) {
							logger.info("Successfully authenticated philips hue bridge $resultObject")
							val userName = resultObject
									.getJsonArray("result")
									.get<JsonObject>(0)
									.getJsonObject("success")
									.getString("username")
							LocalMap(vertx).putHueUserName(userName)
						}
						logger.info("Failed to authenticate philips hue bridge $resultObject")
						future.complete(resultObject)
					} else {
						logger.error("Failed to authenticate philips hue bridge", asyncResult.cause())
						future.fail(asyncResult.cause())
					}
				}
		return future
	}

	fun isConfigured(): Boolean {
		val hueUsername = LocalMap(vertx).getHueUsername()
		val hueBridgeIp = LocalMap(vertx).getHueBridgeIp()
		return StringUtils.isNoneEmpty(hueUsername, hueBridgeIp)
	}

	fun getLights(): Future<JsonObject> {
		return get("/lights")
	}

	fun getLight(id: String): Future<JsonObject> {
		return get("/lights/$id")
	}

	fun setLight(id: String, body: JsonObject): Future<JsonObject> {
		return put("/lights/$id/state", body)
	}
}