package org.logstashplugins;

import co.elastic.logstash.api.*;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


// class name must match plugin name
@LogstashPlugin(name = "ip2region")
public class Ip2region implements Filter {

    public static final PluginConfigSpec<String> DATABASE =
            PluginConfigSpec.stringSetting("database", "/data/ip2region/ip2region.xdb");
    public static final PluginConfigSpec<String> SOURCE_CONFIG =
            PluginConfigSpec.stringSetting("source", "ip");
    public static final PluginConfigSpec<String> TARGET_CONFIG =
            PluginConfigSpec.stringSetting("target", "geo_ip");

    private String id;
    private String sourceField;
    private String targetField;
    private Searcher searcher;

    public Ip2region(String id, Configuration config, Context context) {
        // constructors should validate configuration options
        this.id = id;
        this.sourceField = config.get(SOURCE_CONFIG);
        this.targetField = config.get(TARGET_CONFIG);
        String dbPath = config.get(DATABASE);
        try {
            this.searcher = Searcher.newWithFileOnly(dbPath);
        } catch ( Exception e ) {
            System.out.printf("failed to create searcher with `%s`: %s\n", dbPath, e);
        }
    }

    @Override
    public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {
        for (Event e : events) {
            Object f = e.getField(sourceField);
            if (f instanceof String) {
                Region region = getRegion((String)f);
                e.setField(targetField, region);
                matchListener.filterMatched(e);
            }
        }
        return events;
    }

    public Region getRegion(long ip) {
        try {
            return convert(searcher.search(ip));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the geographic region corresponding to the given IP address.
     *
     * @param ip The IP address in String format.
     * @return The geographic region associated with the IP address.
     * @throws RuntimeException if an exception occurs during the region lookup process.
     */
    public Region getRegion(String ip) {
        try {
            return convert(searcher.search(ip));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a region string into a Region object.
     *
     * @param regionStr The region string to be converted.
     * @return A Region object representing the converted region string, or null if the input is invalid or empty.
     */
    private static Region convert(String regionStr) {
        if (regionStr == null || regionStr.length() == 0) {
            return null;
        }
        String[] regionSplit = regionStr.split("\\|");
        if (regionSplit.length != 5) {
            return null;
        }
        Region region = new Region();
        region.setCountry("0".equals(regionSplit[0]) ? null : regionSplit[0]);
        region.setRegion("0".equals(regionSplit[1]) ? null : regionSplit[1]);
        region.setProvince("0".equals(regionSplit[2]) ? null : regionSplit[2]);
        region.setCity("0".equals(regionSplit[3]) ? null : regionSplit[3]);
        region.setIsp("0".equals(regionSplit[4]) ? null : regionSplit[4]);
        return region;
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        // should return a list of all configuration options for this plugin
        List<PluginConfigSpec<?>> list = new ArrayList<>();
        list.add(SOURCE_CONFIG);
        list.add(TARGET_CONFIG);
        list.add(DATABASE);
        return Collections.synchronizedList(list);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
