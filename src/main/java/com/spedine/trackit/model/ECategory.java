package com.spedine.trackit.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ECategory", description = "Expense categories (e.g., FOOD, TRANSPORT, RENT, etc.)")
public enum ECategory {
    FOOD,
    TRANSPORT,
    RENT,
    ENTERTAINMENT,
    UTILITIES,
    HEALTH,
    EDUCATION,
    OTHER
}
