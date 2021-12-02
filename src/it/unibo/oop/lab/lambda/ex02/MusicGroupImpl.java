package it.unibo.oop.lab.lambda.ex02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        final List<String> l = new ArrayList<>();
        this.songs.forEach(s -> l.add(s.getSongName()));
        Collections.sort(l);
        return l.stream();
    }

    @Override
    public Stream<String> albumNames() {
        final List<String> l = new ArrayList<>();
        this.albums.forEach((n, y) -> l.add(n));
        return l.stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final List<String> l = new ArrayList<>();
        this.albums.forEach((n, y) -> {
        	if (y.equals(year)) {
        		l.add(n);
        	}
        });
        return l.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
        		.filter(s -> s.getAlbumName().isPresent() ? s.getAlbumName().get().equals(albumName) : false)
        		.count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
        		.filter(s -> s.getAlbumName().isEmpty() ? true : false)
        		.count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
        		.filter(s -> s.getAlbumName().isPresent() ? s.getAlbumName().get().equals(albumName) : false)
        		.mapToDouble(s -> s.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(
                this.songs.stream()
                .max((x, y) -> x.getDuration() > y.getDuration() ? 1 : -1)
                .get()
                .getSongName());
   }

    @Override
    public Optional<String> longestAlbum() {
        final Map<Optional<String>, Double> m = new HashMap<>();
        this.albums.forEach((x, y) -> {
            final OptionalDouble duration = this.songs.stream()
            .filter(s -> s.getAlbumName().isPresent() ? s.getAlbumName().get().equals(x) : false)
            .mapToDouble(s -> s.getDuration())
            .reduce((a, b) -> a + b);
            m.put(Optional.of(x), duration.getAsDouble());
        });
        return m.entrySet().stream()
                .max((x, y) -> x.getValue().compareTo(y.getValue()))
                .get().getKey();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
