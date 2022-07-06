package h07.h5;

import h07.IllegalMethodsCheck;
import h07.NodePointer;
import h07.PathFinder;
import h07.implementation.NodePointerImpl;
import h07.provider.PathProvider;
import h07.transformer.MethodInterceptor;
import kotlin.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.List;

import static h07.Assertions.*;

@TestForSubmission("h07")
public class PathFinderTest {

    @BeforeEach
    public void reset() {
        NodePointerImpl.resetIds();
        MethodInterceptor.reset();
    }

    @AfterEach
    public void checkIllegalMethods() {
        IllegalMethodsCheck.checkMethods(
            "^java/util/LinkedList.+",
            "^java/util/ArrayList.+",
            "^java/util/Iterator.+"
        );
    }

    @ParameterizedTest
    @ArgumentsSource(PathProvider.class)
    public void testPathFinder(List<NodePointerImpl> nodes) {
        List<NodePointer<Integer, Integer>> actualNodes = new PathFinder<Integer, Integer>().apply(nodes.get(nodes.size() - 1));

        assertEqualsTutor(nodes.size(), actualNodes.size(),
            () -> new AssertionMessage("the provided path does not contain the correct amount of nodes. Expected %d, actual %d".formatted(nodes.size(), actualNodes.size()),
                List.of(new Pair<>("[[[this]]]", "new PathFinder<>(node)"),
                    new Pair<>("[[[node]]]", "a reference implementation with %d predecessors".formatted(nodes.size() - 1))))
        );

        //check if the order is reversed for better assertion message
        boolean reversed = true;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(nodes.size() - i - 1) != actualNodes.get(i)) {
                reversed = false;
                break;
            }
        }

        if (nodes.size() > 1 && reversed) failTutor(new AssertionMessage("the order of the elements in the returned list is reversed",
            List.of(new Pair<>("[[[this]]]", "new PathFinder<>(node)"),
                new Pair<>("[[[node]]]", "a reference implementation with %d predecessors".formatted(nodes.size() - 1))))
        );

        for (int i = 0; i < nodes.size(); i++) {
            int finalI = i;
            assertSameTutor(nodes.get(i), actualNodes.get(i),
                () -> new AssertionMessage("node at position %d not correct. Expected %s, actual %s".formatted(finalI, nodes.get(finalI).toString(), actualNodes.get(finalI).toString()),
                    List.of(new Pair<>("[[[this]]]", "new PathFinder<>(node)"),
                        new Pair<>("[[[node]]]", "a reference implementation with %d predecessors".formatted(nodes.size() - 1))))
            );
        }

    }



}
