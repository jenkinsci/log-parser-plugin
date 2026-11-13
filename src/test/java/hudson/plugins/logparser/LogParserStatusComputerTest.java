package hudson.plugins.logparser;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogParserStatusComputerTest {

    private static final String[] RULES = {"my-rule"};
    private static final Pattern[] PATTERNS = {Pattern.compile("ERROR")};

    @Test
    void shouldCloseLogAfterParsing() {
        CloseTrackingInputStream log = new CloseTrackingInputStream(
                new ByteArrayInputStream("first line\nERROR in second line\n".getBytes(StandardCharsets.UTF_8)));
        LogParserStatusComputer computer = new LogParserStatusComputer(
                log, RULES, PATTERNS, "LogParserStatusComputerTest_build_1", StandardCharsets.UTF_8);

        HashMap<String, String> actual = computer.call();

        assertThat(actual).containsEntry("1", "my-rule");
        assertThat(log.closed).isTrue();
    }

    @Test
    void shouldCloseLogWhenReadingIsInterrupted() {
        CloseTrackingInputStream log = new CloseTrackingInputStream(new InterruptedInputStream());
        LogParserStatusComputer computer = new LogParserStatusComputer(
                log, RULES, PATTERNS, "LogParserStatusComputerTest_build_2", StandardCharsets.UTF_8);

        assertThatThrownBy(computer::call).isInstanceOf(RuntimeException.class);
        assertThat(log.closed).isTrue();
    }

    private static final class CloseTrackingInputStream extends FilterInputStream {
        private boolean closed;

        CloseTrackingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    /**
     * Behaves like the agent side of a pipe whose reading thread is interrupted.
     */
    private static final class InterruptedInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            throw new InterruptedIOException("interrupted");
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            throw new InterruptedIOException("interrupted");
        }
    }
}
