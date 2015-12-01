1. LogParserResults.java

   Add Maps for totalCount and linksFile for extra tags, add its associated getters and setters.
   
   Add Set for extra tags and its getter and setter
      
2. LogParserUtils.java

   1. standardizeStatus(final String status)
   
      The legal status condition check is removed so that arbitrary status can come through
      
   2. compilePatterns(final String[] parsingRulesArray, final Logger logger)
   
      The extra tags are extracted and set in CompliedPatterns result
      
3. LogParserParser.java

   1. Add extra tags to statusCount, linkFiles, writers
   
   2. parseLog(final Run<?, ?> build)
   
      Add link file paths for extra paths
      
      Close file writer for extra tags
      
      Pass in extra tags for writeReferenceHtml(...)
      
      Set totalCount, linksFile and extraTag in LogParserResult for extra tags
      
   3. colorLine(final String line, final String status)
   
      Set color as default if the tag is extra
      
4. CompliedPatterns.java

   Add List for extra tags
   
5. LogParserDisplayConsts.java

   Add default color, icon, link list display and link list display plural for extra tags
   
6. LogParserWriter.java

   1. writeReferenceHtml(...)
   
      Add an additional parameter for extra tags
      
      Also write links for extra tags
      
   2. writeLinks(...)
   
      Set icon, link list display and link list display plural as default if the tag is extra
      
7. LogParserAction.java
   
   1. buildDataset()
   
      Add extra tags to dataset as well