package healery.gadgetbridge.devices.amazfitbip;

import healery.gadgetbridge.entities.BaseActivitySummary;
import healery.gadgetbridge.util.DateTimeUtils;

public class BipActivitySummary extends BaseActivitySummary {
    private int version;
    private float distanceMeters;
    private float ascentMeters;
    private float descentMeters;
    private float minAltitude;
    private float maxAltitude;
    private int minLatitude;
    private int maxLatitude;
    private int minLongitude;
    private int maxLongitude;
    private long steps;
    private long activeTimeSeconds;
    private float caloriesBurnt;
    private float maxSpeed;
    private float minPace;
    private float maxPace;
    private float totalStride;
    private long timeAscent;
    private long timeDescent;
    private long timeFlat;
    private int averageHR;
    private int averagePace;
    private int averageStride;

//    @Override
//    public long getSteps() {
//        return steps;
//    }
//
//    @Override
//    public float getDistanceMeters() {
//        return distanceMeters;
//    }
//
//    @Override
//    public float getAscentMeters() {
//        return ascentMeters;
//    }
//
//    @Override
//    public float getDescentMeters() {
//        return descentMeters;
//    }
//
//    @Override
//    public float getMinAltitude() {
//        return minAltitude;
//    }
//
//    @Override
//    public float getMaxAltitude() {
//        return maxAltitude;
//    }
//
//    @Override
//    public float getCalories() {
//        return caloriesBurnt;
//    }
//
//    @Override
//    public float getMaxSpeed() {
//        return maxSpeed;
//    }
//
//    @Override
//    public float getMinSpeed() {
//        return minPace;
//    }
//
//    @Override
//    public float getAverageSpeed() {
//        return averagePace;
//    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setDistanceMeters(float distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public void setAscentMeters(float ascentMeters) {
        this.ascentMeters = ascentMeters;
    }

    public void setDescentMeters(float descentMeters) {
        this.descentMeters = descentMeters;
    }

    public void setMinAltitude(float minAltitude) {
        this.minAltitude = minAltitude;
    }

    public void setMaxAltitude(float maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public void setMinLatitude(int minLatitude) {
        this.minLatitude = minLatitude;
    }

    public void setMaxLatitude(int maxLatitude) {
        this.maxLatitude = maxLatitude;
    }

    public void setMinLongitude(int minLongitude) {
        this.minLongitude = minLongitude;
    }

    public void setMaxLongitude(int maxLongitude) {
        this.maxLongitude = maxLongitude;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public void setActiveTimeSeconds(long activeTimeSeconds) {
        this.activeTimeSeconds = activeTimeSeconds;
    }

    public void setCaloriesBurnt(float caloriesBurnt) {
        this.caloriesBurnt = caloriesBurnt;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setMinPace(float minPace) {
        this.minPace = minPace;
    }

    public void setMaxPace(float maxPace) {
        this.maxPace = maxPace;
    }

    public void setTotalStride(float totalStride) {
        this.totalStride = totalStride;
    }

    public float getTotalStride() {
        return totalStride;
    }

    public void setTimeAscent(long timeAscent) {
        this.timeAscent = timeAscent;
    }

    public long getTimeAscent() {
        return timeAscent;
    }

    public void setTimeDescent(long timeDescent) {
        this.timeDescent = timeDescent;
    }

    public long getTimeDescent() {
        return timeDescent;
    }

    public void setTimeFlat(long timeFlat) {
        this.timeFlat = timeFlat;
    }

    public long getTimeFlat() {
        return timeFlat;
    }

    public void setAverageHR(int averageHR) {
        this.averageHR = averageHR;
    }

    public int getAverageHR() {
        return averageHR;
    }

    public void setAveragePace(int averagePace) {
        this.averagePace = averagePace;
    }

    public int getAveragePace() {
        return averagePace;
    }

    public void setAverageStride(int averageStride) {
        this.averageStride = averageStride;
    }

    public int getAverageStride() {
        return averageStride;
    }
}
