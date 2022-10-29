package hudson.plugins.logparser;

import java.util.HashMap;

interface ParsingStrategy {
    HashMap<String, String> parse(ParsingInput input);
}
