package h07.h8;

import h07.*;
import h07.provider.Pointer2DPointerProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.HashMap;
import java.util.Iterator;

import static h07.Assertions.assertArcPointerPoint2DEquals;
import static h07.Assertions.assertNodePointerPoint2DEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestForSubmission("h07")
public class ArcPointerPoint2DTest extends Point2DPointerTest{

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
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

        assertTrue(getExistingArcPointersMap(actualArcPointer).containsKey(arcToAdd) &&
                getExistingArcPointersMap(actualArcPointer).get(arcToAdd).equals(actualArcPointer),
            "the created arcPointer wasn't added to the existingArcPointersMap");

        assertArcPointerPoint2DEquals(existingNodePointers, existingArcPointers, arcToAdd.getElement1(), arcToAdd.getElement2(), collection, actualArcPointer);
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testGetLength(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                              HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                              Point2DCollection collection) {

        Iterator<Point2D> iterator = collection.getPoints().iterator();
        Point2D source = iterator.next();
        Point2D destination = iterator.next();
        double length = Math.sqrt(Math.pow(source.getX() - destination.getX(), 2) + Math.pow(source.getY() - destination.getY(), 2));

        ArcPointerPoint2D actualArcPointer = new ArcPointerPoint2D(existingNodePointers, existingArcPointers, source, destination, collection);

        assertEquals(length, actualArcPointer.getLength(), "the methode getLength() did not return the correct value.");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testGetDestination(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                   HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                   Point2DCollection collection) throws NoSuchFieldException, IllegalAccessException {

        Iterator<Point2D> iterator = collection.getPoints().iterator();
        Point2D source = iterator.next();
        Point2D destination = iterator.next();

        //existingNodes contains destination node
        NodePointer<Double, Double> actualDestination = new ArcPointerPoint2D((HashMap<Point2D, NodePointerPoint2D>) existingNodePointers.clone(), (HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D>) existingArcPointers.clone(), source, destination, collection).destination();
        assertInstanceOf(NodePointerPoint2D.class, actualDestination, "the NodePointer returned by the destination() method does not have the correct dynamic type");
        assertSame(existingNodePointers.get(destination), actualDestination, "the methode destination() did not return the correct value if the existingNodePointers map contains the destination node.");

        //existingNodes does not contain destination node
        existingNodePointers.remove(destination);

        actualDestination = new ArcPointerPoint2D(existingNodePointers, existingArcPointers, source, destination, collection).destination();
        assertInstanceOf(NodePointerPoint2D.class, actualDestination, "the NodePointer returned by the destination() method does not have the correct dynamic type");
        assertNodePointerPoint2DEquals(existingNodePointers, existingArcPointers, destination, collection,
            null, null, (NodePointerPoint2D) actualDestination);
    }

}
