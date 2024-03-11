package common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Body {
    public Vector2 position;
    public Vector2 velocity;
    public Vector2 force;

    public double mass;

    // Initialize body
    public Body(Vector2 position, double mass) {
        this.position = position;
        this.mass = mass;
        velocity = new Vector2();
        force = new Vector2();
    }

    // Convert simulation space to screen space
    private Vector2 scaleDown(Vector2 vec) {
        return new Vector2((vec.x / Simulation.SIM_RADIUS) * 1280, (vec.y / Simulation.SIM_RADIUS) * 1280);
    }

    // Draw body to canvas
    public void Draw(Graphics2D g2d) {
        double size = Math.max((mass / 10.97219e24) * 6, 2.5);

        g2d.setColor(Color.CYAN);

        Vector2 screenPos = scaleDown(position);

        Ellipse2D.Double ell = new Ellipse2D.Double(screenPos.x - size / 2.0,
                screenPos.y - size / 2.0, size, size);
        g2d.fill(ell);
    }
}
