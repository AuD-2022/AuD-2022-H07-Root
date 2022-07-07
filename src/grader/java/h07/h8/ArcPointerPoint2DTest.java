package h07.h8;

import h07.*;
import h07.provider.Pointer2DPointerProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static h07.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class ArcPointerPoint2DTest extends Point2DPointerTest{

    private static final kotlin.Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new kotlin.Pair<>("[[[this]]]", "[[[new ArcPointerPoint2D(existingNodePointers, existingArcPointers, source, destination, collection)]]]");

    private static final kotlin.Pair<String, String> MAP_DESCRIPTION =
        new kotlin.Pair<>("[[[existingArcPointersMap]]] and [[[existingNodePointersMap]]]",
            "the fields and the methods [[[getLength()]]], [[[destination()]]] and [[[outgoingArcs()]]] of the values of the maps have been overwritten to return the expected values");


    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/HashMap.+", "^java/lang/Double compare\\(DD\\)I");
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testConstructor(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                Point2DCollection collection)
        throws NoSuchFieldException, IllegalAccessException {

        Pair<Point2D, Point2D> arcToAdd = existingArcPointers.keySet().iterator().next();
        existingArcPointers.remove(arcToAdd);
        ArcPointerPoint2D actualArcPointer = new ArcPointerPoint2D(existingNodePointers,
            existingArcPointers, arcToAdd.getElement1(), arcToAdd.getElement2(), collection);

        assertTrueTutor(getExistingArcPointersMap(actualArcPointer).containsKey(arcToAdd) &&
                getExistingArcPointersMap(actualArcPointer).get(arcToAdd).equals(actualArcPointer),
            () -> new Assertions.AssertionMessage("the created [[[arcPointer]]] wasn't added to the [[[existingArcPointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertArcPointerPoint2DEquals(existingNodePointers, existingArcPointers, arcToAdd.getElement1(), arcToAdd.getElement2(), collection, actualArcPointer,
            () -> new AssertionMessage("the [[[arcPointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testGetLength(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                              HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                              Point2DCollection collection) {

        Pair<Point2D, Point2D> arcToAdd = existingArcPointers.keySet().iterator().next();
        Point2D source = arcToAdd.getElement1();
        Point2D destination = arcToAdd.getElement2();

        existingArcPointers.remove(arcToAdd);

        ArcPointerPoint2D actualArcPointer = new ArcPointerPoint2D(existingNodePointers,
            existingArcPointers, source, destination, collection);

        double length = Math.sqrt(Math.pow(source.getX() - destination.getX(), 2) + Math.pow(source.getY() - destination.getY(), 2));
        double difference = Math.abs(length - actualArcPointer.getLength());

        assertTrueTutor(difference < 1e-5, () -> new AssertionMessage(
            "[[[getLength()]]] did not return the correct value. Expected a difference of less than 1e-5, but was %f".formatted(difference),
                List.of(CONSTRUCTOR_DESCRIPTION, new kotlin.Pair<>("[[[source, destination]]]", "two points with a distance of %f".formatted(length))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testGetDestination(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                   HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                   Point2DCollection collection) throws NoSuchFieldException, IllegalAccessException {

        Pair<Point2D, Point2D> arcToAdd = existingArcPointers.keySet().iterator().next();
        Point2D source = arcToAdd.getElement1();
        Point2D destination = arcToAdd.getElement2();

        existingArcPointers.remove(arcToAdd);

        ArcPointerPoint2D actualArcPointer = new ArcPointerPoint2D(existingNodePointers,
            existingArcPointers, source, destination, collection);

        //existingNodes contains destination node
        NodePointer<Double, Double> actualDestination = actualArcPointer.destination();

        assertInstanceOf(NodePointerPoint2D.class, actualDestination, "the [[[nodePointer]]] returned by [[[destination()]]] does not have the correct dynamic type if the [[[existingNodePointers]]] map contains the destination point");

        assertSameTutor(existingNodePointers.get(destination), actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[nodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[source, destination]]]", "two points where the destination is a key of the [[[existingNodePointers]]] map")))
        );

        //existingNodes does not contain destination node
        existingNodePointers.remove(destination);

        actualDestination = new ArcPointerPoint2D(existingNodePointers, existingArcPointers, source, destination, collection).destination();

        assertInstanceOf(NodePointerPoint2D.class, actualDestination,
            "the [[[nodePointer]]] returned by [[[destination()]]] does not have the correct dynamic type if the [[[existingNodePointers]]] map does not contain the destination point");

        assertNodePointerPoint2DEquals(existingNodePointers, existingArcPointers, destination, collection,
            null, null, (NodePointerPoint2D) actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[nodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[source, destination]]]", "two points where the destination is not a key of the [[[existingNodePointers]]] map")))
        );
    }

}
