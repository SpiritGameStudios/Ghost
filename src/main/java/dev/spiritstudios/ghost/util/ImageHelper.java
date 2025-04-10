package dev.spiritstudios.ghost.util;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ImageHelper {
	private ImageHelper() {
		Util.utilError();
	}

	public static int getCommonColor(BufferedImage image) {
		image = toBuffered(image.getScaledInstance(32, 32, Image.SCALE_AREA_AVERAGING));

		Map<Integer, Integer> colorMap = new Int2IntArrayMap();

		for (int x = 0; x < 32; x++) {
			for (int y = 0; y < 32; y++) {
				int rgb = image.getRGB(x, y);
				if (isGray(rgb)) continue;

				int counter = colorMap.getOrDefault(rgb, 0);
				colorMap.put(rgb, ++counter);
			}
		}

		List<Map.Entry<Integer, Integer>> entries = new LinkedList<>(colorMap.entrySet());
		if (entries.isEmpty()) return 0xFF000000;
		entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
		return entries.getFirst().getKey();
	}

	public static BufferedImage toBuffered(Image image) {
		if (image instanceof BufferedImage bufferedImage) return bufferedImage;
		BufferedImage bufferedImage = new BufferedImage(
			image.getWidth(null), image.getHeight(null),
			BufferedImage.TYPE_INT_ARGB
		);

		Graphics2D graphics = bufferedImage.createGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();

		return bufferedImage;
	}

	private static boolean isGray(int rgb) {
		int red = (rgb >> 16) & 0xff;
		int green = (rgb >> 8) & 0xff;
		int blue = (rgb) & 0xff;

		int rg = red - green;
		int rb = red - blue;

		return (rg <= 10 && rg >= -10) || (rb <= 10 && rb >= -10);
	}
}
