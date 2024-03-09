package common;

import javax.swing.*;

import barnesHut.QuadTree;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Window {
    public boolean enabled = false;

    private static Window windowInstance = null;

    private JFrame window;
    private WindowCanvas canvas;

    private Window() {
        window = new JFrame("Simulation");
        window.setLayout(new BorderLayout());
        Vector2 windowSize = new Vector2(1280, 1280);
        canvas = new WindowCanvas(windowSize);
        canvas.setPreferredSize(new Dimension((int) windowSize.x, (int) windowSize.y));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // window.setResizable(false);

        window.add(canvas);
        window.pack();
        window.setVisible(true);
    }

    public static synchronized Window GetInstance() {
        if (windowInstance == null) {
            windowInstance = new Window();
        }

        return windowInstance;
    }

    public void LinkData(Body[] bodies, QuadTree quadTree) {
        canvas.bodies = bodies;
        canvas.quadTree = quadTree;
    }

    public void updateWindow() {
        canvas.repaint();
    }

    public void Close() {
        window.setVisible(false);
        window.dispose();
    }

    private class WindowCanvas extends JComponent {

        private Vector2 dimensions;
        public Body[] bodies;
        public QuadTree quadTree = null;

        public WindowCanvas(Vector2 dimensions) {
            this.dimensions = dimensions;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            // long startTime = System.nanoTime();
            Rectangle2D.Double rec = new Rectangle2D.Double(0, 0, dimensions.x, dimensions.y);
            g2d.setColor(Color.BLACK);
            g2d.fill(rec);

            drawBodies(g2d);
            if (quadTree != null && false)
                quadTree.Draw(g2d);

            // long endTime = System.nanoTime();
            // System.out.printf("Rendering: %d ms\n", (endTime - startTime) / 1000000);
        }

        private void drawBodies(Graphics2D g2d) {
            for (int i = 0; i < bodies.length; i++) {
                bodies[i].Draw(g2d);
            }
        }

    }

}
