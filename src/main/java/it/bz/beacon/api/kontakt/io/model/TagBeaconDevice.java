// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.beacon.api.kontakt.io.model;

import it.bz.beacon.api.kontakt.io.model.enumeration.ButtonFeature;

import java.util.List;

public class TagBeaconDevice extends BeaconDevice {
    private Accelerometer accelerometer;
    private int txPower;
    private PowerSaving powerSaving;
    private List<ButtonFeature> buttonFeatures;
    private int panicDuration;
    private String eidInitialTimestamp;
    private int eidRotationPeriodExponent;

    public Accelerometer getAccelerometer() {
        return accelerometer;
    }

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer = accelerometer;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public PowerSaving getPowerSaving() {
        return powerSaving;
    }

    public void setPowerSaving(PowerSaving powerSaving) {
        this.powerSaving = powerSaving;
    }

    public List<ButtonFeature> getButtonFeatures() {
        return buttonFeatures;
    }

    public void setButtonFeatures(List<ButtonFeature> buttonFeatures) {
        this.buttonFeatures = buttonFeatures;
    }

    public int getPanicDuration() {
        return panicDuration;
    }

    public void setPanicDuration(int panicDuration) {
        this.panicDuration = panicDuration;
    }

    public String getEidInitialTimestamp() {
        return eidInitialTimestamp;
    }

    public void setEidInitialTimestamp(String eidInitialTimestamp) {
        this.eidInitialTimestamp = eidInitialTimestamp;
    }

    public int getEidRotationPeriodExponent() {
        return eidRotationPeriodExponent;
    }

    public void setEidRotationPeriodExponent(int eidRotationPeriodExponent) {
        this.eidRotationPeriodExponent = eidRotationPeriodExponent;
    }
}
