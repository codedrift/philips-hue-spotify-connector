package alphabetapeter.util

import io.vertx.core.Vertx

class Env(private val vertx: Vertx): Loggable {

	private val envPrefix = "SERVER_ENV_"

	fun mapEnvToLocalMap() {
		val localMap = LocalMap(vertx)
		val serverEnv = System.getenv()
				.filter { it.key.startsWith(envPrefix) }
				.map{
					val keyWithoutPrefix = it.key.removePrefix(envPrefix)
					val value = it.value

					when {
						isBoolean(value) -> localMap.putBoolean(keyWithoutPrefix, value.toBoolean())
						else -> localMap.putString(keyWithoutPrefix, value)
					}

					"$keyWithoutPrefix=$value"
				}
		logger.info("SERVER ENV \n$serverEnv")
	}

	private fun isBoolean(value: String?): Boolean{
		return value != null && (value == "true" || value == "false")
	}
}