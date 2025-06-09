package com.powerchp.chpmanager.model;

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
        for (EngineState state : EngineState.values()) {
            if (state.faName.equalsIgnoreCase(faName.trim())) {
                return state;
            }
        }
        throw new IllegalArgumentException("وضعیت موتور نامعتبر است: " + faName);
    }
}
