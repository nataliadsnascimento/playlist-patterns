package com.playlist.decorator;

import java.util.Locale;

/**
 * Efeito que multiplica o volume das amostras, com corte em {@code [-1.0, 1.0]}.
 */
public final class VolumeEffect extends AudioEffect {

  private final double factor;

  /**
   * Cria o efeito de volume.
   *
   * @param wrapped áudio decorado.
   * @param factor fator multiplicador do volume.
   */
  public VolumeEffect(AudioTrack wrapped, double factor) {
    super(wrapped);
    this.factor = factor;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "volume(%.1f)", factor);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] modified = new double[original.length];
    for (int i = 0; i < original.length; i++) {
      double val = original[i] * factor;
      if (val > 1.0) {
        val = 1.0;
      } else if (val < -1.0) {
        val = -1.0;
      }
      modified[i] = val;
    }
    return modified;
  }
}
