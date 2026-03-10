package com.foodai.orchestration.gateway;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Gateway Service for proxying requests to backend microservices.
 *
 * <p>Handles request routing, header forwarding, and response transformation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayService {

  private final WebClient userServiceClient;
  private final WebClient restaurantServiceClient;
  private final WebClient menuServiceClient;
  private final WebClient orderServiceClient;
  private final WebClient promotionServiceClient;

  /** Supported backend services. */
  public enum ServiceType {
    USER,
    RESTAURANT,
    MENU,
    ORDER,
    PROMOTION
  }

  /**
   * Route a request to the appropriate backend service.
   *
   * @param service The target service
   * @param method HTTP method
   * @param path The path after the service prefix
   * @param headers Request headers
   * @param body Request body (can be null)
   * @param queryParams Query parameters
   * @return Response from the backend service
   */
  public Mono<ResponseEntity<String>> routeRequest(
      ServiceType service,
      HttpMethod method,
      String path,
      HttpHeaders headers,
      String body,
      Map<String, String> queryParams) {

    WebClient client = getClientForService(service);
    String requestId = headers.getFirst("X-Request-ID");

    log.info(
        "Gateway routing request: {} {} -> {} service, requestId={}",
        method,
        path,
        service,
        requestId);

    WebClient.RequestBodySpec requestSpec =
        client
            .method(method)
            .uri(
                uriBuilder -> {
                  uriBuilder.path(path);
                  if (queryParams != null) {
                    queryParams.forEach(uriBuilder::queryParam);
                  }
                  return uriBuilder.build();
                })
            .headers(h -> copyHeaders(headers, h));

    WebClient.RequestHeadersSpec<?> finalSpec;
    if (body != null
        && !body.isEmpty()
        && (method == HttpMethod.POST
            || method == HttpMethod.PUT
            || method == HttpMethod.PATCH)) {
      finalSpec = requestSpec.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(body));
    } else {
      finalSpec = requestSpec;
    }

    return finalSpec
        .retrieve()
        .toEntity(String.class)
        .doOnSuccess(
            response ->
                log.info(
                    "Gateway response: {} {} -> {} status={}, requestId={}",
                    method,
                    path,
                    service,
                    response.getStatusCode(),
                    requestId))
        .doOnError(
            error ->
                log.error(
                    "Gateway error: {} {} -> {} error={}, requestId={}",
                    method,
                    path,
                    service,
                    error.getMessage(),
                    requestId))
        .onErrorResume(
            WebClientResponseException.class,
            ex ->
                Mono.just(
                    ResponseEntity.status(ex.getStatusCode())
                        .headers(copyResponseHeaders(ex.getHeaders()))
                        .body(ex.getResponseBodyAsString())));
  }

  private WebClient getClientForService(ServiceType service) {
    switch (service) {
      case USER:
        return userServiceClient;
      case RESTAURANT:
        return restaurantServiceClient;
      case MENU:
        return menuServiceClient;
      case ORDER:
        return orderServiceClient;
      case PROMOTION:
        return promotionServiceClient;
      default:
        throw new IllegalArgumentException("Unknown service: " + service);
    }
  }

  private void copyHeaders(HttpHeaders source, HttpHeaders target) {
    // Copy relevant headers from the original request
    if (source.containsKey(HttpHeaders.AUTHORIZATION)) {
      target.set(HttpHeaders.AUTHORIZATION, source.getFirst(HttpHeaders.AUTHORIZATION));
    }
    if (source.containsKey(HttpHeaders.CONTENT_TYPE)) {
      target.set(HttpHeaders.CONTENT_TYPE, source.getFirst(HttpHeaders.CONTENT_TYPE));
    }
    if (source.containsKey(HttpHeaders.ACCEPT)) {
      target.set(HttpHeaders.ACCEPT, source.getFirst(HttpHeaders.ACCEPT));
    }
    if (source.containsKey("X-Request-ID")) {
      target.set("X-Request-ID", source.getFirst("X-Request-ID"));
    }
    if (source.containsKey("X-User-ID")) {
      target.set("X-User-ID", source.getFirst("X-User-ID"));
    }
    if (source.containsKey("X-Restaurant-ID")) {
      target.set("X-Restaurant-ID", source.getFirst("X-Restaurant-ID"));
    }
  }

  private HttpHeaders copyResponseHeaders(HttpHeaders source) {
    HttpHeaders headers = new HttpHeaders();
    if (source.containsKey(HttpHeaders.CONTENT_TYPE)) {
      headers.set(HttpHeaders.CONTENT_TYPE, source.getFirst(HttpHeaders.CONTENT_TYPE));
    }
    return headers;
  }
}

