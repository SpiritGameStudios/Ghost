package dev.spiritstudios.ghost.util;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ImageHelper {
    private ImageHelper() {
        Util.utilError();
    }

    public static int getCommonColor(BufferedImage image) {
        Map<Integer, Integer> colorMap = new Int2IntOpenHashMap();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int counter = colorMap.get(rgb);
                colorMap.put(rgb, ++counter);
            }
        }

        List<Map.Entry<Integer, Integer>> entries = new LinkedList<>(colorMap.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return entries.getFirst().getKey();
    }
}
