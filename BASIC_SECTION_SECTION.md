1. LogParserResults.java
	1. Added basicInfo and basicLinksFile for the basic section and their associated getters and 	setters.
	
2. LogParserParser.java
	1. Add basic tag to statusCount, linkFiles, writers
	2. parseLog(final Run<?, ?> build)
		Add Link file paths for extra paths
		Identify lines that are basic with the method checkBasicInfoLine(String line)
		Set basicInfos,linkFiles, and basic Tag in log parser result.
		
3. LogParserDisplayConstants
	1. Add color and gif for basic tags

4. LogParserConsts
	1. Added an array of srings that identify the basic info lines.
	