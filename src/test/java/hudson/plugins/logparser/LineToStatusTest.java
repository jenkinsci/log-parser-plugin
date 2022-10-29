package hudson.plugins.logparser;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class LineToStatusTest {
    private LineToStatus toStatus;

    @Before
    public void setUp() {
        toStatus = new LineToStatus(Arrays.asList(
                new ParsingRulePattern("my-rule", Pattern.compile("abc")),
                new ParsingRulePattern("my-second-rule", Pattern.compile("bc")),
                new ParsingRulePattern("#my-commented-rule", Pattern.compile("xyz"))
        ));
    }

    @Test
    public void shouldHandleEmpty() {
        String actual = toStatus.apply("");
        assertThat(actual).isEqualTo(LogParserConsts.NONE);
    }

    @Test
    public void shouldSkipCommentedRule() {
        String actual = toStatus.apply("xyz");
        assertThat(actual).isEqualTo(LogParserConsts.NONE);
    }

    @Test
    public void shouldFindOnlyFirstMatchingRule() {
        String actual = toStatus.apply("abc");
        assertThat(actual).isEqualTo("my-rule");
    }
}
