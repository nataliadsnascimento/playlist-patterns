package com.playlist.composite;

import com.playlist.core.Track;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Composite do padrão Composite: uma playlist que pode conter faixas e outras playlists.
 */
public class PlaylistNode implements MediaItem {
  private final String name;
  private final List<MediaItem> child = new ArrayList<>();

  /**
   * Cria uma playlist vazia.
   *
   * @param name nome da playlist. Não pode ser nulo nem em branco.
   * @throws IllegalArgumentException se o nome for nulo ou em branco.
   */
  public PlaylistNode(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("O nome não pode ser nulo nem em branco");
    }
    this.name = name;
  }

  /**
   * Adiciona um item ao final da playlist.
   *
   * @param item item a ser adicionado.
   * @return a própria playlist, permitindo encadear chamadas.
   * @throws IllegalArgumentException se o item for nulo, for a própria
   *         playlist ou contiver a própria playlist (o que criaria um ciclo).
   */
  public PlaylistNode add(MediaItem item) {
    if (item == null) {
      throw new IllegalArgumentException("Item não pode ser nulo");
    }
    if (item == this) {
      throw new IllegalArgumentException("Playlist não pode ser adicionada a ela mesma");
    }
    if (item instanceof PlaylistNode && ((PlaylistNode) item).contains(this)) {
      throw new IllegalArgumentException("Não é possível criar um ciclo");
    }
    child.add(item);
    return this;
  }

  /**
   * Remove um filho direto da playlist.
   *
   * @param item item a ser removido.
   * @return {@code true} se o item era filho direto e foi removido.
   */
  public boolean remove(MediaItem item) {
    return child.remove(item);
  }

  /**
   * Lista os filhos diretos da playlist.
   *
   * @return uma lista imutável com os filhos, na ordem de inserção.
   */
  public List<MediaItem> getChildren() {
    return Collections.unmodifiableList(child);
  }

  /**
   * Verifica se o item está em qualquer nível abaixo desta playlist.
   *
   * @param item item procurado.
   * @return {@code true} se o item for filho direto ou descendente.
   */
  public boolean contains(MediaItem item) {
    for (MediaItem mediaItem : getChildren()) {
      if (mediaItem.equals(item)) {
        return true;
      }
      if (mediaItem instanceof PlaylistNode && ((PlaylistNode) mediaItem).contains(item)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDurationSeconds() {
    int total = 0;
    for (MediaItem c : child) {
      total += c.getDurationSeconds();
    }
    return total;
  }

  @Override
  public int getTrackCount() {
    int total = 0;
    for (MediaItem c : child) {
      total += c.getTrackCount();
    }
    return total;
  }

  @Override
  public List<Track> flatten() {
    List<Track> result = new ArrayList<>();
    for (MediaItem c : child) {
      result.addAll(c.flatten());
    }
    return result;
  }
}