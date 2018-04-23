package alphabetapeter.color

import alphabetapeter.model.RGB
import com.philips.lighting.hue.sdk.utilities.PHUtilities

class ColorConverter {

	companion object {
		fun convertIntToHex(int: Int): String {
			return String.format("#%06X", 0xFFFFFF and int)
		}

		fun convertRgbToHex(r: Int, g: Int, b: Int): String {
			var rgbHex = Integer.toHexString(r shl 16 or (g shl 8) or b)

			// Left-pad with 0s
			val length = rgbHex.length
			if (length < 6) {
				rgbHex = "00000".substring(0, 6 - length) + rgbHex
			}

			return "#$rgbHex"
		}

		fun convertRgbToXy(color: RGB, modelId: String): FloatArray {
			return PHUtilities.calculateXYFromRGB(color.r,color.g,color.b, modelId)
		}


	}
}