package io.razem.influxdbclient

import org.asynchttpclient.DefaultAsyncHttpClientConfig

class HttpConfig {
  private var builder = new DefaultAsyncHttpClientConfig.Builder

  def setConnectTimeout(timeout: Int): HttpConfig = {
    builder = builder.setConnectTimeout(timeout)
    this
  }

  def setRequestTimeout(timeout: Int): HttpConfig = {
    builder = builder.setRequestTimeout(timeout)
    this
  }

  def setAcceptAnyCertificate(acceptAnyCertificate: Boolean): HttpConfig = {
    builder = builder.setUseInsecureTrustManager(acceptAnyCertificate)
    this
  }

  protected[influxdbclient] def build(): DefaultAsyncHttpClientConfig = builder.build()
}
