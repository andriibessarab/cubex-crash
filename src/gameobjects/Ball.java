package gameobjects;

import gameobjects.bricks.Brick;
import gameobjects.bricks.TriangleBrick;
import screens.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Ball extends GameObject {
    private int speedX; // Speed of the ball along the X-axis
    private int speedY; // Speed of the ball along the Y-axis
    private Color color; // Color of the ball
    private final int parentWidth; // Width of the game area
    private final int parentHeight; // Height of the game area

    private final ImageIcon img;

    public Ball(int x, int y, int diameter, int speedX, int speedY, Color color, int parentWidth, int parentHeight) {
        super(x, y, diameter, diameter); // Width and height are equal for a ball (diameter)
        this.speedX = speedX;
        this.speedY = speedY;
        this.color = color;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;

        img = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/game/ball.gif")));
    }

    @Override
    public void update() {
        // Update the ball's position based on its speed
        x += speedX;
        y += speedY;

        // Collision detection with the borders
        if (x <= GameScreen.GAME_X + 5 || x >= GameScreen.GAME_X + GameScreen.GAME_WIDTH - width - 5) {
            speedX = -speedX; // Reverse direction on X-axis
            x += speedX;
        }
        if (y <= GameScreen.GAME_Y + 5) {
            speedY = -speedY; // Reverse direction on Y-axis
            y += speedY;
        }

        // Never let y or x speed be 0 to avoid getting stuck
//        if (speedX == 0) {
//            speedX = 1;
//        }
//        if (speedY == 0) {
//            speedY = 1;
//        }
    }

    public boolean isCollidingWith(Brick o) {
        if (o instanceof TriangleBrick) {
            return isCollidingWithTriangle((TriangleBrick) o);
        }

        // Check if the ball is colliding with the brick ensure the ball doesn tget stuck
        if(x + width >= o.getX() && x <= o.getX() + o.getWidth() && y + height >= o.getY() && y <= o.getY() + o.getHeight()) {
            return true;
        }

        return false;
    }

    public void bounceOff(Brick o) {
        // Calculate the center coordinates of the ball
        double ballCenterX = x + width / 2;
        double ballCenterY = y + height / 2;

        // Calculate the center coordinates of the brick
        double brickCenterX = o.getX() + o.getWidth() / 2;
        double brickCenterY = o.getY() + o.getHeight() / 2;

        // Calculate the half-width and half-height of the brick
        double brickHalfWidth = o.getWidth() / 2;
        double brickHalfHeight = o.getHeight() / 2;

        // Calculate the distance between the centers of the ball and the brick
        double dx = ballCenterX - brickCenterX;
        double dy = ballCenterY - brickCenterY;

        // Check for collision with the top or bottom of the brick
        if (Math.abs(dx) < brickHalfWidth + width / 2 && Math.abs(dy) < brickHalfHeight + height / 2) {
            // Collision detected
            if (Math.abs(dy) > Math.abs(dx)) {
                // Vertical collision
                speedY = -speedY; // Reverse direction on Y-axis
                y += speedY;
            } else {
                // Horizontal collision
                speedX = -speedX; // Reverse direction on X-axis
                x += speedX;
            }
        } else {
            // Corner collisions
            if (dx > 0) {
                if (dy > 0) {
                    // Ball hits bottom-right corner
                    // Adjust speed in both X and Y directions
                    speedX = -speedX;
                    speedY = -speedY;
                } else {
                    // Ball hits top-right corner
                    // Adjust speed in both X and Y directions
                    speedX = -speedX;
                    speedY = -speedY;
                }
            } else {
                if (dy > 0) {
                    // Ball hits bottom-left corner
                    // Adjust speed in both X and Y directions
                    speedX = -speedX;
                    speedY = -speedY;
                } else {
                    // Ball hits top-left corner
                    // Adjust speed in both X and Y directions
                    speedX = -speedX;
                    speedY = -speedY;
                }
            }
        }
    }


    public boolean isCollidingWithTriangle(TriangleBrick triangle) {
        // Define the vertices of the triangle (assuming the right angle is at the top right)
        Point v1 = new Point(triangle.getX(), triangle.getY()); // Top left vertex
        Point v2 = new Point(triangle.getX() + triangle.getWidth() + 10, triangle.getY()); // Top right vertex
        Point v3 = new Point(triangle.getX() + triangle.getWidth() + 10, triangle.getY() + triangle.getHeight() + 20); // Bottom right vertex

        // Check collision with each side of the triangle
        if (lineIntersectsCircle(v1, v2) || lineIntersectsCircle(v2, v3) || lineIntersectsCircle(v3, v1)) {
            return true;
        }

        // Check if the center of the ball is inside the triangle
        if (isPointInsideTriangle(new Point(x + width / 2, y + height / 2), v1, v2, v3)) {
            return true;
        }

        return false;
    }

    private boolean lineIntersectsCircle(Point a, Point b) {
        Point closest = findClosestPoint(a, b);
        int distanceX = closest.x - (x + width / 2);
        int distanceY = closest.y - (y + height / 2);
        int distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        return distanceSquared < (width / 2) * (width / 2);
    }

    private Point findClosestPoint(Point a, Point b) {
        int ballCenterX = x + width / 2;
        int ballCenterY = y + height / 2;
        int ax = b.x - a.x;
        int ay = b.y - a.y;
        int t = ((ballCenterX - a.x) * ax + (ballCenterY - a.y) * ay) / (ax * ax + ay * ay);
        t = Math.max(0, Math.min(1, t));
        return new Point(a.x + t * ax, a.y + t * ay);
    }

    private boolean isPointInsideTriangle(Point p, Point v1, Point v2, Point v3) {
        int d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(p, v1, v2);
        d2 = sign(p, v2, v3);
        d3 = sign(p, v3, v1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    private int sign(Point p1, Point p2, Point p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

//    public void bounceOff(Ball o) {
//        // Swap speeds of the two balls
//        int tempSpeedX = speedX;
//        int tempSpeedY = speedY;
//        speedX = -o.speedX;
//        speedY = -o.speedY;
//        o.speedX = -tempSpeedX;
//        o.speedY = -tempSpeedY;
//    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img.getImage(), x, y, width, height, null);

        // Draw debug information if debug mode is enabled
        if (debug) {
            drawDebug(g);
        }
    }

    @Override
    protected void drawDebug(Graphics g) {
        g.drawString("SpeedX: " + speedX, x + width + 5, y);
        g.drawString("SpeedY: " + speedY, x + width + 5, y + 15);
    }
}
