package hudson.plugins.logparser;

import java.io.File;
import java.util.ArrayList;

public class RootBuild {
	int buildNumber;
	File buildFolder;
	File configFile;
	File consoleFile;
	File pomFile;
	File testFile;
	public RootBuild(){
		
	}
	public RootBuild buildCreator(File jobFolder, int buildNumber) throws Exception{
		if (!jobFolder.exists())
			throw new Exception("job does not exist");
		else{
			String buildFolderName="build"+buildNumber;
			File buildFolder=new File(jobFolder.getPath()+File.separator+buildFolderName);
			if (!buildFolder.exists()){
				throw new Exception("build does not exist");
			}
			else{
				configFile=new File(buildFolder.getPath()+File.separator+"config.xml"); //move config.xml to consts
				consoleFile=new File(buildFolder.getPath()+File.separator+"console.txt");
				pomFile=new File(buildFolder.getPath()+File.separator+"pom.xml");
				testFile=new File(buildFolder.getPath()+File.separator+"test.txt");
				return new RootBuild(buildNumber, buildFolder,configFile, consoleFile, pomFile, testFile);
			}
		}
	}
	private RootBuild(int buildNumber, File buildFolder, File configFile, File consoleFile, File pomFile, File testFile) {
		this.buildNumber=buildNumber;
		this.configFile=configFile;
		this.consoleFile=consoleFile;
		this.pomFile=pomFile;
		//this.testFile=testFile;
	}
	public int getBuildNumber(){
		return buildNumber;
	}
	public File getBuildFolder(){
		return buildFolder;
	}
	public File getConfigFile(){
		return configFile;
	}
	public File getConsoleFile(){
		return consoleFile;
	}
	public File getPomFile(){
		return pomFile;
	}
	public File testFile(){
		return testFile;
	}
	public ArrayList<File> getFiles(){
		ArrayList<File> files= new ArrayList<File>();
		files.add(consoleFile);
		files.add(configFile);
		files.add(pomFile);
		//files.add(testFile);
		return files;
	}
}
