package ru.urfu.mutual_marker.service.statistics;

public interface StatisticalTest {
    /**
     * Returns {@code true} if the null hypothesis is rejected; {@code false}
     * otherwise. The meaning of the null hypothesis and alternative hypothesis
     * depends on the specific test.
     * <p>
     * The prespecified level of confidence, alpha, can be used for either
     * one-tailed or two-tailed (directional or nondirectional) distributions,
     * depending on the specific test. Some tests may only support specific
     * values for alpha.
     *
     * @param alpha the prespecified level of confidence
     * @return {@code true} if the null hypothesis is rejected; {@code false}
     *         otherwise
     */
    public boolean test(double alpha);
}
