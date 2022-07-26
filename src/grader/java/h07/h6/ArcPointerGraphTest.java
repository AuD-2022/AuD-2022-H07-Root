package h07.h6;


import h07.*;
import h07.provider.GraphToGraphPointerMapsProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.HashMap;
import java.util.List;

import static h07.Assertions.*;
import static h07.TestConstants.RANDOM;
import static org.junit.jupiter.api.Assertions.*;
import static h07.TestConstants.MAX_NODE_DISTANCE;

@TestForSubmission("h07")
public class ArcPointerGraphTest extends GraphPointerTest {

    private static final Pair<String, String> CONSTRUCTOR_DESCRIPTION =
        new Pair<>("[[[this]]]", "[[[new ArcPointerGraph(existingNodePointers, existingArcPointers, newGraphArc)]]]");

    private static final Pair<String, String> MAP_DESCRIPTION =
        new Pair<>("[[[existingArcPointersMap]]] and [[[existingNodePointersMap]]]",
            "the fields and the methods [[[getLength()]]], [[[destination()]]] and [[[outgoingArcs()]]] of the values of the maps have been overwritten to return the expected values");

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

        GraphArc<Integer> newGraphArc = existingArcPointers.keySet().iterator().next();
        existingArcPointers.remove(newGraphArc);
        ArcPointerGraph<Integer, Integer> actualArcPointer = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, newGraphArc);

        assertTrueTutor(getExistingArcPointersMap(actualArcPointer).containsKey(newGraphArc) &&
                getExistingArcPointersMap(actualArcPointer).get(newGraphArc).equals(actualArcPointer),
            () -> new AssertionMessage("the created [[[arcPointer]]] wasn't added to the [[[existingArcPointersMap]]] after invoking the constructor",
                List.of(CONSTRUCTOR_DESCRIPTION)));

        assertArcPointerGraphEquals(existingNodePointers, existingArcPointers, newGraphArc, actualArcPointer,
            () -> new AssertionMessage("the [[[arcPointer]]] created by the constructor does not have the expected properties",
                List.of(CONSTRUCTOR_DESCRIPTION)));
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testGetLength(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) {

        int length = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);

        GraphArc<Integer> newGraphArc = new GraphArc<>(length, existingNodePointers.keySet().iterator().next());
        ArcPointerGraph<Integer, Integer> actualArcPointer = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, newGraphArc);

        assertEqualsTutor(length, actualArcPointer.getLength(),
            () -> new AssertionMessage("[[[getLength()]]] did not return the correct value",
                List.of(CONSTRUCTOR_DESCRIPTION, new Pair<>("[[[newGraphArc]]]", "a [[[graphArc]]] with [[[length == %d]]]".formatted(length))))
        );
    }

    @ParameterizedTest
    @ArgumentsSource(GraphToGraphPointerMapsProvider.class)
    public void testGetDestination(HashMap<GraphNode<Integer>, NodePointerGraph<Integer, Integer>> existingNodePointers,
                                HashMap<GraphArc<Integer>, ArcPointerGraph<Integer, Integer>> existingArcPointers) throws NoSuchFieldException, IllegalAccessException {

        int length = RANDOM.nextInt(0, MAX_NODE_DISTANCE + 1);
        GraphNode<Integer> destination = existingNodePointers.keySet().iterator().next();

        //existingNodePointers contains the destination node
        GraphArc<Integer> newGraphArc = new GraphArc<>(length, destination);
        NodePointer<Integer, Integer> actualDestination = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, newGraphArc).destination();

        assertInstanceOf(NodePointerGraph.class, actualDestination,
            "the [[[nodePointer]]] returned by [[[destination()]]] does not have the correct dynamic type if the [[[existingNodePointers]]] map contains the destination node");

        assertSameTutor(existingNodePointers.get(destination), actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[nodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphArc]]]", "a [[[graphArc]]] whose destination is a key of the [[[existingNodePointers]]] map")))
        );

        //existingNodePointers does not contain the destination node
        existingNodePointers.remove(destination);

        actualDestination = new ArcPointerGraph<>(existingNodePointers, existingArcPointers, newGraphArc).destination();

        assertInstanceOf(NodePointerGraph.class, actualDestination,
            "the [[[nodePointer]]] returned by [[[destination()]]] method does not have the correct dynamic type if the [[[existingNodePointers]]] map does not contain the destination node");

        assertNodePointerGraphEquals(existingNodePointers, existingArcPointers, destination, null, null, (NodePointerGraph<Integer, Integer>) actualDestination,
            () -> new AssertionMessage("[[[destination()]]] did not return the correct [[[nodePointer]]]",
                List.of(CONSTRUCTOR_DESCRIPTION, MAP_DESCRIPTION,
                    new Pair<>("[[[newGraphArc]]]", "a [[[graphArc]]] whose destination is not a key of the [[[existingNodePointers]]] map")))
        );
    }

}
