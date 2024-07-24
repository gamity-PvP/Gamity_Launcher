package fr.gamity.launcher.thomas260913;

import java.io.OutputStream;
import java.io.IOException;

public class BufferedOutputStream extends OutputStream {
    private final StringBuilder buffer;
    private final StringBuilder lineBuffer = new StringBuilder();

    public BufferedOutputStream(StringBuilder buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            buffer.append(lineBuffer.toString()).append('\n');
            lineBuffer.setLength(0);
        } else {
            lineBuffer.append((char) b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = off; i < off + len; i++) {
            write(b[i]);
        }
    }
}