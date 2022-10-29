package hudson.plugins.logparser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Finds the desired {@link ParsingStrategy} by reading the system property {@code hudson.plugins.logparser.ParsingStrategy}.
 * <p>
 * The default strategy is {@link ClassicParsingStrategy}, which was the only available strategy through v2.3.0.
 * This class logs the strategy selected at INFO level.
 * <p>
 * An invalid parameter is logged at WARNING and falls back to {@link ClassicParsingStrategy}.
 *
 * @since 2.4.0
 */
class ParsingStrategyLocator {
    private static final String SYSTEM_PROPERTY = ParsingStrategy.class.getName();
    private static final String CLASSIC = ClassicParsingStrategy.class.getName();
    private static final String STREAM = StreamParsingStrategy.class.getName();
    private static final Logger LOGGER = Logger.getLogger(ParsingStrategyLocator.class.getName());
    private final Map<String, String> systemProperties;

    ParsingStrategyLocator(Map<String, String> systemProperties) {
        this.systemProperties = systemProperties;
    }

    static ParsingStrategyLocator create() {
        Map<String, String> typedProperties = new HashMap<>();
        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            typedProperties.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return new ParsingStrategyLocator(typedProperties);
    }

    ParsingStrategy get() {
        String strategy = systemProperties.get(SYSTEM_PROPERTY);

        if (STREAM.equals(strategy)) {
            LOGGER.info("Using " + STREAM + " as requested by system property " + SYSTEM_PROPERTY);
            return new StreamParsingStrategy();
        }
        if (strategy == null) {
            LOGGER.info("Defaulting to " + CLASSIC);
        } else if (CLASSIC.equals(strategy)) {
            LOGGER.info("Using " + CLASSIC + " as requested by system property " + SYSTEM_PROPERTY);
        } else {
            LOGGER.warning("Defaulting to " + CLASSIC + " because system property " + SYSTEM_PROPERTY + " was set to invalid strategy " + strategy);
        }
        return new ClassicParsingStrategy();
    }
}
