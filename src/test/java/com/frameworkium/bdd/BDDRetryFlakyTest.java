package com.frameworkium.bdd;

import com.frameworkium.core.common.properties.Property;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BDDRetryFlakyTest implements IRetryAnalyzer {

    static final int MAX_RETRY_COUNT;
    static final HashMap<String, AtomicInteger> retryCountPerTest = new HashMap<>();

    public boolean retry(ITestResult result) {
        retryCountPerTest.putIfAbsent(result.getName(), new AtomicInteger(0));
        return this.retryCountPerTest.get(result.getName()).getAndIncrement() < MAX_RETRY_COUNT;
    }

    static {
        MAX_RETRY_COUNT = Property.MAX_RETRY_COUNT.getIntWithDefault(1);
    }

}
