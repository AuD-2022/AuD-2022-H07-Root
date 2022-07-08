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

import static h07.TestConstants.MAX_ARC_LENGTH_POINT;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static h07.Assertions.*;

@TestForSubmission("h07")
public class NodePointerPoint2DTest extends Point2DPointerTest{

    private static final kotlin.Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new kotlin.Pair<>("[[[this]]]", "[[[new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection)]]]");

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
            "^java/util/HashMap.+",
            "^java/util/Iterator.+",
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+",
            "^java/lang/Double compare\\(DD\\)I");
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

        assertTrueTutor(getExistingNodePointersMap(actualNodePointer).containsKey(pointToAdd) &&
                getExistingNodePointersMap(actualNodePointer).get(pointToAdd).equals(actualNodePointer),
            () -> new AssertionMessage("the created [[[nodePointer]]] wasn't added to the [[[existingNodePointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertNodePointerPoint2DEquals(existingNodePointers, existingArcPointers, pointToAdd, collection, null, null, actualNodePointer,
            () -> new AssertionMessage("the [[[nodePointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testDistance(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                             HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                             Point2DCollection collection) {

        NodePointerPoint2D newNodePointer = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(0, 0), collection);
        double distance = RANDOM.nextDouble(0, MAX_ARC_LENGTH_POINT);

        newNodePointer.setDistance(distance);
        assertEqualsTutor(distance, newNodePointer.getDistance(), () -> new AssertionMessage("[[[getDistance()]]] did not return the correct value",
            List.of(CONSTRUCTOR_DESCRIPTION,
                new kotlin.Pair<>("[[[distance]]]", "The [[[distance]]] of the [[[nodePointer]]] has been set using [[[setDistance(%f)]]]".formatted(distance))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testPredecessor(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                Point2DCollection collection) {

        NodePointerPoint2D predecessor = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(0,0), collection);
        NodePointerPoint2D destination = new NodePointerPoint2D(existingNodePointers, existingArcPointers, new Point2D(1,1), collection);

        destination.setPredecessor(predecessor);
        assertEqualsTutor(predecessor, destination.getPredecessor(),() -> new AssertionMessage("[[[getPredecessor()]]] did not return the correct value",
            List.of(CONSTRUCTOR_DESCRIPTION,
                new kotlin.Pair<>("[[[predecessor]]]", "The [[[predecessor]]] of the [[[nodePointer]]] has been set using [[[setPredecessor(%s)]]]".formatted(predecessor))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(Pointer2DPointerProvider.class)
    public void testOutgoingArcs(HashMap<Point2D, NodePointerPoint2D> existingNodePointers,
                                 HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers,
                                 Point2DCollection collection) throws NoSuchFieldException, IllegalAccessException {

        Point2D point = collection.getPoints().get(0);
        List<ArcPointerPoint2D> expectedOutgoingArcs = collection.getPoints().stream()
            .filter(destination -> destination != point)
            .filter(destination -> Math.sqrt(Math.pow(point.getX() - destination.getX(), 2) + Math.pow(point.getY() - destination.getY(), 2)) <= MAX_ARC_LENGTH_POINT)
            .map(destination -> existingArcPointers.get(new Pair<>(point, destination)))
            .collect(Collectors.toList());

        NodePointerPoint2D node = new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection);

        //existingArcsMap contains all arcs
        List<ArcPointerPoint2D> actualOutgoingArcs = arcPointerListToArcPointerPoint2DList(iteratorToList(node.outgoingArcs()));

        //create a message with more information about why the test failed
        String extendedMessage = createExtendedMessage(actualOutgoingArcs, point);

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(), () -> new AssertionMessage(
            "[[[outgoingArcs()]]] did not return the correct amount of arcs" + extendedMessage,
            List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                new kotlin.Pair<>("[[[point]]]", "a [[[Point]]] whose outgoing arcs are keys of the [[[existingArcPointers]]] map")))
        );

        assertListContainsAllWithPredicate(expectedOutgoingArcs, actualOutgoingArcs, (ArcPointerPoint2D expected, ArcPointerPoint2D actual) -> expected == actual,
            "the list returned by [[[outgoingArcs()]]]",
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs" + extendedMessage,
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[point]]]", "a [[[Point]]] whose outgoing arcs are keys of the [[[existingArcPointers]]] map")))
        );

        //existingArcsMap does not contain the arcs
        for (ArcPointerPoint2D outgoingArc : expectedOutgoingArcs) existingArcPointers.remove(new Pair<>(getSource(outgoingArc), getDestination(outgoingArc)));

        node = new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection);
        actualOutgoingArcs = arcPointerListToArcPointerPoint2DList(iteratorToList(node.outgoingArcs()));

        //create a message with more information about why the test failed
        String extendedMessage2 = createExtendedMessage(actualOutgoingArcs, point);

        assertEqualsTutor(expectedOutgoingArcs.size(), actualOutgoingArcs.size(),
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct amount of arcs" + extendedMessage2,
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[point]]]", "a [[[Point]]] whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );

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
        }, "the list returned by [[[outgoingArcs()]]]",
            () -> new AssertionMessage("[[[outgoingArcs()]]] did not return the correct arcs" + extendedMessage2,
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new kotlin.Pair<>("[[[point]]]", "a [[[Point]]] whose outgoing arcs are not keys of the [[[existingArcPointers]]] map")))
        );
    }

    private static String createExtendedMessage(List<ArcPointerPoint2D> actualOutgoingArcs, Point2D point) {
        String extendedMessage = "";

        if (actualOutgoingArcs.size() == 0) return extendedMessage;

        if (actualOutgoingArcs.stream().map(arc -> {
            try {
                return getDestination(arc);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                fail("could not read field of class ArcPointerPoint2D");//shouldn't happen
                return null;
            }
        }).anyMatch(dest -> dest == point)) {
            extendedMessage += ". An arc to the node itself was returned";
        }

        if (actualOutgoingArcs.stream().map(ArcPointerPoint2D::getLength).noneMatch(length -> length == MAX_ARC_LENGTH_POINT)) {
            extendedMessage += ". The arc with [[[length == maxArcLength]]] wasn't returned";
        }

        return extendedMessage;
    }


    private List<ArcPointerPoint2D> arcPointerListToArcPointerPoint2DList(List<ArcPointer<Double, Double>> outgoingArcs) {
        return outgoingArcs.stream()
            .map((ArcPointer<Double, Double> arcPointer) -> {
                    assertInstanceOf(ArcPointerPoint2D.class, arcPointer, "the elements returned by [[[outgoingArcs()]]] did not have the correct dynamic type");
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
