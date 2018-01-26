package alphabetapeter.color

import alphabetapeter.util.Loggable
import de.androidpit.colorthief.ColorThief
import de.androidpit.colorthief.MMCQ
import io.vertx.core.shareddata.Shareable
import java.net.URL
import javax.imageio.ImageIO


class ColorPaletteBuilder : Loggable {

	data class RGB(val r: Int, val g: Int, val b: Int)
	data class Palette(val hex: String, val rgb: RGB)
	data class HueColor(val hex: String, val rgb: RGB) : Shareable

	fun buildPalettesFromUrl(url: String): List<Palette> {
		logger.info("Building color palette for url $url")
		// https://github.com/SvenWoltmann/color-thief-java
		val img = ImageIO.read(URL(url))
		val result = ColorThief.getColorMap(img, 5)
		return result.vboxes.map { parseVbox(it) }
	}

	private fun parseVbox(vbox: MMCQ.VBox): Palette {
		val rgb = vbox.avg(false)
		// Create color String representations
		val rgbHexString = ColorConverter.convertRgbToHex(rgb[0], rgb[1], rgb[2])
		return Palette(rgbHexString, RGB(rgb[0], rgb[1], rgb[2]))
	}

	fun chooseHueColor(colors: List<Palette>): Palette? {
		return colors
				.map { ColorJudge.isColorful(it) }
				.filter { it.isColorful }
				.sortedBy { it.highestDeviation }
				.map { it.color }
				.lastOrNull()
	}



}