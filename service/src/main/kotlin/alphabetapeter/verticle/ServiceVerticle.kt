package alphabetapeter.verticle

import alphabetapeter.clients.PhilipsHueApiClient
import alphabetapeter.clients.SpotifyApiClient
import alphabetapeter.events.ColorChangeEventConsumer
import alphabetapeter.events.SpotifyStatusUpdateTickEventConsumer
import alphabetapeter.events.SpotifyTokenRefreshEventConsumer
import alphabetapeter.router.handler.ConfigGetHandler
import alphabetapeter.router.handler.LightMatchingSettingUpdateHandler
import alphabetapeter.router.handler.philipshue.PhilipsHueCreateClientHandler
import alphabetapeter.router.handler.philipshue.PhilipsHueStatusGetHandler
import alphabetapeter.router.handler.philipshue.PhilipsHueStatusShuffleHandler
import alphabetapeter.router.handler.spotify.SpotifyAuthHandler
import alphabetapeter.router.handler.spotify.SpotifyPlayerStatusGetHandler
import alphabetapeter.timer.SpotifyPlayerStatusUpdateTimer
import alphabetapeter.util.Loggable
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.CorsHandler

class ServiceVerticle : AbstractVerticle(), Loggable {

	override fun start(startFuture: Future<Void>) {

		val router = Router.router(vertx)
		val spotifyApiClient = SpotifyApiClient(vertx)
		val philipsHueApiClient = PhilipsHueApiClient(vertx)

		configureRouter(router)

		createRoutes(router, philipsHueApiClient, spotifyApiClient)

		createEventConsumers(philipsHueApiClient, spotifyApiClient)

		SpotifyPlayerStatusUpdateTimer(vertx).create()

		startHttpServer(router, startFuture)
	}

	private fun startHttpServer(router: Router, startFuture: Future<Void>) {
		this.vertx
				.createHttpServer()
				.requestHandler(router::accept)
				.listen(8081) { asyncResult ->
					if (asyncResult.succeeded()) {
						logger.info("Server listening on port 8081")
						startFuture.complete()
					} else {
						logger.info("Failed to create http server")
						startFuture.fail(asyncResult.cause())
					}
				}
	}

	private fun createEventConsumers(philipsHueApiClient: PhilipsHueApiClient, spotifyApiClient: SpotifyApiClient) {
		ColorChangeEventConsumer(vertx, philipsHueApiClient).create()
		SpotifyStatusUpdateTickEventConsumer(vertx, spotifyApiClient).create()
		SpotifyTokenRefreshEventConsumer(vertx, spotifyApiClient).create()
	}

	private fun createRoutes(router: Router, philipsHueApiClient: PhilipsHueApiClient, spotifyApiClient: SpotifyApiClient) {

		val philipsHueRouter = Router.router(vertx)

		philipsHueRouter.get("/status").handler(PhilipsHueStatusGetHandler(philipsHueApiClient))
		philipsHueRouter.post("/client").handler(PhilipsHueCreateClientHandler(vertx, philipsHueApiClient))
		philipsHueRouter.post("/matching").handler(LightMatchingSettingUpdateHandler(vertx))
		philipsHueRouter.post("/shufflelights").handler(PhilipsHueStatusShuffleHandler(vertx))
		router.mountSubRouter("/philipshue", philipsHueRouter)

		val spotifyRouter = Router.router(vertx)

		spotifyRouter.get("/status").handler(SpotifyPlayerStatusGetHandler(vertx))
		spotifyRouter.post("/auth").handler(SpotifyAuthHandler(spotifyApiClient))
		router.mountSubRouter("/spotify", spotifyRouter)

		router.get("/config").handler(ConfigGetHandler(vertx))
	}

	private fun configureRouter(router: Router) {
//		router.route().handler({
//			logger.info("Requested ${it.request().method()} ${it.request().absoluteURI()}")
//			it.next()
//		})

		val corsHandler = CorsHandler
				.create("*")
				.allowedHeader("Access-Control-Allow-Origin")

		router.route().handler(corsHandler)
	}
}