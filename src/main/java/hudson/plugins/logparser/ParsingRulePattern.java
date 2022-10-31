package hudson.plugins.logparser;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

class ParsingRulePattern {
    private final String rule;
    private final Pattern pattern;

    ParsingRulePattern(String rule, Pattern pattern) {
        this.rule = rule;
        this.pattern = pattern;
    }

    public String getRule() {
        return rule;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsingRulePattern that = (ParsingRulePattern) o;
        return Objects.equals(rule, that.rule) && Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rule, pattern);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParsingRulePattern.class.getSimpleName() + "[", "]")
                .add("rule='" + rule + "'")
                .add("pattern=" + pattern)
                .toString();
    }
}
