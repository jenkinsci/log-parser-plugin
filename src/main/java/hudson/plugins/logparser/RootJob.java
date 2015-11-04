package hudson.plugins.logparser;

import java.io.File;
import java.util.ArrayList;

public class RootJob {
	ArrayList<Integer> buildInts;
	File jobFolder;
	String jobPath;
	public RootJob(){
		
	}
	public RootJob jobCreator (String name) throws Exception{
		jobFolder=new File(RootDiffArchiveConsts.ROOTDIR+File.separator+name);
		jobPath=jobFolder.getPath();
		if (!jobFolder.exists()){
			throw new Exception("job does not exist");
		}
		else{
			buildInts=getBuilds(jobFolder);		
		}
		return new RootJob(jobFolder,jobPath,buildInts);
	}
	private RootJob(File jobFolder,String jobPath, ArrayList<Integer> buildInts){
		this.buildInts=buildInts;
		this.jobPath=jobPath;
		this.jobFolder=jobFolder;
	}
	public ArrayList<Integer> getBuilds(){
		return getBuilds(jobFolder);
	}
	private ArrayList<Integer> getBuilds(File jobFolder){
		File[] builds=jobFolder.listFiles();
		ArrayList<Integer> buildNumbers = new ArrayList<Integer>();
		for(File f:builds){
			String build=f.getName();
			if (f.isDirectory()){
				int buildNumber=Integer.parseInt(build.substring(5));
				buildNumbers.add(buildNumber);
			}
		}
		return buildNumbers;
	}
	public String getJobPath(){
		return jobPath;
	}
	public File getJobFolder(){
		return jobFolder;
	}
	public RootBuild build(int buildNumber) throws Exception{
		return buildHelper(jobFolder,buildNumber);
	}
	private RootBuild buildHelper(File jobFolder, int buildNumber) throws Exception {
		RootBuild b = new RootBuild();
		return b.buildCreator(jobFolder,buildNumber);
	}
	

}
