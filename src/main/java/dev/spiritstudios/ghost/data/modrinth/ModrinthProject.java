package dev.spiritstudios.ghost.data.modrinth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.spiritstudios.ghost.util.Codecs;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ModrinthProject(
        String slug,
        String title,
        String description,
        List<String> categories,
        String body,
        Optional<String> iconUrl,
        int followers,
        Optional<Integer> color
) {
    public static final Codec<ModrinthProject> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("slug").forGetter(ModrinthProject::slug),
            Codec.STRING.fieldOf("title").forGetter(ModrinthProject::title),
            Codec.STRING.fieldOf("description").forGetter(ModrinthProject::description),
            Codec.STRING.listOf().fieldOf("categories").forGetter(ModrinthProject::categories),
            Codec.STRING.fieldOf("body").forGetter(ModrinthProject::body),
            Codec.STRING.optionalFieldOf("icon_url").forGetter(ModrinthProject::iconUrl),
            Codec.INT.fieldOf("followers").forGetter(ModrinthProject::followers),
            Codec.INT.optionalFieldOf("color").forGetter(ModrinthProject::color)
    ).apply(instance, ModrinthProject::new));

    public record License(
            String id,
            String name,
            Optional<String> url
    ) {
        public static final Codec<License> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(License::id),
                Codec.STRING.fieldOf("name").forGetter(License::name),
                Codec.STRING.optionalFieldOf("url").forGetter(License::url)
        ).apply(instance, License::new));
    }

    public record GalleryImage(
            String url,
            boolean featured,
            Optional<String> title,
            Optional<String> description,
            Instant created,
            int ordering
    ) {
        public static final Codec<GalleryImage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("url").forGetter(GalleryImage::url),
                Codec.BOOL.fieldOf("featured").forGetter(GalleryImage::featured),
                Codec.STRING.optionalFieldOf("title").forGetter(GalleryImage::title),
                Codec.STRING.optionalFieldOf("description").forGetter(GalleryImage::description),
                Codecs.INSTANT.fieldOf("created").forGetter(GalleryImage::created),
                Codec.INT.fieldOf("ordering").forGetter(GalleryImage::ordering)
        ).apply(instance, GalleryImage::new));
    }
}
