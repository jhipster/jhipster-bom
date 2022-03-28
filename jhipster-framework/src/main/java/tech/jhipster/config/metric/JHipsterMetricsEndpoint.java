package tech.jhipster.config.metric;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.search.Search;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>JHipsterMetricsEndpoint class.</p>
 */
@WebEndpoint(id = "jhimetrics")
public class JHipsterMetricsEndpoint {

    private final MeterRegistry meterRegistry;

    private final Logger logger = LoggerFactory.getLogger(JHipsterMetricsEndpoint.class);

    /** Constant <code>MISSING_NAME_TAG_MESSAGE="Missing name tag for metric {}"</code> */
    public static final String MISSING_NAME_TAG_MESSAGE = "Missing name tag for metric {}";

    /**
     * <p>Constructor for JHipsterMetricsEndpoint.</p>
     *
     * @param meterRegistry a {@link io.micrometer.core.instrument.MeterRegistry} object.
     */
    public JHipsterMetricsEndpoint(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * GET /management/jhi-metrics
     * <p>
     * Give metrics displayed on Metrics page
     *
     * @return a Map with a String defining a category of metrics as Key and
     * another Map containing metrics related to this category as Value
     */
    @ReadOperation
    public Map<String, Map<?, ?>> allMetrics() {

        Map<String, Map<?, ?>> results = new HashMap<>();
        // JVM stats
        results.put("jvm", jvmMemoryMetrics());
        // HTTP requests stats
        results.put("http.server.requests", httpRequestsMetrics());
        // Cache stats
        results.put("cache", cacheMetrics());
        // Service stats
        results.put("services", serviceMetrics());
        // Database stats
        results.put("databases", databaseMetrics());
        // Garbage collector
        results.put("garbageCollector", garbageCollectorMetrics());
        // Process stats
        results.put("processMetrics", processMetrics());

        return results;
    }

    private Map<String, Number> processMetrics() {
        Map<String, Number> resultsProcess = new HashMap<>();

        Collection<Gauge> gauges = Search.in(meterRegistry).name(s -> s.contains("cpu") || s.contains("system") || s.contains("process")).gauges();
        gauges.forEach(gauge -> resultsProcess.put(gauge.getId().getName(), gauge.value()));

        Collection<TimeGauge> timeGauges = Search.in(meterRegistry).name(s -> s.contains("process")).timeGauges();
        timeGauges.forEach(gauge -> resultsProcess.put(gauge.getId().getName(), gauge.value(TimeUnit.MILLISECONDS)));

        return resultsProcess;
    }

    private Map<String, Object> garbageCollectorMetrics() {
        Map<String, Object> resultsGarbageCollector = new HashMap<>();

        Collection<Timer> timers = Search.in(meterRegistry).name(s -> s.contains("jvm.gc.pause")).timers();
        timers.forEach(timer -> {
            String key = timer.getId().getName();

            HashMap<String, Number> gcPauseResults = new HashMap<>();
            gcPauseResults.put("count", timer.count());
            gcPauseResults.put("max", timer.max(TimeUnit.MILLISECONDS));
            gcPauseResults.put("totalTime", timer.totalTime(TimeUnit.MILLISECONDS));
            gcPauseResults.put("mean", timer.mean(TimeUnit.MILLISECONDS));

            ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
            for (ValueAtPercentile percentile : percentiles) {
                gcPauseResults.put(String.valueOf(percentile.percentile()), percentile.value(TimeUnit.MILLISECONDS));
            }

            resultsGarbageCollector.putIfAbsent(key, gcPauseResults);
        });

        Collection<Gauge> gauges = Search.in(meterRegistry).name(s -> s.contains("jvm.gc") && !s.contains("jvm.gc.pause")).gauges();
        gauges.forEach(gauge -> resultsGarbageCollector.put(gauge.getId().getName(), gauge.value()));

        Collection<Counter> counters = Search.in(meterRegistry).name(s -> s.contains("jvm.gc") && !s.contains("jvm.gc.pause")).counters();
        counters.forEach(counter -> resultsGarbageCollector.put(counter.getId().getName(), counter.count()));

        gauges = Search.in(meterRegistry).name(s -> s.contains("jvm.classes.loaded")).gauges();
        Double classesLoaded = gauges.stream().mapToDouble(Gauge::value).sum();
        resultsGarbageCollector.put("classesLoaded", classesLoaded);

        Collection<FunctionCounter> functionCounters = Search.in(meterRegistry).name(s -> s.contains("jvm.classes.unloaded")).functionCounters();
        Double classesUnloaded = functionCounters.stream().mapToDouble(FunctionCounter::count).sum();
        resultsGarbageCollector.put("classesUnloaded", classesUnloaded);

        return resultsGarbageCollector;
    }

    private Map<String, Map<String, Number>> databaseMetrics() {
        Map<String, Map<String, Number>> resultsDatabase = new HashMap<>();

        Collection<Timer> timers = Search.in(meterRegistry).name(s -> s.contains("hikari")).timers();
        timers.forEach(timer -> {
            String key = timer.getId().getName().substring(timer.getId().getName().lastIndexOf('.') + 1);

            resultsDatabase.putIfAbsent(key, new HashMap<>());
            resultsDatabase.get(key).put("count", timer.count());
            resultsDatabase.get(key).put("max", timer.max(TimeUnit.MILLISECONDS));
            resultsDatabase.get(key).put("totalTime", timer.totalTime(TimeUnit.MILLISECONDS));
            resultsDatabase.get(key).put("mean", timer.mean(TimeUnit.MILLISECONDS));

            ValueAtPercentile[] percentiles = timer.takeSnapshot().percentileValues();
            for (ValueAtPercentile percentile : percentiles) {
                resultsDatabase.get(key).put(String.valueOf(percentile.percentile()), percentile.value(TimeUnit.MILLISECONDS));
            }
        });

        Collection<Gauge> gauges = Search.in(meterRegistry).name(s -> s.contains("hikari")).gauges();
        gauges.forEach(gauge -> {
            String key = gauge.getId().getName().substring(gauge.getId().getName().lastIndexOf('.') + 1);
            resultsDatabase.putIfAbsent(key, new HashMap<>());
            resultsDatabase.get(key).put("value", gauge.value());
        });

        return resultsDatabase;
    }

    private Map<String, Map<?, ?>> serviceMetrics() {
        Collection<String> crudOperation = Arrays.asList("GET", "POST", "PUT", "DELETE");
        Collection<Timer> timers = meterRegistry.find("http.server.requests").timers();

        Set<String> uris = timers.stream().map(timer -> timer.getId().getTag("uri"))
            .collect(Collectors.toSet());
        Map<String, Map<?, ?>> resultsHttpPerUri = new HashMap<>();

        uris.forEach(uri -> {
            Map<String, Map<?, ?>> resultsPerUri = new HashMap<>();

            crudOperation.forEach(operation -> {
                Map<String, Number> resultsPerUriPerCrudOperation = new HashMap<>();

                Collection<Timer> httpTimersStream = meterRegistry.find("http.server.requests").tags("uri", uri, "method", operation).timers();
                long count = httpTimersStream.stream().mapToLong(Timer::count).sum();

                if (count != 0) {
                    double max = httpTimersStream.stream().mapToDouble(x -> x.totalTime(TimeUnit.MILLISECONDS)).max().orElse(0);
                    double totalTime = httpTimersStream.stream().mapToDouble(x -> x.totalTime(TimeUnit.MILLISECONDS)).sum();

                    resultsPerUriPerCrudOperation.put("count", count);
                    resultsPerUriPerCrudOperation.put("max", max);
                    resultsPerUriPerCrudOperation.put("mean", totalTime / count);

                    resultsPerUri.put(operation, resultsPerUriPerCrudOperation);
                }
            });

            resultsHttpPerUri.put(uri, resultsPerUri);
        });

        return resultsHttpPerUri;
    }

    private Map<String, Map<String, Number>> cacheMetrics() {
        Map<String, Map<String, Number>> resultsCache = new HashMap<>();

        Collection<FunctionCounter> counters = Search.in(meterRegistry).name(s -> s.contains("cache") && !s.contains("hibernate")).functionCounters();
        counters.forEach(counter -> {
            String key = counter.getId().getName();
            String name = counter.getId().getTag("name");
            if (name != null) {
                resultsCache.putIfAbsent(name, new HashMap<>());
                if (counter.getId().getTag("result") != null) {
                    key += "." + counter.getId().getTag("result");
                }
                resultsCache.get(name).put(key, counter.count());
            } else {
                logger.warn(MISSING_NAME_TAG_MESSAGE, key);
            }
        });

        Collection<Gauge> gauges = Search.in(meterRegistry).name(s -> s.contains("cache")).gauges();
        gauges.forEach(gauge -> {
            String key = gauge.getId().getName();
            String name = gauge.getId().getTag("name");
            if (name != null) {
                resultsCache.putIfAbsent(name, new HashMap<>());
                resultsCache.get(name).put(key, gauge.value());
            } else {
                logger.warn(MISSING_NAME_TAG_MESSAGE, key);
            }
        });
        return resultsCache;
    }

    private Map<String, Map<String, Number>> jvmMemoryMetrics() {
        Map<String, Map<String, Number>> resultsJvm = new HashMap<>();

        Search jvmUsedSearch = Search.in(meterRegistry).name(s -> s.contains("jvm.memory.used"));

        Collection<Gauge> gauges = jvmUsedSearch.gauges();
        gauges.forEach(gauge -> {
            String key = gauge.getId().getTag("id");
            resultsJvm.putIfAbsent(key, new HashMap<>());
            resultsJvm.get(key).put("used", gauge.value());
        });

        Search jvmMaxSearch = Search.in(meterRegistry).name(s -> s.contains("jvm.memory.max"));

        gauges = jvmMaxSearch.gauges();
        gauges.forEach(gauge -> {
            String key = gauge.getId().getTag("id");
            resultsJvm.get(key).put("max", gauge.value());
        });

        gauges = Search.in(meterRegistry).name(s -> s.contains("jvm.memory.committed")).gauges();
        gauges.forEach(gauge -> {
            String key = gauge.getId().getTag("id");
            resultsJvm.get(key).put("committed", gauge.value());
        });

        return resultsJvm;
    }

    private Map<String, Map<?, ?>> httpRequestsMetrics() {
        Set<String> statusCode = new HashSet<>();
        Collection<Timer> timers = meterRegistry.find("http.server.requests").timers();

        timers.forEach(timer -> statusCode.add(timer.getId().getTag("status")));

        Map<String, Map<?, ?>> resultsHttp = new HashMap<>();
        Map<String, Map<String, Number>> resultsHttpPerCode = new HashMap<>();

        statusCode.forEach(code -> {
            Map<String, Number> resultsPerCode = new HashMap<>();

            Collection<Timer> httpTimersStream = meterRegistry.find("http.server.requests").tag("status", code).timers();
            long count = httpTimersStream.stream().mapToLong(Timer::count).sum();
            double max = httpTimersStream.stream().mapToDouble(x -> x.max(TimeUnit.MILLISECONDS)).max().orElse(0);
            double totalTime = httpTimersStream.stream().mapToDouble(x -> x.totalTime(TimeUnit.MILLISECONDS)).sum();

            resultsPerCode.put("count", count);
            resultsPerCode.put("max", max);
            resultsPerCode.put("mean", count != 0 ? totalTime / count : 0);

            resultsHttpPerCode.put(code, resultsPerCode);
        });

        resultsHttp.put("percode", resultsHttpPerCode);

        timers = meterRegistry.find("http.server.requests").timers();
        long countAllRequests = timers.stream().mapToLong(Timer::count).sum();
        Map<String, Number> resultsHTTPAll = new HashMap<>();
        resultsHTTPAll.put("count", countAllRequests);

        resultsHttp.put("all", resultsHTTPAll);

        return resultsHttp;
    }

}
