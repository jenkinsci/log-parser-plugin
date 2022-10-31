package hudson.plugins.logparser;

import java.util.HashMap;

/**
 * Extracted from {@link LogParserStatusComputer} to support additional parsing implementations.
 * @see ClassicParsingStrategy
 * @see StreamParsingStrategy
 * @since 2.4.0
 */
interface ParsingStrategy {
    HashMap<String, String> parse(ParsingInput input);
}
