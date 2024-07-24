package fr.gamity.launcher.thomas260913;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultiOutputStream extends OutputStream {
    private List<OutputStream> outputStreams = new ArrayList<>();

    public void addOutputStream(OutputStream outputStream) {
        outputStreams.add(outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.close();
        }
    }
}

