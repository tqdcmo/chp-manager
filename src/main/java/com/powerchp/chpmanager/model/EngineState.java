package com.powerchp.chpmanager.model;

import java.util.Objects;

public enum EngineState {
    RUNNING("روشن"),
    STOPPED("خاموش"),
    READY("آماده"),
    MAINTENANCE("در حال تعمیر"),
    ERROR("خطا"),
    OFFLINE("آفلاین");

    private final String faName;

    EngineState(String faName) {
        this.faName = faName;
    }

    public String getFaName() {
        return faName;
    }

    public static EngineState fromFaName(String faName) {
        if (faName == null || faName.trim().isEmpty()) {
            throw new IllegalArgumentException("نام فارسی وضعیت موتور نمی‌تواند خالی باشد.");
        }

        for (EngineState state : EngineState.values()) {
            if (Objects.equals(state.faName.trim(), faName.trim())) {
                return state;
            }
        }

        throw new IllegalArgumentException("وضعیت موتور نامعتبر است: " + faName);
    }
}
