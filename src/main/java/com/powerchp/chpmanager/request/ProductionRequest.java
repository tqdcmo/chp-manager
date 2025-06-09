package com.powerchp.chpmanager.request;

public class ProductionRequest {
    private double powerGenerated;
    private double gasConsumed;

    public double getPowerGenerated() {
        return powerGenerated;
    }

    public void setPowerGenerated(double powerGenerated) {
        this.powerGenerated = powerGenerated;
    }

    public double getGasConsumed() {
        return gasConsumed;
    }

    public void setGasConsumed(double gasConsumed) {
        this.gasConsumed = gasConsumed;
    }
}
