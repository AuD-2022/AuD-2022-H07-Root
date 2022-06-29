package h07.h6;


import h07.*;
import h07.provider.GraphToGraphPointerMapsProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.HashMap;

import static h07.Assertions.assertArcPointerGraphEquals;
import static h07.Assertions.assertNodePointerGraphEquals;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static h07.TestConstants.MAX_NODE_DISTANCE;

@TestForSubmission("h07")
public class ArcPointerGraphTest extends GraphPointerTest {

    @BeforeEach
    public void reset() {
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods("^java/util/HashMap.+");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testConstructor(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers)
        throws NoSuchFieldException, IllegalAccessException {

        GraphArc<Integer> arcToAdd = existingArcPointers.keySet().iterator().next();
        existingArcPointers.remove(arcToAdd);
        ArcPointerGraph<Integer, Integer> actualArcPointer = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, arcToAdd);

        assertTrue(getExistingArcPointersMap(actualArcPointer).containsKey(arcToAdd) &&
                getExistingArcPointersMap(actualArcPointer).get(arcToAdd).equals(actualArcPointer),
            "the created arcPointer wasn't added to the existingArcPointersMap");

        assertArcPointerGraphEquals(existingNodePointers, existingArcPointers, arcToAdd, actualArcPointer);
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testGetLength(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        int length = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        GraphArc<Integer> arcToAdd = new GraphArc<>(length, existingNodePointers.keySet().iterator().next());
        ArcPointerGraph<Integer, Integer> actualArcPointer = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, arcToAdd);

        assertEquals(length, actualArcPointer.getLength(), "the methode getLength() did not return the correct value.");
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testGetDestination(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        int length = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);
        GraphNode<Integer> destination = existingNodePointers.keySet().iterator().next();

        //existingNodePointers contains the destination node
        GraphArc<Integer> arcToAdd = new GraphArc<>(length, destination);
        NodePointer<Integer, Integer> actualDestination = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, arcToAdd).destination();
        assertInstanceOf(NodePointerGraph.class, actualDestination, "the NodePointer returned by the destination() method does not have the correct dynamic type if the existingNodePointers map contains the destination node");
        assertEquals(existingNodePointers.get(destination), actualDestination, "the methode destination() did not return the correct value if the existingNodePointers map contains the destination node");

        //existingNodePointers does not contain the destination node
        existingNodePointers.remove(destination);

        actualDestination = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, arcToAdd).destination();
        assertInstanceOf(NodePointerGraph.class, actualDestination, "the NodePointer returned by the destination() method does not have the correct dynamic type if the existingNodePointers map does not contain the destination node");
        assertNodePointerGraphEquals(existingNodePointers, existingArcPointers, destination, null, null, (NodePointerGraph<Integer, Integer>) actualDestination);
    }

}
