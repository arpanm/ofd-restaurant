package com.foodai.orchestration.gateway;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * API Gateway Controller.
 *
 * <p>Routes incoming requests to the appropriate backend microservice based on the URL path
 * prefix.
 *
 * <p>Routing rules:
 *
 * <ul>
 *   <li>/api/v1/user/** -> User Service (8083)
 *   <li>/api/v1/restaurant/** -> Restaurant Service (8081)
 *   <li>/api/v1/menu/** -> Menu Service (8082)
 *   <li>/api/v1/order/** -> Order Service (8084)
 *   <li>/api/v1/promotion/** -> Promotion Service (8089)
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "API Gateway", description = "Routes requests to backend microservices")
public class GatewayController {

  private final GatewayService gatewayService;

  // ============================================
  // User Service Routes (/user/**)
  // ============================================

  @RequestMapping(
      value = "/user/**",
      method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
      })
  @Operation(summary = "Route to User Service", description = "Proxies requests to the User Service")
  public Mono<ResponseEntity<String>> routeToUserService(HttpServletRequest request)
      throws IOException {
    return routeRequest(request, GatewayService.ServiceType.USER, "/user");
  }

  // ============================================
  // Restaurant Service Routes (/restaurant/**)
  // ============================================

  @RequestMapping(
      value = "/restaurant/**",
      method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
      })
  @Operation(
      summary = "Route to Restaurant Service",
      description = "Proxies requests to the Restaurant Service")
  public Mono<ResponseEntity<String>> routeToRestaurantService(HttpServletRequest request)
      throws IOException {
    return routeRequest(request, GatewayService.ServiceType.RESTAURANT, "/restaurant");
  }

  // ============================================
  // Menu Service Routes (/menu/**)
  // ============================================

  @RequestMapping(
      value = "/menu/**",
      method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
      })
  @Operation(summary = "Route to Menu Service", description = "Proxies requests to the Menu Service")
  public Mono<ResponseEntity<String>> routeToMenuService(HttpServletRequest request)
      throws IOException {
    return routeRequest(request, GatewayService.ServiceType.MENU, "/menu");
  }

  // ============================================
  // Order Service Routes (/order/**)
  // ============================================

  @RequestMapping(
      value = "/order/**",
      method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
      })
  @Operation(summary = "Route to Order Service", description = "Proxies requests to the Order Service")
  public Mono<ResponseEntity<String>> routeToOrderService(HttpServletRequest request)
      throws IOException {
    return routeRequest(request, GatewayService.ServiceType.ORDER, "/order");
  }

  // ============================================
  // Promotion Service Routes (/promotion/**)
  // ============================================

  @RequestMapping(
      value = "/promotion/**",
      method = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
      })
  @Operation(
      summary = "Route to Promotion Service",
      description = "Proxies requests to the Promotion Service")
  public Mono<ResponseEntity<String>> routeToPromotionService(HttpServletRequest request)
      throws IOException {
    return routeRequest(request, GatewayService.ServiceType.PROMOTION, "/promotion");
  }

  // ============================================
  // Helper Methods
  // ============================================

  private Mono<ResponseEntity<String>> routeRequest(
      HttpServletRequest request, GatewayService.ServiceType service, String prefix)
      throws IOException {

    String requestUri = request.getRequestURI();
    String path = extractPath(requestUri, prefix);
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    HttpHeaders headers = extractHeaders(request);
    String body = extractBody(request);
    Map<String, String> queryParams = extractQueryParams(request);

    log.debug(
        "Routing request: {} {} -> {} service, path={}", method, requestUri, service, path);

    return gatewayService.routeRequest(service, method, path, headers, body, queryParams);
  }

  private String extractPath(String requestUri, String prefix) {
    // Remove /api/v1/prefix from the path
    String fullPrefix = "/api/v1" + prefix;
    if (requestUri.startsWith(fullPrefix)) {
      String remaining = requestUri.substring(fullPrefix.length());
      // Ensure path starts with /api/v1 for the backend service
      return "/api/v1" + (remaining.isEmpty() ? "" : remaining);
    }
    return requestUri;
  }

  private HttpHeaders extractHeaders(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.add(headerName, request.getHeader(headerName));
    }
    return headers;
  }

  private String extractBody(HttpServletRequest request) throws IOException {
    if ("GET".equalsIgnoreCase(request.getMethod())
        || "DELETE".equalsIgnoreCase(request.getMethod())) {
      return null;
    }
    StringBuilder body = new StringBuilder();
    try (BufferedReader reader = request.getReader()) {
      String line;
      while ((line = reader.readLine()) != null) {
        body.append(line);
      }
    }
    return body.length() > 0 ? body.toString() : null;
  }

  private Map<String, String> extractQueryParams(HttpServletRequest request) {
    Map<String, String> queryParams = new HashMap<>();
    request.getParameterMap().forEach((key, values) -> {
      if (values != null && values.length > 0) {
        queryParams.put(key, values[0]);
      }
    });
    return queryParams;
  }
}

