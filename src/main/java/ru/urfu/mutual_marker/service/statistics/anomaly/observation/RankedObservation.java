package ru.urfu.mutual_marker.service.statistics.anomaly.observation;

public class RankedObservation extends Observation {
    /**
     * The rank of this observation.
     */
    private double rank;

    /**
     * Constructs a ranked observation with the specified value and group. The
     * rank of this observation is default to 0.0.
     *
     * @param value the value of this observation
     * @param group the group from which this observation belongs
     */
    public RankedObservation(double value, int group) {
        super(value, group);
    }

    /**
     * Returns the rank of this observation.
     *
     * @return the rank of this observation
     */
    public double getRank() {
        return rank;
    }

    /**
     * Sets the rank of this observation.
     *
     * @param rank the new rank for this observation
     */
    public void setRank(double rank) {
        this.rank = rank;
    }
}
