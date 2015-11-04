package hudson.plugins.logparser;

import java.io.File;
import java.util.ArrayList;

public class RootDiffArchive {
	File rootDir;
	String rootDirPath;
	ArrayList<String> Jobs;
	public RootDiffArchive() throws Exception{
		this.rootDir=new File(RootDiffArchiveConsts.ROOTDIR);
		this.rootDirPath=rootDir.getPath();
		this.Jobs=getJobs();
	}
	public ArrayList<String> getJobs() throws Exception{
		if (rootDir.exists()){
			if (rootDir.isDirectory()){
				Jobs = new ArrayList<String>();
				File[] filesInRootDir=rootDir.listFiles();
				for(File f:filesInRootDir){
					if (f.isDirectory())
						Jobs.add(f.getName());
				}
				return Jobs;
			}
			throw new Exception("rootDir is not a folder");
		}
		throw new Exception("rootDir does not exist");
	}
	public File getRootFolder(){
		return rootDir;
	}
	public String getRootFolderPath(){
		return rootDirPath;
	}
	public RootJob job(String name) throws Exception{
		RootJob job= new RootJob();
		return job.jobCreator(name);
	}
}

