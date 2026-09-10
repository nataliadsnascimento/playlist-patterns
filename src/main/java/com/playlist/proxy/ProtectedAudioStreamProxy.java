package com.playlist.proxy;

import com.playlist.core.AccessDeniedException;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Proxy que controla o acesso ao {@link RemoteAudioStream}.
 *
 * <p>Ele acumula três responsabilidades clássicas do padrão: proteção
 * (bloqueia faixas premium para o plano gratuito), lazy loading (só cria o
 * objeto real quando o áudio é realmente pedido) e cache (não baixa o mesmo
 * áudio duas vezes).
 */
public class ProtectedAudioStreamProxy implements AudioStream {
  private final Track track;
  private final Subscription plan;
  private final Supplier<AudioStream> loader;
  private AudioStream realStream;
  private byte[] cachedBytes;

  /**
   * Cria o proxy com uma fábrica explícita do objeto real.
   *
   * @param track  faixa que será transmitida.
   * @param plan   plano de assinatura de quem está ouvindo.
   * @param loader fábrica que cria o stream real. Só pode ser chamada quando o
   *               áudio for realmente necessário.
   * @throws IllegalArgumentException se qualquer argumento for nulo.
   */
  public ProtectedAudioStreamProxy(Track track, Subscription plan, Supplier<AudioStream> loader) {
    if (track == null || plan == null || loader == null) {
      throw new IllegalArgumentException("Os argumentos não podem ser nulos");
    }
    this.track = track;
    this.plan = plan;
    this.loader = loader;
  }

  /**
   * Cria o proxy usando {@link RemoteAudioStream} como objeto real.
   *
   * @param track faixa que será transmitida.
   * @param plan  plano de assinatura de quem está ouvindo.
   */
  public ProtectedAudioStreamProxy(Track track, Subscription plan) {
    this(track, plan, () -> new RemoteAudioStream(track));
  }

  /**
   * Indica se o objeto real já foi criado.
   *
   * @return {@code true} apenas depois que o stream real tiver sido carregado.
   */
  public boolean isLoaded() {
    return realStream != null;
  }

  @Override
  public String getTrackId() {
    return track.id();
  }

  /**
   * Devolve os bytes do áudio, respeitando plano, carga preguiçosa e cache.
   *
   * @return uma cópia dos bytes do áudio.
   * @throws AccessDeniedException se a faixa for premium e o plano for
   *                               {@link Subscription#FREE}.
   */
  @Override
  public byte[] readBytes() {
    if (track.premium() && plan == Subscription.FREE) {
      throw new AccessDeniedException("Access denied for premium track on free plan");
    }

    if (cachedBytes == null) {
      if (realStream == null) {
        realStream = loader.get();
      }
      cachedBytes = realStream.readBytes();
    }

    return Arrays.copyOf(cachedBytes, cachedBytes.length);
  }
}