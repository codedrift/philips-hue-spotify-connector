package alphabetapeter.util

import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory

interface Loggable {

	val logger: Logger
		get() = LoggerFactory.getLogger(this::class.java)
}