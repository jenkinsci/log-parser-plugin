package hudson.plugins.logparser;
import java.io.*;

public final class ReadWriteTextFile {

	private ReadWriteTextFile() {
		// to suppress PMD warning
	}
	
  static public String getContents(final File aFile) {
    final StringBuilder contents = new StringBuilder();
    
    try {
      final BufferedReader input =  new BufferedReader(new FileReader(aFile));
      try {
        String line = null; //not declared within while loop
        while (( line = input.readLine()) != null){
          contents.append(line+"\n");
        }
      }
      finally {
        input.close();
      }
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    
    return contents.toString();
  }

  static public void setContents(final File aFile, final String aContents)
                                 throws FileNotFoundException, IOException {
    if (aFile == null) {
      throw new IllegalArgumentException("File should not be null.");
    }
    if (!aFile.exists()) {
    	aFile.createNewFile();
    }
    if (!aFile.isFile()) {
      throw new IllegalArgumentException("Should not be a directory: " + aFile);
    }
    if (!aFile.canWrite()) {
      throw new IllegalArgumentException("File cannot be written: " + aFile);
    }

    //use buffering
    final Writer output = new BufferedWriter(new FileWriter(aFile));
    try {
      //FileWriter always assumes default encoding is OK!
      output.write( aContents );
    }
    finally {
      output.close();
    }
  }

} 

