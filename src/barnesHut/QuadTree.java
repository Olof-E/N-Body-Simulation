package barnesHut;

import java.awt.Color;
import java.awt.Graphics2D;
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

    public void Draw(Graphics2D g2d) {
        root.Draw(g2d);
    }

    private class Cell {
        public Cell[] children;
        public Body body;

        public Vector2 pos = null;
        public double width;

        // public Vector2 centerOfMass;
        // public double mass;

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
                    else
                        children[3].Insert(this.body);

                    this.body = null;

                    if (children[0].Contains(newBody))
                        children[0].Insert(newBody);
                    else if (children[1].Contains(newBody))
                        children[1].Insert(newBody);
                    else if (children[2].Contains(newBody))
                        children[2].Insert(newBody);
                    else
                        children[3].Insert(newBody);
                }
            } else {
                this.body = newBody;
            }
            this.bodyCount++;
        }

        public void Draw(Graphics2D g2d) {
            Rectangle2D quad = new Rectangle2D.Double(this.pos.x - this.width / 2.0, this.pos.y - this.width / 2.0,
                    this.width, this.width);
            g2d.setColor(Color.GREEN);
            g2d.draw(quad);

            for (int i = 0; i < children.length; i++) {
                if (children[i] != null)
                    children[i].Draw(g2d);
            }
        }
    }
}
