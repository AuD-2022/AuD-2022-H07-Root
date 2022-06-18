package h07.h8;

import h07.IllegalMethodsCheck;
import h07.Point2D;
import h07.Point2DCollection;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static h07.Assertions.assertBetween;
import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class Point2DCollectionTest {

    public static final int POINT_COUNT = 25;
    public static final int MAX_ARC_LENGTH = 10;

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/concurrent/ThreadLocalRandom.+",
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+",
            "^java/lang/Double compare\\(DD\\)I+");
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Point2D from = new Point2D(0, 0);
        Point2D to = new Point2D(10, 10);

        List<Double> values = new ArrayList<>(POINT_COUNT*2);

        Point2DCollection collection = new Point2DCollection(POINT_COUNT, from, to, MAX_ARC_LENGTH);

        assertEquals(MAX_ARC_LENGTH, getMaxArcLength(collection), "the attribute maxArcLength does not have the correct value");

        List<Point2D> actualPoints = getPoints(collection);
        assertNotNull(actualPoints, "the list points is null");
        assertEquals(POINT_COUNT, actualPoints.size(), "the list points does not have the correct size");
        for (Point2D point : actualPoints) {
            assertBetween(from.getX(), to.getX(), point.getX(), "incorrect x-value for point at position %d in points list. ".formatted(actualPoints.indexOf(point)));
            assertBetween(from.getY(), to.getY(), point.getY(), "incorrect y-value for point at position %d in points list. ".formatted(actualPoints.indexOf(point)));
            values.add(point.getX());
            values.add(point.getY());
        }

        double average = values.stream().mapToDouble(Double::doubleValue).sum() / values.size();
        double standardDeviation = Math.sqrt(1/(values.size() - 1.0) * values.stream().mapToDouble(value -> Math.pow(value - average, 2)).sum());
        assertTrue(standardDeviation > 1.5, "Expected a standard deviation of at least 2.0 but got %f"
            .formatted(standardDeviation));
    }

    private double getMaxArcLength(Point2DCollection col) throws NoSuchFieldException, IllegalAccessException {
        Field field = Point2DCollection.class.getDeclaredField("maxArcLength");
        field.setAccessible(true);
        return (double) field.get(col);
    }

    @SuppressWarnings("unchecked")
    private List<Point2D> getPoints(Point2DCollection col) throws NoSuchFieldException, IllegalAccessException {
        Field field = Point2DCollection.class.getDeclaredField("points");
        field.setAccessible(true);
        return (List<Point2D>) field.get(col);
    }
}
