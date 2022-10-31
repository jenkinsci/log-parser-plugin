package hudson.plugins.logparser;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ParsingStrategyLocatorTest {

    @Test
    public void shouldDefaultToClassic() {
        Map<String, String> systemProperties = new HashMap<>();
        ParsingStrategyLocator locator = new ParsingStrategyLocator(systemProperties);

        ParsingStrategy actual = locator.get();

        assertThat(actual).isInstanceOf(ClassicParsingStrategy.class);
    }

    @Test
    public void shouldAllowOverridingToStream() {
        Map<String, String> systemProperties = new HashMap<>();
        systemProperties.put(ParsingStrategy.class.getName(), StreamParsingStrategy.class.getName());
        ParsingStrategyLocator locator = new ParsingStrategyLocator(systemProperties);

        ParsingStrategy actual = locator.get();

        assertThat(actual).isInstanceOf(StreamParsingStrategy.class);
    }

    @Test
    public void shouldFallbackToClassic() {
        Map<String, String> systemProperties = new HashMap<>();
        systemProperties.put(ParsingStrategy.class.getName(), "does.not.match");
        ParsingStrategyLocator locator = new ParsingStrategyLocator(systemProperties);

        ParsingStrategy actual = locator.get();

        assertThat(actual).isInstanceOf(ClassicParsingStrategy.class);
    }
}
