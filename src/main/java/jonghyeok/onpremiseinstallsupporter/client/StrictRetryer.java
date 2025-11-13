package jonghyeok.onpremiseinstallsupporter.client;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrictRetryer implements Retryer {

    private final int maxAttempts;
    private final long period;
    private final long maxPeriod;
    int attempt;
    long sleptForMillis;

    public StrictRetryer(long period, long maxPeriod, int maxAttempts) {
        this.period = period;
        this.maxPeriod = maxPeriod;
        this.maxAttempts = maxAttempts;
        this.attempt = 1;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (attempt++ >= maxAttempts) throw e;

        long interval;
        if (e.retryAfter() != null) {
            interval = e.retryAfter() - System.currentTimeMillis();
            log.info("Retry-After 헤더 존재: {} ms", interval);
        } else {
            interval = nextMaxInterval();
        }
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw e;
        }
        sleptForMillis += interval;
    }

    long nextMaxInterval() {
        long interval = (long) (period * Math.pow(1.5, attempt - 1));
        return Math.min(interval, maxPeriod);
    }

    @Override
    public Retryer clone() {
        return new StrictRetryer(period, maxPeriod, maxAttempts);
    }
}
