package org.jenkinsci.plugins.logparser;

import hudson.FilePath;
import hudson.Functions;
import hudson.plugins.logparser.LogParserAction;
import hudson.plugins.logparser.LogParserPublisher;
import hudson.slaves.DumbSlave;
import hudson.tasks.Maven;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.ToolInstallations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * In this test suite we initialize the Job workspaces with a resource (maven-project1.zip) that contains a Maven
 * project.
 */
public class LogParserWorkflowTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    private static LogParserAction result;

    @BeforeClass
    public static void init() throws Exception {
        Maven.MavenInstallation mavenInstallation = ToolInstallations.configureMaven35();
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "logParserPublisherWorkflowStep");
        DumbSlave agent = jenkinsRule.createOnlineSlave();
        FilePath workspace = agent.getWorkspaceFor(job);
        assertNotNull(workspace);
        Path projectZip = zipProjectDir();
        try (InputStream is = Files.newInputStream(projectZip)){
            workspace.unzipFrom(is);
        }
        job.setDefinition(new CpsFlowDefinition(""
                       + "node('" + agent.getNodeName() + "') {\n"
                       + "  def mvnHome = tool '" + mavenInstallation.getName() + "'\n"
                       + "  " + (Functions.isWindows() ? "bat" : "sh") + " \"${mvnHome}/bin/mvn clean install\"\n"
                       + "  step([$class: 'LogParserPublisher', projectRulePath: 'logparser-rules.txt', useProjectRule: true])\n"
                       + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        result = job.getLastBuild().getAction(LogParserAction.class);
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for success.
     */
    @Test
    public void logParserPublisherWorkflowStep() throws Exception {
       assertEquals(0, result.getResult().getTotalErrors());
       assertEquals(2, result.getResult().getTotalWarnings());
       assertEquals(0, result.getResult().getTotalInfos());
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for number of debug tags
     */
    @Test
    public void logParserPublisherWorkflowStepDebugTags() throws Exception {
        assertEquals(0, result.getResult().getTotalDebugs());
    }

    /**
     * Run a workflow job using {@link LogParserPublisher} and check for number of example arbitrary tags
     */
    @Test
    public void logParserPublisherWorkflowStepArbitraryTags() throws Exception {
        assertEquals(0, result.getResult().getTotalCountsByExtraTag("jenkins"));
        assertEquals(1, result.getResult().getTotalCountsByExtraTag("logParserPublisherWorkflowStep"));
    }

    /**
     * Generates a zip file from the project structure checked into
     *
     * <pre>src/test/resources/org/jenkinsci/plugins/logparser/maven-project1</pre>
     *
     * Keeps test behavior the same, but allows for clearer changes than checking
     * in a zip file.
     *
     * @return Path to newly-generated zip file
     * @throws IOException if error creating temp directory, zip file, or walking tree
     */
    private static Path zipProjectDir() throws IOException {
        URL projectDir = LogParserWorkflowTest.class.getResource("./maven-project1");
        assertNotNull(projectDir);
        Path projectZip = Files.createTempDirectory("log-parser-workflow-test")
                .resolve("maven-project1.zip")
                .toAbsolutePath();
        URI zip = URI.create("jar:file:" + projectZip);
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        Path projectRoot = Paths.get(URI.create(projectDir.toString()));
        try (FileSystem zipFs = FileSystems.newFileSystem(zip, env)) {
            Files.walkFileTree(projectRoot, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relative = projectRoot.relativize(dir);
                    Files.createDirectories(zipFs.getPath(relative.toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relative = projectRoot.relativize(file);
                    Path target = zipFs.getPath(relative.toString());
                    Files.copy(file, target);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return projectZip;
    }
}
