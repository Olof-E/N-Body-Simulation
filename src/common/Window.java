package common;

import javax.swing.*;

import barnesHut.QuadTree;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Window {
    public boolean enabled = false;
    public boolean debugMode = false;

    private static Window windowInstance = null;

    private JFrame window;
    private WindowCanvas canvas;

    private Window() {
    }

    // Initialize the window and its components
    public void Init(Vector2 windowDims, boolean debugMode) {
        this.debugMode = debugMode;
        window = new JFrame("Simulation");
        window.setLayout(new BorderLayout());
        canvas = new WindowCanvas(windowDims);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        window.add(canvas);
        window.pack();
        window.setVisible(true);
    }

    // Singelton
    public static Window GetInstance() {
        if (windowInstance == null) {
            windowInstance = new Window();
        }

        return windowInstance;
    }

    // Link simulation data to window
    public void LinkData(Body[] bodies, QuadTree quadTree) {
        canvas.bodies = bodies;
        canvas.quadTree = quadTree;
    }

    // Redraw the window
    public void updateWindow() {
        canvas.repaint();
    }

    // Close and cleanup the window
    public void Close() {
        window.setVisible(false);
        window.dispose();
    }

    private class WindowCanvas extends JComponent {

        private Vector2 dimensions;
        public Body[] bodies;
        public QuadTree quadTree = null;

        // Set up the canvas area
        public WindowCanvas(Vector2 dimensions) {
            this.dimensions = dimensions;
            setPreferredSize(new Dimension((int) dimensions.x, (int) dimensions.y));
        }

        // Draw the canvas
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            // Draw background
            Rectangle2D.Double rec = new Rectangle2D.Double(0, 0, dimensions.x, dimensions.y);
            g2d.setColor(Color.BLACK);
            g2d.fill(rec);

            // Draw all bodies
            for (int i = 0; i < bodies.length; i++) {
                bodies[i].Draw(g2d);
            }

            // Draw the quad tree if in debug mode
            if (quadTree != null && debugMode)
                quadTree.Draw(g2d);

        }
    }

}
