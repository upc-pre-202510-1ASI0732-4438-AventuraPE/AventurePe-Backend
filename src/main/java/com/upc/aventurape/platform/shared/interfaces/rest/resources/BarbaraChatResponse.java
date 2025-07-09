package com.upc.aventurape.platform.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record BarbaraChatResponse(
    String response,
    String status,
    LocalDateTime timestamp
) {
} 