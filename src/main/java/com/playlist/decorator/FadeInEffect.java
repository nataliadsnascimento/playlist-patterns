package com.playlist.decorator;

import java.util.Locale;

/**
 * Efeito que aplica uma rampa linear de volume nas primeiras amostras.
 */
public final class FadeInEffect extends AudioEffect {

  private final int sampleCount;

  /**
   * Cria o efeito de fade in.
   *
   * @param wrapped áudio decorado.
   * @param sampleCount quantidade de amostras usadas na rampa.
   */
  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    this.sampleCount = sampleCount;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "fadeIn(%d)", sampleCount);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] modified = new double[original.length];
    System.arraycopy(original, 0, modified, 0, original.length);

    int limit = Math.min(sampleCount, modified.length);
    if (sampleCount > 0) {
      for (int i = 0; i < limit; i++) {
        double multiplier = (double) i / sampleCount;
        modified[i] *= multiplier;
      }
    }
    return modified;
  }
}