package com.spedine.trackit.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ECurrency", description = "Supported currency codes (e.g., BRL, USD, EUR)")
public enum ECurrency {
    BRL,
    USD,
    EUR,
    GBP,
    JPY
}
