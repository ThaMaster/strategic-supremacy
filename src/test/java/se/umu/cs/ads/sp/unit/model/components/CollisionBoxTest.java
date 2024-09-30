package se.umu.cs.ads.sp.unit.model.components;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.umu.cs.ads.sp.model.components.CollisionBox;
import se.umu.cs.ads.sp.utils.Position;

import java.awt.*;
import java.awt.geom.Line2D;

public class CollisionBoxTest {

    private CollisionBox cBox;

    @Before
    public void beforeMethod() {
        // Do setup for testing...
        // This method is mostly used for integration testing
        cBox = new CollisionBox(new Position(0, 0), 10, 10);
    }

    @After
    public void afterMethod() {
        // Clean up after testing...
        // This method is mostly used for integration testing
    }

    @Test
    public void createCollisionBoxWithCorrectAttributesTest() {
        CollisionBox newCollisionBox = new CollisionBox(new Position(5, 5), 2, 2);
        Rectangle rectangle = new Rectangle(5, 5, 2, 2);
        Assert.assertEquals(newCollisionBox.getCollisionShape(), rectangle);
    }

    @Test
    public void checkIntersectingLineTest() {
        Line2D line = new Line2D.Double(0, 11, 12, 5);
        Assert.assertTrue(cBox.checkCollision(line));
    }

    @Test
    public void checkInternalLineTest() {
        Line2D line = new Line2D.Double(2, 2, 8, 8);
        Assert.assertTrue(cBox.checkCollision(line));
    }

    @Test
    public void checkNonIntersectingLineTest() {
        Line2D line = new Line2D.Double(20, 20, 25, 25);
        Assert.assertFalse(cBox.checkCollision(line));
    }

    @Test
    public void checkIntersectingCollisionBoxTest() {
        CollisionBox cBox2 = new CollisionBox(new Position(5, 5), 10, 10);
        Assert.assertTrue(cBox.checkCollision(cBox2));
    }

    @Test
    public void checkInternalCollisionBoxTest() {
        CollisionBox cBox2 = new CollisionBox(new Position(2, 2), 8, 8);
        Assert.assertTrue(cBox.checkCollision(cBox2));
    }

    @Test
    public void checkNonIntersectingCollisionBoxTest() {
        CollisionBox cBox2 = new CollisionBox(new Position(20, 20), 10, 10);
        Assert.assertFalse(cBox.checkCollision(cBox2));
    }

    @Test
    public void checkLargerCollisionBoxTest() {
        CollisionBox cBox2 = new CollisionBox(new Position(-5, -5), 20, 20);
        Assert.assertTrue(cBox.checkCollision(cBox2));
    }
}
