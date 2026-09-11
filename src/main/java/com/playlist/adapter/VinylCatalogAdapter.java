package com.playlist.adapter;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter que converte os registros do {@link LegacyVinylCatalog} para {@link Track}.
 */
public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyCatalog;

  /**
   * Cria o adapter em cima do sistema legado.
   *
   * @param legacyCatalog catálogo legado a ser adaptado. Não pode ser nulo.
   * @throws IllegalArgumentException se o catálogo for nulo.
   */
  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null) {
      throw new IllegalArgumentException("Legacy catalog cannot be null");
    }
    this.legacyCatalog = legacyCatalog;
  }

  @Override
  public List<Track> findAll() {
    List<Track> validTracks = new ArrayList<>();
    for (String record : legacyCatalog.fetchAllRecords()) {
      Optional<Track> track = parseRecord(record);
      track.ifPresent(validTracks::add);
    }
    return validTracks;
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.isBlank()) {
      return Optional.empty();
    }
    String record = legacyCatalog.findRecordByCatalogNumber(id);
    if (record == null) {
      return Optional.empty();
    }
    return parseRecord(record);
  }

  private Optional<Track> parseRecord(String record) {
    if (record == null) {
      return Optional.empty();
    }
    String[] parts = record.split("\\|", -1);
    if (parts.length != 5) {
      return Optional.empty();
    }

    String id = parts[0].trim();
    String rawTitle = parts[1];
    String rawArtist = parts[2];
    String rawDuration = parts[3];

    if (id.isEmpty()) {
      return Optional.empty();
    }

    String title = formatTitle(rawTitle);
    if (title.isEmpty()) {
      return Optional.empty();
    }

    String artist = formatArtist(rawArtist);

    int durationSeconds;
    try {
      int durationMs = Integer.parseInt(rawDuration.trim());
      if (durationMs < 0) {
        return Optional.empty();
      }
      durationSeconds = durationMs / 1000;
    } catch (NumberFormatException e) {
      return Optional.empty();
    }

    String rawPremium = parts[4].trim();
    boolean premium;
    if (rawPremium.equalsIgnoreCase("Y")) {
      premium = true;
    } else if (rawPremium.equalsIgnoreCase("N")) {
      premium = false;
    } else {
      return Optional.empty();
    }

    return Optional.of(new Track(id, title, artist, durationSeconds, premium));
  }

  private String formatTitle(String rawTitle) {
    String cleaned = rawTitle.trim().replaceAll("\\s+", " ");
    if (cleaned.isEmpty()) {
      return "";
    }
    String[] words = cleaned.toLowerCase().split(" ");
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
      if (!word.isEmpty()) {
        if (sb.length() > 0) {
          sb.append(" ");
        }
        sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
      }
    }
    return sb.toString();
  }

  private String formatArtist(String rawArtist) {
    String cleaned = rawArtist.trim().replaceAll("\\s+", " ");
    String[] nameParts = cleaned.split(",");
    if (nameParts.length == 2) {
      String lastName = formatTitle(nameParts[0]);
      String firstName = formatTitle(nameParts[1]);
      return firstName + " " + lastName;
    }
    return formatTitle(cleaned);
  }
}