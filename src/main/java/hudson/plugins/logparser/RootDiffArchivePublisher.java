package hudson.plugins.logparser;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.io.Files;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import hudson.model.Run;
import hudson.model.Descriptor.FormException;
import hudson.plugins.logparser.LogParserPublisher.DescriptorImpl;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
public class RootDiffArchivePublisher extends Recorder implements SimpleBuildStep{

	@DataBoundConstructor
	public RootDiffArchivePublisher(){
		
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		// TODO Auto-generated method stub
		return BuildStepMonitor.NONE;
	}

	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher,
			TaskListener listener) throws InterruptedException, IOException {
		File buildFolder = null;
		try {
			buildFolder = createBuildFolder(run);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Successfully reached diffArchivePublisher");
		saveConfigFile(buildFolder,run);
		savePomFile(buildFolder, run);
		saveConsoleFile(buildFolder,run);
		saveTestFile(buildFolder,run);
	}

	private File createBuildFolder(Run<?, ?> run) throws Exception { //doesn't work.
		int buildNumber = run.getNumber();
		String folderName="build"+buildNumber;
		String projectName=run.getParent().getName(); //hopefully get the name of the job
		File f=new File(RootDiffArchiveConsts.ROOTDIR+"/"+projectName+"/"+folderName);
		f.mkdirs();
		return f;
		
	
	}

	private void saveConfigFile(File buildFolder, Run<?, ?> run) {
		XmlFile configFile=run.getParent().getConfigFile(); // should get the jobs config file
		File originalConfig=configFile.getFile();
		File archiveConfigFile=new File(buildFolder.getPath()+File.separator+"config.xml");
		try {
			Files.copy(originalConfig,archiveConfigFile);
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new RuntimeException("Unable to copy and create a config file");
		}
	}

	private void savePomFile(File buildFolder, Run<?, ?> run) {
		//File pomFile=null; //still looking up how to get. Get from run to job to environment to maven module set. 
		File originalPom = new File(run.getParent()+"workspace/pom.xml");
		File archivePomFile = new File(buildFolder.getPath()+File.separator+"pom.xml");
		try {
			Files.copy(originalPom,archivePomFile);
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new RuntimeException("Unable to copy and create a config file");
		}
		//MavenModuleSet.getRootPom(EnvVars env);
		//run.getParent();
		
	}

	private void saveConsoleFile(File buildFolder, Run<?, ?> run) {
		File originalConsole=run.getLogFile();
		File archiveConsole=new File(buildFolder.getPath()+File.separator+"console.txt");
		try {
			Files.copy(originalConsole,archiveConsole);
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new RuntimeException("Unable to copy and create a config file");
		}
		
	}

	private void saveTestFile(File buildFolder, Run<?,?> run) {
		//need clarification of what the test cases are and where are they located. Are they part of the console log. 
		//if it is should I make a new file with just the test cases.
		
	}
	@Override
	public DescriptorImpl getDescriptor() {
	        return (DescriptorImpl) super.getDescriptor();
	}
	@Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		@SuppressWarnings("rawtypes")
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

		@Override
		public String getDisplayName() {
			// TODO Auto-generated method stub
			return "DiffArchive";
		}  
    }

}
