package hudson.plugins.logparser;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogParserPublisherTest {
    @Mock
    private LogParserPublisher.DescriptorImpl descriptor;
    @Mock
    private Run<?, ?> run;
    @Mock
    private Launcher launcher;
    @Mock
    private TaskListener listener;
    @Captor
    private ArgumentCaptor<LogParserAction> actionCaptor;

    @Test
    void shouldSetFailedToParseErrorOnNullParsingRulesPath(@TempDir File workspace) throws IOException, InterruptedException {
        LogParserPublisher publisher = new LogParserPublisher(false, null, null) {
            @Override
            public BuildStepDescriptor<Publisher> getDescriptor() {
                return descriptor;
            }
        };

        publisher.perform(run, new FilePath(workspace), launcher, listener);

        verify(run).setResult(Result.ABORTED);
        verify(run).addAction(actionCaptor.capture());

        LogParserAction actual = actionCaptor.getValue();
        assertThat(actual.getResult().getFailedToParseError()).isEqualTo(LogParserPublisher.NULL_PARSING_RULES);
    }

}