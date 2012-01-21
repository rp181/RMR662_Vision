/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package squaredetection;

import java.awt.Image;

/**
 *
 * @author Phani
 */
public class SquareDetection {

    /**
     * @param args the command line arguments
     */
    private static Thread t;

    public static void main(String[] args) {
        final Frame f = new Frame();
        f.setVisible(true);

        Runnable runnable = new Runnable() {

            public void run() {
                while (true) {
                    f.runProgram();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        };
        if (t == null) {
            t = new Thread(runnable);
            t.start();
        }
    }
}
