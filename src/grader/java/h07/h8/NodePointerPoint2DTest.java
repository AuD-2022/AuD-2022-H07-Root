package h07.h8;

import h07.*;
import h07.provider.Pointer2DPointerProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static h07.provider.Pointer2DPointerProvider.MAX_ARC_LENGTH;
import static h07.provider.AbstractProvider.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static h07.Assertions.*;

@TestForSubmission("h07")
public class NodePointerPoint2DTest extends Point2DPointerTest{

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/HashMap.+",
            "^java/util/Iterator.+",
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+",
            "^java/lang/Math.+");
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testConstructor(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                Point2DCollection collection) throws NoSuchFieldException, IllegalAccessException {

        Point2D pointToAdd = existingNodePointers.keySet().iterator().next();
        existingNodePointers.remove(pointToAdd);
        NodePointerPoint2D actualNodePointer = new NodePointerPoint2D(existingNodePointers,
            existingArcPointers, pointToAdd, collection);

        assertTrue(getExistingNodePointersMap(actualNodePointer).containsKey(pointToAdd) &&
                getExistingNodePointersMap(actualNodePointer).get(pointToAdd).equals(actualNodePointer),
            "the created nodePointer wasn't added to the existingArcPointersMap");

        assertNodePointerPoint2DEquals(existingNodePointers, existingArcPointers, pointToAdd, collection, null, null, actualNodePointer);
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testDistance(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                             HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                             Point2DCollection collection) {

        NodePointerPoint2D newNodePointer = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(0, 0), collection);
        double distance = RANDOM.nextDouble(0, MAX_ARC_LENGTH);

        newNodePointer.setDistance(distance);
        assertEquals(distance, newNodePointer.getDistance(), "the methode getDistance() did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testPredecessor(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                Point2DCollection collection) {

        NodePointerPoint2D predecessor = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(0,0), collection);
        NodePointerPoint2D destination = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(1,1), collection);

        destination.setPredecessor(predecessor);
        assertEquals(predecessor, destination.getPredecessor(), "the methode getPredecessor() did not return the correct value");
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testOutgoingArcs(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                 HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                 Point2DCollection collection) throws NoSuchFieldException, IllegalAccessException {

        Point2D point = collection.getPoints().get(0);
        List<ArcPointerPoint2D> expectedOutgoingArcs = collection.getPoints().stream()
            .filter(destination -> destination != point)
            .filter(destination -> Math.sqrt(Math.pow(point.getX() - destination.getX(), 2) + Math.pow(point.getY() - destination.getY(), 2)) <= MAX_ARC_LENGTH)
            .map(destination -> existingArcPointers.get(new Pair<>(point, destination)))
            .collect(Collectors.toList());

        NodePointerPoint2D node = new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection);

        //existingArcsMap contains all arcs
        List<ArcPointerPoint2D> actualOutgoingArcs = arcPointerListToArcPointerPoint2DList(iteratorToList(node.outgoingArcs()));
        assertEquals(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), "the method outgoingArcs() did not return the correct amount of arcs if the existingArcPointersMap contains all arcs");
        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerPoint2D expected, ArcPointerPoint2D actual) -> expected == actual, "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap contains all arcs");

        //existingArcsMap does not contain the arcs
        for (ArcPointerPoint2D outgoingArc : expectedOutgoingArcs) existingArcPointers.remove(new Pair<>(getSource(outgoingArc), getDestination(outgoingArc)));

        node = new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection);

        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerPoint2D expected, ArcPointerPoint2D actual) -> {
            try {
                return getExistingNodePointersMap(expected) == getExistingNodePointersMap(actual) &&
                    getExistingArcPointersMap(expected) == getExistingArcPointersMap(actual) &&
                    getCollection(expected) == getCollection(actual) &&
                    getSource(expected) == getSource(actual) &&
                    getDestination(expected) == getDestination(actual);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                fail("could not read fields of class ArcPointerPoint2D"); //shouldn't happen
                return false;
            }
        }, "the method outgoingArcs() did not return the correct elements if the existingArcPointersMap does not contains all arcs");
    }


    private List<ArcPointerPoint2D> arcPointerListToArcPointerPoint2DList(List<ArcPointer<Double, Double>> outgoingArcs) {
        return outgoingArcs.stream()
            .map((ArcPointer<Double, Double> arcPointer) -> {
                    assertInstanceOf(ArcPointerPoint2D.class, arcPointer, "the elements returned by the the outgoingArcs() method did not have the correct dynamic type");
                    return ((ArcPointerPoint2D) arcPointer);
                }
            ).collect(Collectors.toList());
    }

    private <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) list.add(iterator.next());
        return list;
    }

}
