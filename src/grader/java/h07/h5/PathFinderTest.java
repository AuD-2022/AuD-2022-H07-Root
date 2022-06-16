package h07.h5;

import h07.NodePointer;
import h07.PathFinder;
import h07.implementation.NodePointerImpl;
import h07.provider.PathProvider;
import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestForSubmission("h07")
public class PathFinderTest {

    @BeforeEach
    public void reset() {
        NodePointerImpl.resetIds();
        MethodInterceptor.reset();
    }

    @ParameterizedTest
    @ArgumentsSource(PathProvider.class)
    public void testPathFinder(List<NodePointerImpl> nodes) {
        List<NodePointer<Integer, Integer>> actualNodes = new PathFinder<Integer, Integer>().apply(nodes.get(nodes.size() - 1));

        assertEquals(nodes.size(), actualNodes.size(),
            "the provided path does not contain the correct amount of nodes. Expected %d, actual %d".formatted(nodes.size(), actualNodes.size()));

        for (int i = 0; i < nodes.size(); i++) {
            assertEquals(nodes.get(i), actualNodes.get(i),
                "node at position %d not correct. Expected %s, actual %s".formatted(i, nodes.get(i).toString(), actualNodes.get(i).toString()));
        }

    }



}
