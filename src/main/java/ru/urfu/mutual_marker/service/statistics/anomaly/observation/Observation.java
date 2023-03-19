package ru.urfu.mutual_marker.service.statistics.anomaly.observation;

public class Observation {
    /**
     * The value of this observation.
     */
    private final double value;

    /**
     * The group from which this observation belongs.
     */
    private final int group;

    /**
     * Constructs an observation with the specified value and group.
     *
     * @param value the value of this observation
     * @param group the group from which this observation belongs
     */
    public Observation(double value, int group) {
        super();
        this.value = value;
        this.group = group;
    }

    /**
     * Returns the value of this observation.
     *
     * @return the value of this observation
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the group from which this observation belongs.
     *
     * @return the group from which this observation belongs
     */
    public int getGroup() {
        return group;
    }
}
