/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicUserInterface;

import java.io.IOException;
import javax.swing.JTextArea;
import java.io.Writer;

/**
 *
 * @author feffo
 */
class JTextAreaWriter extends Writer {

    private JTextArea textArea;

    public JTextAreaWriter(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            textArea.append(cbuf[i] + "");
        }
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }
}
