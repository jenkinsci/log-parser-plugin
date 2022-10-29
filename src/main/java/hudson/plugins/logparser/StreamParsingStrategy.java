package hudson.plugins.logparser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StreamParsingStrategy implements ParsingStrategy {
    @Override
    public HashMap<String, String> parse(ParsingInput input) {
        String[] parsingRulesArray = input.getParsingRulesArray();
        Pattern[] compiledPatterns = input.getCompiledPatterns();
        List<ParsingRulePattern> parsingRulePatterns = new LinkedList<>();
        for (int i = 0; i < parsingRulesArray.length; i++) {
            String rule = parsingRulesArray[i];
            Pattern pattern = compiledPatterns[i];
            parsingRulePatterns.add(new ParsingRulePattern(rule, pattern));
        }
        LineToStatus toStatus = new LineToStatus(parsingRulePatterns);
        try (Stream<String> lines = input.getReader().lines()) {
            List<String> statusByLine = lines.map(toStatus).collect(Collectors.toList());
            final HashMap<String, String> result = new HashMap<>();
            for (int i = 0; i < statusByLine.size(); i++) {
                result.put(Integer.toString(i), statusByLine.get(i));
            }
            return result;
        }
    }
}
