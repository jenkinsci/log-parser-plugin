package hudson.plugins.logparser;
import java.util.*;

import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
public class RootSectionsDiff {
	int build1;
	int build2;
	HashMap<String, Diff> data = new LinkedHashMap<String, Diff>();
	
	class Diff {
		List<String> added = new ArrayList<String>();
		List<String> removed = new ArrayList<String>();
		List<String> modified = new ArrayList<String>();
	}
	
	public RootSectionsDiff(RootSections sections1, RootSections sections2) {
		this.build1 = sections1.build;
		this.build2 = sections2.build;
		
		List<String> allSectionNames = Arrays.asList(
				LogParserConsts.ERROR,
				LogParserConsts.WARNING,
				LogParserConsts.INFO
		);
		
		for (String sectionName : allSectionNames) {
			if (sections1.data.containsKey(sectionName) && sections2.data.containsKey(sectionName)) {
				List<String> build1Strings = sections1.data.get(sectionName);
				List<String> build2Strings = sections2.data.get(sectionName);
				this.data.put(sectionName, generateDiff(build1Strings, build2Strings));
			}
		}
	}
	
	private Diff generateDiff(List<String> base, List<String> comp) {
		Diff diff = new Diff();
		
		List<Delta> deltas = DiffUtils.diff(base, comp).getDeltas();
		for (Delta delta : deltas) {
			if (delta.getType() == TYPE.INSERT) {
				diff.added.addAll((List<String>) delta.getRevised().getLines());
			} else if (delta.getType() == TYPE.DELETE) {
				diff.removed.addAll((List<String>) delta.getOriginal().getLines());
			} else if (delta.getType() == TYPE.CHANGE) {
				diff.modified.addAll((List<String>) delta.getRevised().getLines());
			}
		}
				
		return diff;
	}

	public List<String> getDiffSections(boolean add, String section)
	{
		return (add)?this.data.get(section).added:this.data.get(section).removed;
	}
	
}
