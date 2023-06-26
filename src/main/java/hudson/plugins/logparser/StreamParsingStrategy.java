package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This strategy relies on {@link BufferedReader#lines()}.
 * <p>
 * For each build, this strategy will:
 * <ul>
 *     <li>Lazily stream each line</li>
 *     <li>Map it to a status via {@link LineToStatus}</li>
 *     <li>Collect to a map of line number to status</li>
 * </ul>
 * @since 2.4.0
 * @see ClassicParsingStrategy
 */
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.getLog(), input.getCharset()));
             Stream<String> lines = reader.lines()) {
            List<String> statusByLine = lines.map(toStatus).collect(Collectors.toList());
            final HashMap<String, String> result = new HashMap<>();
            for (int i = 0; i < statusByLine.size(); i++) {
                String status = statusByLine.get(i);
                if (!LogParserConsts.NONE.equals(status)) {
                    result.put(Integer.toString(i), status);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
