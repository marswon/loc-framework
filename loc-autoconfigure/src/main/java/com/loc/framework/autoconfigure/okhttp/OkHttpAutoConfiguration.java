package com.loc.framework.autoconfigure.okhttp;

import com.loc.framework.autoconfigure.okhttp.OkHttpClientProperties.Connection;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 2018/1/4.
 */
@ConditionalOnClass(OkHttpClient.class)
@Configuration
@EnableConfigurationProperties(OkHttpClientProperties.class)
@Slf4j
public class OkHttpAutoConfiguration {

  private final HttpLoggingInterceptor httpLoggingInterceptor;

  public OkHttpAutoConfiguration(HttpLoggingInterceptor httpLoggingInterceptor) {
    this.httpLoggingInterceptor = httpLoggingInterceptor;
  }

  private OkHttpClient.Builder createBuilder(OkHttpClientProperties okHttpClientProperties,
      ConnectionPool connectionPool) {
    return new okhttp3.OkHttpClient.Builder()
        .readTimeout(okHttpClientProperties.getReadTimeout(), TimeUnit.MILLISECONDS)
        .connectTimeout(okHttpClientProperties.getConnectTimeout(), TimeUnit.MILLISECONDS)
        .writeTimeout(okHttpClientProperties.getWriteTimeout(), TimeUnit.MILLISECONDS)
        .connectionPool(connectionPool).followRedirects(okHttpClientProperties.isFollowRedirects())
        .retryOnConnectionFailure(okHttpClientProperties.isRetryOnConnectionFailure())
        .addInterceptor(httpLoggingInterceptor);
  }


  @Bean
  @ConditionalOnMissingBean
  public ConnectionPool connectionPool(OkHttpClientProperties okHttpClientProperties) {
    Connection connection = okHttpClientProperties.getConnection();
    return new ConnectionPool(connection.getMaxIdleConnections(),
        connection.getKeepAliveDuration(), TimeUnit.MILLISECONDS);
  }

  @Bean
  @ConditionalOnMissingBean
  public OkHttpClient okHttpClient(OkHttpClientProperties okHttpClientProperties,
      ConnectionPool connectionPool) {
    return createBuilder(okHttpClientProperties, connectionPool).build();
  }
}
