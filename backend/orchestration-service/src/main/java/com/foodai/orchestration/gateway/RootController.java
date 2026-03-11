package com.foodai.orchestration.gateway;

import com.foodai.orchestration.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles root path so GET / returns a useful response instead of an error.
 */
@RestController
@Tag(name = "Gateway", description = "API Gateway info")
public class RootController {

  @GetMapping(value = {"/", ""}, produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Gateway info", description = "Returns gateway name and API base path")
  public ResponseEntity<ApiResponse<Map<String, String>>> root() {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            ApiResponse.success(
                Map.of(
                    "service",
                    "OFD Restaurant API Gateway",
                    "apiBase",
                    "/api/v1",
                    "docs",
                    "/swagger-ui.html",
                    "health",
                    "/actuator/health")));
  }
}
