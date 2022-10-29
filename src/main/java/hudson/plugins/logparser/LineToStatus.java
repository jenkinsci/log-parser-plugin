package hudson.plugins.logparser;

import hudson.console.ConsoleNote;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LineToStatus implements UnaryOperator<String> {
    private final List<ParsingRulePattern> patterns;

    LineToStatus(List<ParsingRulePattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String apply(String s) {
        // For now, strip out ConsoleNote(s) before parsing.
        // Notes are injected into log lines, and can break start-of-line
        // patterns, and include html. Will likely need alternative way to
        // handle in the future.
        String line = ConsoleNote.removeNotes(s);
        for (ParsingRulePattern parsingRulePattern : patterns) {
            String rule = parsingRulePattern.getRule();
            if (LogParserUtils.skipParsingRule(rule)) {
                continue;
            }
            Pattern pattern = parsingRulePattern.getPattern();
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String status = rule.split("\\s")[0];
                return LogParserUtils.standardizeStatus(status);
            }
        }
        return LogParserConsts.NONE;
    }
}
