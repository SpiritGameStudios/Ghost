package dev.spiritstudios.ghost.util

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap
import java.awt.Image
import java.awt.image.BufferedImage

fun Image.commonColor(): Int {
	val image = toBuffered(this.getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING))
	val colorMap = Int2IntArrayMap(32 * 32)

	for (x in 0..31) {
		for (y in 0..31) {
			val rgb = image.getRGB(x, y)
			if (isGray(rgb)) continue

			var counter = colorMap.getOrDefault(rgb, 0)
			colorMap[rgb] = ++counter
		}
	}

	val map = colorMap.int2IntEntrySet().sortedWith { o1, o2 ->
		o2!!.intValue.compareTo(o1!!.intValue)
	}

	return if (map.isEmpty()) 0xFF000000.toInt() else map.first().intKey
}

fun toBuffered(image: Image): BufferedImage {
	if (image is BufferedImage) return image
	val bufferedImage = BufferedImage(
		image.getWidth(null), image.getHeight(null),
		BufferedImage.TYPE_INT_ARGB
	)

	val graphics = bufferedImage.createGraphics()
	graphics.drawImage(image, 0, 0, null)
	graphics.dispose()

	return bufferedImage
}

private fun isGray(rgb: Int): Boolean {
	val red = (rgb shr 16) and 0xff
	val green = (rgb shr 8) and 0xff
	val blue = (rgb) and 0xff

	val rg = red - green
	val rb = red - blue

	return (rg <= 10 && rg >= -10) || (rb <= 10 && rb >= -10)
}
