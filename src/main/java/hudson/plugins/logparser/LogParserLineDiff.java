package hudson.plugins.logparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class LogParserLineDiff {
	
	String htmlString = null;
	
	public StringBuilder linesToBlock(List<?> list){
		StringBuilder sb = new StringBuilder();
		for(Object object : list){
			sb.append(object + "\n");
		}
		if (sb.length() >= 1)
			sb.deleteCharAt(sb.length() - 1); // last "\n"
		return sb;
	}
	
	public int lineDiff(List<String> original, List<String> revised){
		String[] colors = new String[] { "palegreen", "khaki", "pink",
				"moccasin", "lightskyblue", "lightyellow", "coral",
				"aliceblue", "yellowgreen", "beige", "lightpink" };
		
		StringBuilder originalText = new StringBuilder();
		StringBuilder revisedText = new StringBuilder();
		
		Patch patch = DiffUtils.diff(original, revised);
		List<Delta> deltas = patch.getDeltas();
		
		int colorIndex = 0;
		int last = -1;
		
		for (Delta delta : deltas){
			
			if (last + 1 < delta.getOriginal().getPosition()){
				
				originalText.append("<pre style='font-size:smaller;'>\n");
				revisedText.append("<pre style='font-size:smaller;'>\n");
				
				for (int i = last + 1; i < delta.getOriginal().getPosition(); i++) {
					originalText.append(original.get(i) + "\n");
					revisedText.append(original.get(i) + "\n");
				}
				
				originalText.append("</pre>\n");
				revisedText.append("</pre>\n");
			}
			
			List<?> or = delta.getOriginal().getLines();
			originalText.append("<pre style='background-color:" + colors[colorIndex] + ";'>\n"
					+ linesToBlock(or) + "\n</pre>");
			List<?> re = delta.getRevised().getLines();
			revisedText.append("<pre style='background-color:" + colors[colorIndex] + ";'>\n"
					+ linesToBlock(re) + "\n</pre>");
			colorIndex = (colorIndex < colors.length) ? colorIndex + 1 : 0;
			last = delta.getOriginal().last();
		}
		
		if (last + 1 < original.size()) { // last is not delta
			originalText.append("<pre style='font-size:smaller;'>\n");
			revisedText.append("<pre style='font-size:smaller;'>\n");
			for (int i = last + 1; i < original.size(); i++) {
				originalText.append(original.get(i) + "\n");
				revisedText.append(original.get(i) + "\n");
			}
			originalText.append("</pre>\n");
			revisedText.append("</pre>\n");
		}
		
		htmlString = "<html><table><tr><td style='vertical-align:top;'>"
				+ originalText.toString() + "</td><td style='vertical-align:top;'>"
				+ revisedText.toString() + "</td></tr></table></html>";
		
		return deltas.size();
	}
	
	public void writeToFile(String savePath){
		PrintWriter writer;
		try {
			writer = new PrintWriter(savePath, "UTF-8");
			writer.println(htmlString);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public int lineDiff(String prevPath, String currPath, String savePath) throws IOException{
		int res = -1;
		if(prevPath.length() != 0){
			BufferedReader prevReader = new BufferedReader(new FileReader(
					prevPath));
			List<String> prevConsoleOutput = new ArrayList<String>();
			String line = "";
			while((line = prevReader.readLine()) != null){
				prevConsoleOutput.add(line);
			}
			prevReader.close();
			
			BufferedReader currReader = new BufferedReader(new FileReader(
					currPath));
			List<String> currConsoleOutput = new ArrayList<String>();
			while((line = currReader.readLine()) != null){
				currConsoleOutput.add(line);
			}
			currReader.close();
			res = lineDiff(prevConsoleOutput, currConsoleOutput);
		}else{
			htmlString = "previous build not found";
		}
		writeToFile(savePath);
		return res;
	}

}
