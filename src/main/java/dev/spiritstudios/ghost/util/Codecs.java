package dev.spiritstudios.ghost.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

public final class Codecs {
	public static final Codec<Instant> INSTANT = Codec.STRING.comapFlatMap(
		s -> {
			try {
				TemporalAccessor accessor = DateTimeFormatter.ISO_INSTANT.parse(s);
				return DataResult.success(Instant.from(accessor));
			} catch (DateTimeParseException e) {
				return DataResult.error(() -> "Invalid instant format");
			}
		},
		DateTimeFormatter.ISO_INSTANT::format
	);
}
