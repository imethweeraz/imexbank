package com.imeth.imexbank.interceptors;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class PerformanceInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);
    private static final long SLOW_METHOD_THRESHOLD = 1000; // 1 second

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @AroundInvoke
    public Object measurePerformance(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();
        long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        long startUserTime = threadMXBean.getCurrentThreadUserTime();

        try {
            Object result = context.proceed();

            long duration = System.currentTimeMillis() - startTime;
            long cpuTime = (threadMXBean.getCurrentThreadCpuTime() - startCpuTime) / 1_000_000;
            long userTime = (threadMXBean.getCurrentThreadUserTime() - startUserTime) / 1_000_000;

            if (duration > SLOW_METHOD_THRESHOLD) {
                logger.warn("Slow method detected: {}.{} took {} ms (CPU: {} ms, User: {} ms)",
                        className, methodName, duration, cpuTime, userTime);
            } else {
                logger.trace("Method {}.{} completed in {} ms",
                        className, methodName, duration);
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Method {}.{} failed after {} ms",
                    className, methodName, duration);
            throw e;
        }
    }
}