package ru.urfu.mutual_marker.service.statistics;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class KruskalWallisTest extends OrdinalStatisticalTest{
    /**
     * Constructs a Kruskal-Wallis test with the specified number of groups.
     *
     * @param numberOfGroups the number of groups being tested - кол-во криитериев на проекте
     */
    public KruskalWallisTest(int numberOfGroups) {
        super(numberOfGroups);

        if (numberOfGroups <= 1) {
            throw new IllegalArgumentException("requires two or more groups");
        }
    }

    // make method public
    /**
     * @param value - его оценки по критериям
     * @param group - i-ый студент
     */
    @Override
    public void add(double value, int group) {
        super.add(value, group);
    }

    // make method public
    /**
     * @param values - его оценки по критериям
     * @param group - i-ый студент
     */
    @Override
    public void addAll(double[] values, int group) {
        super.addAll(values, group);
    }

    /**
     * Computes the chi-squared approximation of the Kruskal-Wallis test
     * statistic. See equation (22-1) in the reference book for details.
     *
     * @return the chi-squared approximation of the Kruskal-Wallis test
     *         statistic
     */
    double H() {
        int[] n = new int[numberOfGroups];
        double[] rbar = new double[numberOfGroups];

        for (RankedObservation observation : data) {
            n[observation.getGroup()]++;
            rbar[observation.getGroup()] += observation.getRank();
        }

        double H = 0.0;
        for (int i = 0; i < numberOfGroups; i++) {
            H += Math.pow(rbar[i], 2.0) / n[i];
        }

        int N = data.size();
        return 12.0 / (N * (N + 1)) * H - 3.0 * (N + 1);
    }

    /**
     * Computes the correction factor for ties. See equation (22-3) in the
     * reference book for details.
     *
     * @return the correction factor for ties
     */
    double C() {
        int N = data.size();
        double C = 0.0;

        int i = 0;
        while (i < N) {
            int j = i + 1;

            while ((j < N)
                    && (data.get(i).getValue() == data.get(j).getValue())) {
                j++;
            }

            C += Math.pow(j - i, 3.0) - (j - i);
            i = j;
        }

        return 1 - C / (Math.pow(N, 3.0) - N);
    }

    /**
     * @param alpha - константа, определяющая на сколько точно надо считать
     */
    @Override
    public boolean test(double alpha) {
        update();

        ChiSquaredDistribution dist = new ChiSquaredDistribution(
                numberOfGroups - 1);
        double H = H();
        double C = C();

        if (C == 0.0) {
            // all observations the same
            return false;
        }

        return 1.0 - dist.cumulativeProbability(H / C) < alpha;
    }
}
