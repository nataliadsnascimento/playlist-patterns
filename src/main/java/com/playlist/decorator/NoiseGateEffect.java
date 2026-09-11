package com.playlist.decorator;

import java.util.Locale;

/**
 * Efeito que zera amostras cujo valor absoluto fica abaixo de um limiar.
 */
public final class NoiseGateEffect extends AudioEffect {

  private final double threshold;

  /**
   * Cria o efeito de noise gate.
   *
   * @param wrapped áudio decorado.
   * @param threshold limiar de corte.
   */
  public NoiseGateEffect(AudioTrack wrapped, double threshold) {
    super(wrapped);
    this.threshold = threshold;
  }

  @Override
  protected String describe() {
    return String.format(Locale.ROOT, "noiseGate(%.2f)", threshold);
  }

  @Override
  public double[] getSamples() {
    double[] original = wrapped.getSamples();
    double[] modified = new double[original.length];
    for (int i = 0; i < original.length; i++) {
      if (Math.abs(original[i]) < threshold) {
        modified[i] = 0.0;
      } else {
        modified[i] = original[i];
      }
    }
    return modified;
  }
}