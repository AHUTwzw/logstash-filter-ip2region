package org.logstashplugins;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.FilterMatchListener;
import org.junit.Assert;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstash.plugins.ContextImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaFilterExampleTest {

    @Test
    public void testJavaExampleFilter() {
        String sourceField = "ip";
        String targetField = "geoip";
        Map<String, Object> configValues = new HashMap<>();
        configValues.put(Ip2region.DATABASE.name(), "./data/ip2region_v4.xdb");
        configValues.put(Ip2region.SOURCE_CONFIG.name(), sourceField);
        configValues.put(Ip2region.TARGET_CONFIG.name(), targetField);
        Configuration config = new ConfigurationImpl(configValues);
        Context context = new ContextImpl(null, null);
        Ip2region filter = new Ip2region("test-id", config, context);

        Event e = new org.logstash.Event();
        TestMatchListener matchListener = new TestMatchListener();
        e.setField(sourceField, "61.245.138.83");
        Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
        System.out.println(e.getField(targetField));
    }
}

class TestMatchListener implements FilterMatchListener {

    private AtomicInteger matchCount = new AtomicInteger(0);

    @Override
    public void filterMatched(Event event) {
        matchCount.incrementAndGet();
    }

    public int getMatchCount() {
        return matchCount.get();
    }
}