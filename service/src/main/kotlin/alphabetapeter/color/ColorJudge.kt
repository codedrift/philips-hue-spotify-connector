package alphabetapeter.color

class ColorJudge {

	data class Judging(
			val color: ColorPaletteBuilder.Palette,
			val highestDeviation: Int,
			val deviationAverage: Double,
			val isColorful: Boolean
	)

	companion object {

		/**
		 * Check if color is colorful. ignore black and white extremes
		 */
		fun isColorful(color: ColorPaletteBuilder.Palette): Judging {
			var colorful = true

			val red = color.rgb.r
			val green = color.rgb.g
			val blue = color.rgb.b
			val average = (red + green + blue) / 3

			if (average < 50) {
				//color is too dark
				colorful = false
			}
			if (average > 220) {
				// color is too bright
				colorful = false
			}
			val redDeviation = Math.abs(red / (average * 1f).toDouble() - 1)
			val greenDeviation = Math.abs(green / (average * 1f).toDouble() - 1)
			val blueDeviation = Math.abs(blue / (average * 1f).toDouble() - 1)
			val deviations = listOf(redDeviation, greenDeviation, blueDeviation).sortedBy { it }

			val deviationAverage = deviations.last() - deviations.first()

			val highestDeviation = listOf(red - blue, red - green, blue - green)
					.map(Math::abs)
					.sortedBy { it }
					.last()

			if (highestDeviation < 30) {
				colorful = false
			}

//			if ((deviationAverage * 100) < 5) {
//				colorful = false
//			}

			return Judging(color, highestDeviation, Math.abs(deviationAverage * 100), colorful)
		}
	}
}