import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Window {
    private JFrame window;
    private WindowCanvas canvas;

    public void CreateWindow(Body[] bodies) {
        window = new JFrame("Simulation");
        Vector2 windowSize = new Vector2(1280, 1280);
        canvas = new WindowCanvas(windowSize, bodies);
        window.setSize((int) windowSize.x, (int) windowSize.y);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        window.add(canvas);
        window.setVisible(true);
    }

    public class WindowCanvas extends JComponent {

        private Vector2 dimensions;
        private Body[] bodies;

        public WindowCanvas(Vector2 dimensions, Body[] bodies) {
            this.dimensions = dimensions;
            this.bodies = bodies;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            long startTime = System.nanoTime();
            Rectangle2D.Double rec = new Rectangle2D.Double(0, 0, dimensions.x, dimensions.y);
            g2d.setColor(Color.BLACK);
            g2d.fill(rec);

            drawBodies(g2d);

            long endTime = System.nanoTime();
            // System.out.printf("Rendering: %d ms\n", (endTime - startTime) / 1000000);
        }

        private void drawBodies(Graphics2D g2d) {
            for (int i = 0; i < bodies.length; i++) {
                bodies[i].draw(g2d);
            }
        }

    }

    public void updateWindow() {
        canvas.repaint();
    }

    public void Close() {
        window.setVisible(false);
        window.dispose();
    }
}
