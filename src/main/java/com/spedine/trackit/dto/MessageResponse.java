package com.spedine.trackit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MessageResponse", description = "Generic message response")
public record MessageResponse(
        String message
) {
}
