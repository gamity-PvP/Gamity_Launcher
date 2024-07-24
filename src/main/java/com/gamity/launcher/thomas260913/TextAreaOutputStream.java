package com.gamity.launcher.thomas260913;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.IOException;

public class TextAreaOutputStream extends OutputStream {
    private final TextArea textArea;
    private final StringBuilder lineBuffer = new StringBuilder();

    public TextAreaOutputStream(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            String line = lineBuffer.toString() + '\n';
            lineBuffer.setLength(0);
            Platform.runLater(() -> textArea.appendText(line));
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
