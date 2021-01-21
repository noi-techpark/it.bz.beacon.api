package it.bz.beacon.api.kontakt.io.model;

import java.util.List;

public class PowerSaving {

    private List<Feature> features;
    private Integer moveSuspendTimeout;
    private Integer powerSaverAdvertiseInterval;
    private List<Rtc> rtc;
    private Integer lightSensorThreshold;
    private Integer lightSensorHysteresis;
    private Integer burstOnTime;
    private Integer burstOffTime;

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    public Integer getMoveSuspendTimeout() {
        return moveSuspendTimeout;
    }

    public void setMoveSuspendTimeout(Integer moveSuspendTimeout) {
        this.moveSuspendTimeout = moveSuspendTimeout;
    }

    public Integer getPowerSaverAdvertiseInterval() {
        return powerSaverAdvertiseInterval;
    }

    public void setPowerSaverAdvertiseInterval(Integer powerSaverAdvertiseInterval) {
        this.powerSaverAdvertiseInterval = powerSaverAdvertiseInterval;
    }

    public List<Rtc> getRtc() {
        return rtc;
    }

    public void setRtc(List<Rtc> rtc) {
        this.rtc = rtc;
    }

    public Integer getLightSensorThreshold() {
        return lightSensorThreshold;
    }

    public void setLightSensorThreshold(Integer lightSensorThreshold) {
        this.lightSensorThreshold = lightSensorThreshold;
    }

    public Integer getLightSensorHysteresis() {
        return lightSensorHysteresis;
    }

    public void setLightSensorHysteresis(Integer lightSensorHysteresis) {
        this.lightSensorHysteresis = lightSensorHysteresis;
    }

    public Integer getBurstOnTime() {
        return burstOnTime;
    }

    public void setBurstOnTime(Integer burstOnTime) {
        this.burstOnTime = burstOnTime;
    }

    public Integer getBurstOffTime() {
        return burstOffTime;
    }

    public void setBurstOffTime(Integer burstOffTime) {
        this.burstOffTime = burstOffTime;
    }

    public static class Rtc {
        private Day day;
        private List<Integer> hours;

        public Day getDay() {
            return day;
        }

        public void setDay(Day day) {
            this.day = day;
        }

        public List<Integer> getHours() {
            return hours;
        }

        public void setHours(List<Integer> hours) {
            this.hours = hours;
        }

        public enum Day {
            MON,
            TUE,
            WED,
            THU,
            FRI,
            SAT,
            SUN
        }
    }

    public enum Feature {
        LIGHT_SENSOR,
        RTC,
        MOTION_DETECTION,
        BURST_MODE
    }
}
