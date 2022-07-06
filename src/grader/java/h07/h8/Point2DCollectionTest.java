package h07.h8;

import h07.IllegalMethodsCheck;
import h07.Point2D;
import h07.Point2DCollection;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static h07.Assertions.*;
import static h07.TestConstants.MAX_ARC_LENGTH_POINT;
import static h07.TestConstants.POINTS_COUNT;
import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class Point2DCollectionTest {

    private static final Pair<String, String> CONSTRUCTOR_DESCRIPTION = new Pair<>("[[[this]]]", "[[[new Point2DCollection(%d, (%d,%d), (%d,%d), %d]]]"
        .formatted(POINTS_COUNT, 0,0,10,10,MAX_ARC_LENGTH_POINT));

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
            "^java/lang/Double compare\\(DD\\)I");
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        Point2D from = new Point2D(0, 0);
        Point2D to = new Point2D(10, 10);

        List<Double> values = new ArrayList<>(POINTS_COUNT*2);

        Point2DCollection collection = new Point2DCollection(POINTS_COUNT, from, to, MAX_ARC_LENGTH_POINT);

        assertEqualsTutor(MAX_ARC_LENGTH_POINT, getMaxArcLength(collection), () ->
            new AssertionMessage("the attribute [[[maxArcLength]]] does not have the correct value",
                List.of(CONSTRUCTOR_DESCRIPTION))
        );

        List<Point2D> actualPoints = getPoints(collection);

        assertNotNullTutor(actualPoints, () -> new AssertionMessage("the list [[[points]]] is null",
            List.of(CONSTRUCTOR_DESCRIPTION)));

        assertEqualsTutor(POINTS_COUNT, actualPoints.size(), () ->
            new AssertionMessage("the list [[[points]]] does not have the correct size",
                List.of(CONSTRUCTOR_DESCRIPTION))
        );

        for (Point2D point : actualPoints) {
            assertBetween(from.getX(), to.getX(), point.getX(),
                () -> new AssertionMessage("incorrect x-value for point at position %d in points list. ".formatted(actualPoints.indexOf(point)),
                    List.of(CONSTRUCTOR_DESCRIPTION))
            );

            assertBetween(from.getY(), to.getY(), point.getY(),
                () -> new AssertionMessage("incorrect y-value for point at position %d in points list. ".formatted(actualPoints.indexOf(point)),
                    List.of(CONSTRUCTOR_DESCRIPTION))
            );

            values.add(point.getX());
            values.add(point.getY());
        }

        double average = values.stream().mapToDouble(Double::doubleValue).sum() / values.size();
        double standardDeviation = Math.sqrt(1/(values.size() - 1.0) * values.stream().mapToDouble(value -> Math.pow(value - average, 2)).sum());

        assertTrueTutor(standardDeviation > 1.5, () -> new AssertionMessage("expected a standard deviation of at least 1.5 but got %f"
            .formatted(standardDeviation), List.of(CONSTRUCTOR_DESCRIPTION)), false);
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
