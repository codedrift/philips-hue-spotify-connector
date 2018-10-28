package alphabetapeter

import alphabetapeter.util.Env
import alphabetapeter.verticle.ServiceVerticle
import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("alphabetapeter.Main")

fun main(args: Array<String>) {
	logger.info("Server starting")

	val vertx = Vertx.vertx()
	Env(vertx).mapEnvToLocalMap()
	vertx.deployVerticle(ServiceVerticle()) { res ->
		if (res.succeeded()) {
			logger.info("Deployment succeeded. Id is: ${res.result()}")
		} else {
			logger.info("Deployment failed! ${res.cause()}")
		}
	}
}
