package alphabetapeter.color

import alphabetapeter.model.ColorSet
import alphabetapeter.model.RGB
import alphabetapeter.model.RatedColor
import alphabetapeter.util.Loggable
import de.androidpit.colorthief.ColorThief
import de.androidpit.colorthief.MMCQ
import java.net.URL
import javax.imageio.ImageIO


class ColorPaletteBuilder : Loggable {


	fun buildPalettesFromUrl(url: String): List<ColorSet> {
		logger.info("Building color palette for url $url")
		// https://github.com/SvenWoltmann/color-thief-java
		val img = ImageIO.read(URL(url))
		val result = ColorThief.getColorMap(img, 5)
		return result.vboxes.map { parseVbox(it) }
	}

	private fun parseVbox(vbox: MMCQ.VBox): ColorSet {
		val rgb = vbox.avg(false)
		// Create color String representations
		val rgbHexString = ColorConverter.convertRgbToHex(rgb[0], rgb[1], rgb[2])
		return ColorSet(rgbHexString, RGB(rgb[0], rgb[1], rgb[2]))
	}

	fun chooseHueColor(colors: List<ColorSet>): List<RatedColor> {
		return colors
				.map { ColorJudge.isColorful(it) }
				.filter { it.colorful }
				.sortedBy { it.highestDeviation }
	}



}