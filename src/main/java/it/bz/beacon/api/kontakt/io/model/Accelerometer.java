package it.bz.beacon.api.kontakt.io.model;

import java.util.Set;

public class Accelerometer {
    private DoubleTap doubleTap;
    private Set<Feature> features;
    private Move move;
    private HighPass highPass;
    private Integer sensitivity;
    private Preset preset;

    public DoubleTap getDoubleTap() {
        return doubleTap;
    }

    public void setDoubleTap(DoubleTap doubleTap) {
        this.doubleTap = doubleTap;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public HighPass getHighPass() {
        return highPass;
    }

    public void setHighPass(HighPass highPass) {
        this.highPass = highPass;
    }

    public Integer getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Integer sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Preset getPreset() {
        return preset;
    }

    public void setPreset(Preset preset) {
        this.preset = preset;
    }

    public enum Feature {
        ACCELEROMETER,
        X_AXIS,
        Y_AXIS,
        Z_AXIS,
        MOVE_DETECTION,
        DOUBLE_TAP_DETECTION
    }

    public enum Preset {
        UNKNOWN,
        MOVEMENT,
        FREE_FALL,
        DOUBLE_TAP,
        DOUBLE_TAP_AND_MOVEMENT,
        DOUBLE_TAP_AND_FREE_FALL
    }

    public static class DoubleTap {
        private Integer timeLimit;
        private Integer timeWindow;
        private Set<DetectionFlag> detectionFlags;
        private Integer threshold;
        private Integer timeLatency;

        public Integer getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(Integer timeLimit) {
            this.timeLimit = timeLimit;
        }

        public Integer getTimeWindow() {
            return timeWindow;
        }

        public void setTimeWindow(Integer timeWindow) {
            this.timeWindow = timeWindow;
        }

        public Set<DetectionFlag> getDetectionFlags() {
            return detectionFlags;
        }

        public void setDetectionFlags(Set<DetectionFlag> detectionFlags) {
            this.detectionFlags = detectionFlags;
        }

        public Integer getThreshold() {
            return threshold;
        }

        public void setThreshold(Integer threshold) {
            this.threshold = threshold;
        }

        public Integer getTimeLatency() {
            return timeLatency;
        }

        public void setTimeLatency(Integer timeLatency) {
            this.timeLatency = timeLatency;
        }

        public enum DetectionFlag {
            X_AXIS,
            Y_AXIS,
            Z_AXIS
        }
    }

    public static class Move {
        private Integer duration;
        private Set<DetectionFlag> detectionFlags;
        private DetectionFlagJunction detectionFlagsJunction;
        private Integer threshold;

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Set<DetectionFlag> getDetectionFlags() {
            return detectionFlags;
        }

        public void setDetectionFlags(Set<DetectionFlag> detectionFlags) {
            this.detectionFlags = detectionFlags;
        }

        public DetectionFlagJunction getDetectionFlagsJunction() {
            return detectionFlagsJunction;
        }

        public void setDetectionFlagsJunction(DetectionFlagJunction detectionFlagsJunction) {
            this.detectionFlagsJunction = detectionFlagsJunction;
        }

        public Integer getThreshold() {
            return threshold;
        }

        public void setThreshold(Integer threshold) {
            this.threshold = threshold;
        }

        public enum DetectionFlag {
            X_POSITIVE,
            Y_POSITIVE,
            Z_POSITIVE,
            X_NEGATIVE,
            Y_NEGATIVE,
            Z_NEGATIVE
        }

        public enum DetectionFlagJunction {
            AND,
            OR
        }
    }

    public static class HighPass {
        private Mode mode;
        private Integer reference;
        private boolean accelerometerData;
        private boolean moveDetection;
        private double cutOffFrequency;
        private boolean doubleTapDetection;

        public Mode getMode() {
            return mode;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
        }

        public Integer getReference() {
            return reference;
        }

        public void setReference(Integer reference) {
            this.reference = reference;
        }

        public boolean isAccelerometerData() {
            return accelerometerData;
        }

        public void setAccelerometerData(boolean accelerometerData) {
            this.accelerometerData = accelerometerData;
        }

        public boolean isMoveDetection() {
            return moveDetection;
        }

        public void setMoveDetection(boolean moveDetection) {
            this.moveDetection = moveDetection;
        }

        public double getCutOffFrequency() {
            return cutOffFrequency;
        }

        public void setCutOffFrequency(double cutOffFrequency) {
            this.cutOffFrequency = cutOffFrequency;
        }

        public boolean isDoubleTapDetection() {
            return doubleTapDetection;
        }

        public void setDoubleTapDetection(boolean doubleTapDetection) {
            this.doubleTapDetection = doubleTapDetection;
        }

        public enum Mode {
            NORMAL,
            NORMAL_RESET,
            AUTORESET,
            REFERENCE
        }
    }
}
