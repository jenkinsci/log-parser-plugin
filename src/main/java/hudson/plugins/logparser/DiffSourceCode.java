package hudson.plugins.logparser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class DiffSourceCode {
	String svnURL;
	String username;
	String password;
	SVNRepository repository = null;
	
	public DiffSourceCode(String svnURL,String username, String password){
		this.svnURL=svnURL;
		this.username=username;
		this.password=password;
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
		} catch (SVNException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username , password );
		repository.setAuthenticationManager( authManager );
			 
	}
	public String getSvnURL() {
		return svnURL;
	}
	public void setSvnURL(String svnURL) {
		this.svnURL = svnURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public void checkoutRepository(String filePath) throws Exception{
		if (repository==null){
			 try{
	                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
			 }
			 catch (SVNException e) {
				 e.printStackTrace();
			 }
		}
	                //create authentication data
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(username, password);
		repository.setAuthenticationManager(authManager);

		// output some data to verify connection
		//System.out.println("Repository Root: "
			//	+ repository.getRepositoryRoot(true));
		//System.out.println("Repository UUID: "
				//+ repository.getRepositoryUUID(true));

		// need to identify latest revision
		long latestRevision = repository.getLatestRevision();
		//System.out.println("Repository Latest Revision: " + latestRevision);

		// create client manager and set authentication
		SVNClientManager ourClientManager = SVNClientManager.newInstance();
		ourClientManager.setAuthenticationManager(authManager);

		// use SVNUpdateClient to do the export
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);

		updateClient.doExport(repository.getLocation(), new File(filePath),
				SVNRevision.create(latestRevision),
				SVNRevision.create(latestRevision), null, true,
				SVNDepth.INFINITY);
	}
	public File getFileFromCheckOut(String filePath,String fileName) throws Exception{
		File svnDir=new File(filePath);
		if (!svnDir.isDirectory()){
			checkoutRepository(filePath);
		}
		Collection files=FileUtils.listFiles(svnDir, null, true);
		for(Iterator iterator = files.iterator();iterator.hasNext();){
			File file = (File) iterator.next();
			if (file.getName().equals(fileName)){
				return file;
			}
		}
		throw new Exception("file not found");
	}
		
	public void checkoutRepositoryRevision(String filePath,long revisionNumber) throws SVNException{
		if (repository==null){
			 try{
	                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
			 }
			 catch (SVNException e) {
				 e.printStackTrace();
			 }
		}
	                //create authentication data
		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(username, password);
		repository.setAuthenticationManager(authManager);

		// output some data to verify connection
		//System.out.println("Repository Root: "
			//	+ repository.getRepositoryRoot(true));
		//System.out.println("Repository UUID: "
				//+ repository.getRepositoryUUID(true));

		// need to identify latest revision
		//System.out.println("Repository Latest Revision: " + latestRevision);

		// create client manager and set authentication
		SVNClientManager ourClientManager = SVNClientManager.newInstance();
		ourClientManager.setAuthenticationManager(authManager);

		// use SVNUpdateClient to do the export
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);

		updateClient.doExport(repository.getLocation(), new File(filePath),
				SVNRevision.create(revisionNumber),
				SVNRevision.create(revisionNumber), null, true,
				SVNDepth.INFINITY);
		
	}
	public ArrayList<Long> showRevisionNumbersOfFile(String file) throws Exception{
		if (repository==null){
			 try{
	                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
			 }
			 catch (SVNException e) {
				 e.printStackTrace();
			 }
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username , password );
		repository.setAuthenticationManager( authManager );
		SVNNodeKind nodeKind = repository.checkPath(file , -1 );
		if ( nodeKind == SVNNodeKind.NONE ) {
			throw new Exception("file does not exist");
		}
		else{
			ArrayList<Long> revisions=null;
			repository.getFileRevisions(file, revisions, 0, -1);
			return revisions;
		}
	}
	public OutputStream getOutputStreamFromRepository(String file) throws Exception{
		if (repository==null){
			 try{
	                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
			 }
			 catch (SVNException e) {
				 e.printStackTrace();
			 }
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username , password );
		repository.setAuthenticationManager( authManager );
		SVNNodeKind nodeKind = repository.checkPath(file , -1 );
		if ( nodeKind == SVNNodeKind.NONE ) {
			throw new Exception("file does not exist");
		}
		else if (nodeKind==SVNNodeKind.DIR)
			throw new Exception("file a directory not a file");
		else{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SVNProperties fileProps = null;
			repository.getFile(file , -1 ,fileProps, baos );
			return baos;
		}
	}
	public OutputStream getOutputStreamFromRepositoryRevision(String file, long revisionNumber) throws Exception{
		if (repository==null){
			 try{
	                repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded( svnURL ) );
			 }
			 catch (SVNException e) {
				 e.printStackTrace();
			 }
		}
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username , password );
		repository.setAuthenticationManager( authManager );
		SVNNodeKind nodeKind = repository.checkPath(file , -1 );
		if ( nodeKind == SVNNodeKind.NONE ) {
			throw new Exception("file does not exist");
		}
		else if (nodeKind==SVNNodeKind.DIR)
			throw new Exception("file a directory not a file");
		else{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SVNProperties fileProps = null;
			repository.getFile(file , revisionNumber ,fileProps, baos );
			return baos;
		}
	}
}
