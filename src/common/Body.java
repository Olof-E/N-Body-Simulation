package common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Body {
    public Vector2 position;
    public Vector2 velocity;
    public Vector2 force;

    public double mass;

    public Body(Vector2 position, double mass) {
        this.position = position;
        this.mass = mass;
        velocity = new Vector2();
        force = new Vector2();
    }

    public void Draw(Graphics2D g2d) {
        double size = Math.max((mass / 10.97219e24) * 5, 2.5);

        g2d.setColor(Color.CYAN);
        if (size > 20) {
            g2d.setColor(Color.YELLOW);
            size = 20;
        }

        Ellipse2D.Double ell = new Ellipse2D.Double((position.x / Simulation.SIM_RADIUS) * 1280 - size / 2.0,
                (position.y / Simulation.SIM_RADIUS) * 1280 - size / 2.0, size, size);
        g2d.fill(ell);
    }
}
