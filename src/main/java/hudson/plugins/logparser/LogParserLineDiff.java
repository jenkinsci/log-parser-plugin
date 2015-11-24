package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class LogParserLineDiff {

    private String htmlString = null;
    private List<Delta> deltas;
    private List<String> prevConsoleOutput = null;
    private List<String> currConsoleOutput = null;

    public List<String> getPrevConsoleOutput() {
        return prevConsoleOutput;
    }

    public List<String> getCurrConsoleOutput() {
        return currConsoleOutput;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public int lineDiff(List<String> original, List<String> revised) {

        Patch patch = DiffUtils.diff(original, revised);
        deltas = patch.getDeltas();

        return deltas.size();
    }

    public int lineDiff(String prevPath, String currPath) throws IOException {
        int res = -1;
        if (prevPath.length() != 0) {
            BufferedReader prevReader = new BufferedReader(new FileReader(prevPath));
            prevConsoleOutput = new ArrayList<String>();
            String line = "";
            while ((line = prevReader.readLine()) != null) {
                prevConsoleOutput.add(line);
            }
            prevReader.close();

            BufferedReader currReader = new BufferedReader(new FileReader(currPath));
            currConsoleOutput = new ArrayList<String>();
            while ((line = currReader.readLine()) != null) {
                currConsoleOutput.add(line);
            }
            currReader.close();
            res = lineDiff(prevConsoleOutput, currConsoleOutput);
        } else {
            htmlString = "previous build not found";
        }
        return res;
    }

}
