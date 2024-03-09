package barnesHut;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import common.*;

public class QuadTree {
    public Cell root;

    public QuadTree(Vector2 windowDims) {
        root = new Cell(new Vector2(windowDims.x / 2.0, windowDims.y / 2.0), windowDims.x);
    }

    public void Insert(Body body) {
        root.Insert(body);
    }

    public void ComputePseudoBodies() {
        root.ComputePseudoBodies();
    }

    public Vector2 calculateForce(Body body) {
        return root.calculateForce(body);
    }

    public void Draw(Graphics2D g2d) {
        root.Draw(g2d);
    }

    public void Reset() {
        Vector2 pos = root.pos;
        double width = root.width;

        root = new Cell(pos, width);
    }

    private class Cell {
        public Cell[] children;
        public Body body;

        public Vector2 pos = null;
        public double width;

        public Vector2 centerOfMass = null;
        public double mass = 0;

        public int bodyCount = 0;

        public Cell(Vector2 pos, double width) {
            children = new Cell[] { null, null, null, null };

            this.pos = pos;
            this.width = width;
        }

        public boolean Contains(Body body) {
            Vector2 p = body.position;
            return p.x <= pos.x + width / 2.0
                    && p.x >= pos.x - width / 2.0
                    && p.y >= pos.y - width / 2.0
                    && p.y <= pos.y + width / 2.0;
        }

        public void Insert(Body newBody) {
            if (bodyCount > 1) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].Contains(newBody)) {
                        children[i].Insert(newBody);
                        break;
                    }
                }
            } else if (this.body != null) {
                if (false) {
                    if (this.body == null)
                        this.body = newBody;
                    return;
                } else {
                    children[0] = new Cell(new Vector2(pos.x - width / 4.0, pos.y - width / 4.0), width / 2.0);
                    children[1] = new Cell(new Vector2(pos.x + width / 4.0, pos.y - width / 4.0), width / 2.0);
                    children[2] = new Cell(new Vector2(pos.x - width / 4.0, pos.y + width / 4.0), width / 2.0);
                    children[3] = new Cell(new Vector2(pos.x + width / 4.0, pos.y + width / 4.0), width / 2.0);

                    if (children[0].Contains(this.body))
                        children[0].Insert(this.body);
                    else if (children[1].Contains(this.body))
                        children[1].Insert(this.body);
                    else if (children[2].Contains(this.body))
                        children[2].Insert(this.body);
                    else if (children[3].Contains(this.body))
                        children[3].Insert(this.body);

                    this.body = null;

                    if (children[0].Contains(newBody))
                        children[0].Insert(newBody);
                    else if (children[1].Contains(newBody))
                        children[1].Insert(newBody);
                    else if (children[2].Contains(newBody))
                        children[2].Insert(newBody);
                    else if (children[3].Contains(newBody))
                        children[3].Insert(newBody);
                }
            } else {
                this.body = newBody;
            }
            this.bodyCount++;
        }

        public void ComputePseudoBodies() {
            Vector2 aggregate = new Vector2();
            double totalMass = 0;

            if (bodyCount == 1) {
                centerOfMass = body.position;
                mass = body.mass;
            } else {
                for (int i = 0; i < children.length; i++) {
                    if (children[i] != null && children[i].bodyCount > 0) {
                        children[i].ComputePseudoBodies();
                        aggregate = Vector2.add(aggregate,
                                Vector2.mul(children[i].centerOfMass,
                                        children[i].mass));
                        totalMass += children[i].mass;
                    }
                }
                centerOfMass = Vector2.div(aggregate, totalMass);

                mass = totalMass;
            }

        }

        private Vector2 scaleDown(Vector2 vec) {
            return new Vector2((vec.x / Simulation.SIM_RADIUS) * 1280, (vec.y / Simulation.SIM_RADIUS) * 1280);
        }

        public Vector2 calculateForce(Body otherBody) {
            Vector2 force = new Vector2();

            if (this.bodyCount == 1) {
                if (otherBody == this.body)
                    return new Vector2();
                Vector2 scaledVecI = otherBody.position;
                Vector2 scaledVecJ = centerOfMass;

                double dist = Vector2.dist(centerOfMass, otherBody.position);
                double mag = (Simulation.G_CONSTANT * mass * otherBody.mass) / dist * dist;
                Vector2 dir = Vector2.sub(scaledVecJ, scaledVecI);

                force = new Vector2(mag * dir.x / dist, mag * dir.y / dist);
            } else if (this.bodyCount != 0) {
                double r = Vector2.dist(otherBody.position, centerOfMass);
                double D = width;

                if (D / r < 0.6) {
                    Vector2 scaledVecI = otherBody.position;
                    Vector2 scaledVecJ = centerOfMass;

                    double dist = Vector2.dist(centerOfMass, otherBody.position);
                    double mag = (Simulation.G_CONSTANT * mass * otherBody.mass) / dist * dist;
                    Vector2 dir = Vector2.sub(scaledVecJ, scaledVecI);

                    force = new Vector2(mag * dir.x / dist, mag * dir.y / dist);
                } else {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i] != null) {
                            force = Vector2.add(force, children[i].calculateForce(otherBody));
                        }
                    }
                }
            }
            // System.out.printf("(%6f.3, %6f.3)\n", force.x, force.y);
            return force;
        }

        public void Draw(Graphics2D g2d) {
            Vector2 screenPos = scaleDown(this.pos);
            double screenWidth = (this.width / Simulation.SIM_RADIUS) * 1280;
            Rectangle2D quad = new Rectangle2D.Double(screenPos.x - screenWidth / 2.0, screenPos.y - screenWidth / 2.0,
                    screenWidth, screenWidth);
            if (centerOfMass != null) {
                Vector2 centOfMassScreen = scaleDown(centerOfMass);
                g2d.setColor(Color.red);
                double dotWidth = Math.exp(screenWidth / (0.6 * 1280)) * 7.5;
                Ellipse2D center = new Ellipse2D.Double(centOfMassScreen.x - dotWidth / 2.0,
                        centOfMassScreen.y - dotWidth / 2.0, dotWidth, dotWidth);
                g2d.draw(center);
            }

            g2d.setColor(Color.GREEN);
            g2d.draw(quad);

            for (int i = 0; i < children.length; i++) {
                if (children[i] != null)
                    children[i].Draw(g2d);
            }
        }
    }
}
