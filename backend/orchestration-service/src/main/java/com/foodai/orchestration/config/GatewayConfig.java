package com.foodai.orchestration.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * API Gateway Configuration.
 *
 * <p>Configures WebClient instances for routing requests to backend microservices.
 * Acts as the central configuration for the API Gateway functionality.
 */
@Configuration
public class GatewayConfig {

  @Value("${gateway.services.user-service.url:http://localhost:8083}")
  private String userServiceUrl;

  @Value("${gateway.services.restaurant-service.url:http://localhost:8081}")
  private String restaurantServiceUrl;

  @Value("${gateway.services.menu-service.url:http://localhost:8082}")
  private String menuServiceUrl;

  @Value("${gateway.services.order-service.url:http://localhost:8084}")
  private String orderServiceUrl;

  @Value("${gateway.services.promotion-service.url:http://localhost:8089}")
  private String promotionServiceUrl;

  @Value("${gateway.timeout.connect:5000}")
  private int connectTimeout;

  @Value("${gateway.timeout.read:30000}")
  private int readTimeout;

  @Value("${gateway.timeout.write:30000}")
  private int writeTimeout;

  private HttpClient createHttpClient() {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        .responseTimeout(Duration.ofMillis(readTimeout))
        .doOnConnected(
            conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS)));
  }

  private WebClient.Builder createWebClientBuilder() {
    // Increase buffer size for large responses
    ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(
                configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB
            .build();

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
        .exchangeStrategies(strategies);
  }

  @Bean
  public WebClient userServiceClient() {
    return createWebClientBuilder().baseUrl(userServiceUrl).build();
  }

  @Bean
  public WebClient restaurantServiceClient() {
    return createWebClientBuilder().baseUrl(restaurantServiceUrl).build();
  }

  @Bean
  public WebClient menuServiceClient() {
    return createWebClientBuilder().baseUrl(menuServiceUrl).build();
  }

  @Bean
  public WebClient orderServiceClient() {
    return createWebClientBuilder().baseUrl(orderServiceUrl).build();
  }

  @Bean
  public WebClient promotionServiceClient() {
    return createWebClientBuilder().baseUrl(promotionServiceUrl).build();
  }

  // Getters for URL properties
  public String getUserServiceUrl() {
    return userServiceUrl;
  }

  public String getRestaurantServiceUrl() {
    return restaurantServiceUrl;
  }

  public String getMenuServiceUrl() {
    return menuServiceUrl;
  }

  public String getOrderServiceUrl() {
    return orderServiceUrl;
  }

  public String getPromotionServiceUrl() {
    return promotionServiceUrl;
  }
}

