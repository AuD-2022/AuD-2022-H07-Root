package h07;

import h07.h2.PriorityQueueListTest;
import h07.h3.PriorityQueueHeapTest;
import h07.h4.DijkstraTest;
import h07.h5.PathFinderTest;
import h07.h6.ArcPointerGraphTest;
import h07.h6.GraphTest;
import h07.h6.NodePointerGraphTest;
import h07.h7.AdjacencyMatrixTest;
import h07.h7.ArcPointerAdjacencyMatrixTest;
import h07.h7.NodePointerAdjacencyMatrixTest;
import h07.h8.ArcPointerPoint2DTest;
import h07.h8.NodePointerPoint2DTest;
import h07.h8.Point2DCollectionTest;
import h07.implementation.QueueEntry;
import h07.transformer.AccessTransformer;
import h07.transformer.BytecodeTransformations;
import org.sourcegrade.jagr.api.rubric.*;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;

@RubricForSubmission("h07")
public class H07_RubricProvider implements RubricProvider {

    private static final BiFunction<String, Callable<Method>, Criterion> DEFAULT_CRITERION = (shortDescription, callable) ->
        Criterion.builder()
            .shortDescription(shortDescription)
            .grader(Grader.testAwareBuilder()
                .requirePass(JUnitTestRef.ofMethod(callable))
                .pointsFailedMin()
                .pointsPassedMax()
                .build())
            .build();


    private static final Criterion H2_1 = DEFAULT_CRITERION.apply("Der Konstruktor ist korrekt implementiert.",
        () -> PriorityQueueListTest.class.getDeclaredMethod("testConstructor"));

    private static final Criterion H2_2 = DEFAULT_CRITERION.apply("Die Methode add ist korrekt implementiert.",
        () -> PriorityQueueListTest.class.getDeclaredMethod("testAdd"));

    private static final Criterion H2_3 = Criterion.builder()
        .shortDescription("Die Methoden getFront() und deleteFront() sind korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueListTest.class.getDeclaredMethod("testGetFront", List.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueListTest.class.getDeclaredMethod("testDeleteFront", List.class)))
            .pointsFailedMin()
            .pointsPassedMax()
            .build())
        .build();

    private static final Criterion H2_4 = DEFAULT_CRITERION.apply("Die Klasse PriorityQueueList<T> ist vollständig korrekt implementiert.",
        () -> PriorityQueueListTest.class.getDeclaredMethod("testAll"));

    private static final Criterion H2 = Criterion.builder()
        .shortDescription("H2 | PriorityQueue als Liste")
        .addChildCriteria(H2_1, H2_2, H2_3, H2_4)
        .build();

    private static final Criterion H3_1 = DEFAULT_CRITERION.apply("Der Konstruktor ist korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testConstructor"));

    private static final Criterion H3_2 = DEFAULT_CRITERION.apply("Die Methode add fügt das Element in das Array und die Map ein.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testAddSimple"));

    private static final Criterion H3_3 = DEFAULT_CRITERION.apply("Die Methode add ist vollständig korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testAddComplex"));

    private static final Criterion H3_4 = DEFAULT_CRITERION.apply("Die Methoe delete entfernt das Element aus dem Array und der Map.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testDeleteSimple", QueueEntry[].class));

    private static final Criterion H3_5 = Criterion.builder()
        .shortDescription("Die Methode delete ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueHeapTest.class.getDeclaredMethod("testDeleteComplex", QueueEntry[].class)))
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueHeapTest.class.getDeclaredMethod("testDeleteUpwardCorrectionEdgeCase")))
            .pointsFailedMin()
            .pointsPassedMax()
            .build())
        .build();

    private static final Criterion H3_6 = Criterion.builder()
        .shortDescription("Die Methoden getFront() und deleteFront() sind vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueHeapTest.class.getDeclaredMethod("testGetFront", QueueEntry[].class)))
            .requirePass(JUnitTestRef.ofMethod(() -> PriorityQueueHeapTest.class.getDeclaredMethod("testDeleteFront", QueueEntry[].class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();


    private static final Criterion H3_7 = DEFAULT_CRITERION.apply("Die Methode getPosition ist vollständig korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testGetPosition", QueueEntry[].class));

    private static final Criterion H3_8 = DEFAULT_CRITERION.apply("Die Methode contains ist vollständig korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testContains", QueueEntry[].class));

    private static final Criterion H3_9 = DEFAULT_CRITERION.apply("Die Methode clear ist vollständig korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testClear", QueueEntry[].class));

    private static final Criterion H3_10 = DEFAULT_CRITERION.apply("Die Klasse PriorityQueueHeap<T> ist vollständig korrekt implementiert.",
        () -> PriorityQueueHeapTest.class.getDeclaredMethod("testAll"));

    private static final Criterion H3 = Criterion.builder()
        .shortDescription("H3 | PriorityQueue als Heap")
        .addChildCriteria(H3_1, H3_2, H3_3, H3_4, H3_5, H3_6, H3_7, H3_8, H3_9, H3_10)
        .build();

    private static final Criterion H4_1 = DEFAULT_CRITERION.apply("Der Konstruktor ist korrekt implementiert.",
        () -> DijkstraTest.class.getDeclaredMethod("testConstructor"));

    private static final Criterion H4_2 = DEFAULT_CRITERION.apply("Die beiden Methoden initialize sind vollständig korrekt implementiert.",
        () -> DijkstraTest.class.getDeclaredMethod("testInitialize", List.class));

    private static final Criterion H4_3 = DEFAULT_CRITERION.apply("Nachfolgeknoten, die noch nicht besucht wurden, werden korrekt bearbeitet und in die Queue eingefügt.",
        () -> DijkstraTest.class.getDeclaredMethod("testUnvisitedNode"));

    private static final Criterion H4_4 = DEFAULT_CRITERION.apply("Wenn ein kürzerer Weg gefunden wurde, ersetzt dieser den bisher gefundenen.",
        () -> DijkstraTest.class.getDeclaredMethod("testUpdateVisitedNode"));

    private static final Criterion H4_5 = DEFAULT_CRITERION.apply("Der Algorithmus terminiert korrekt, wenn das Predicate für einen Knoten true liefert.",
        () -> DijkstraTest.class.getDeclaredMethod("testTerminate", List.class));

    private static final Criterion H4_6 = DEFAULT_CRITERION.apply("Die gefundenen Knoten werden korrekt zurückgegeben.",
        () -> DijkstraTest.class.getDeclaredMethod("testWithoutPredicate", List.class));

    private static final Criterion H4_7 = Criterion.builder()
        .shortDescription("Die Klasse Dijkstra<L, D> ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> DijkstraTest.class.getDeclaredMethod("testConstructor")))
            .requirePass(JUnitTestRef.ofMethod(() -> DijkstraTest.class.getDeclaredMethod("testDijkstra", List.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H4 = Criterion.builder()
        .shortDescription("H4 | Dijkstra - Abstraktion von der Graphenstruktur")
        .addChildCriteria(H4_1, H4_2, H4_3, H4_4, H4_5, H4_6, H4_7)
        .build();

    private static final Criterion H5_1 = DEFAULT_CRITERION.apply("Die Klasse PathFinder<L, D> ist vollständig korrekt implementiert.",
        () -> PathFinderTest.class.getDeclaredMethod("testPathFinder", List.class));

    private static final Criterion H5 = Criterion.builder()
        .shortDescription("H5 | Kürzesten Pfad hinterher rekonstruieren")
        .addChildCriteria(H5_1)
        .build();

    private static final Criterion H6_1 = Criterion.builder()
        .shortDescription("Die Klasse NodePointerGraph<L, D> ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerGraphTest.class.getDeclaredMethod("testConstructor", HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerGraphTest.class.getDeclaredMethod("testDistance", HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerGraphTest.class.getDeclaredMethod("testPredecessor", HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerGraphTest.class.getDeclaredMethod("testOutgoingArcs", HashMap.class, HashMap.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H6_2 = Criterion.builder()
        .shortDescription("Die Klasse ArcPointerGraph<L, D> ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerGraphTest.class.getDeclaredMethod("testConstructor", HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerGraphTest.class.getDeclaredMethod("testGetDestination", HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerGraphTest.class.getDeclaredMethod("testGetLength", HashMap.class, HashMap.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H6_3 = DEFAULT_CRITERION.apply("Der Konstruktor Graph(AdjacencyMatrix<L>) ist vollständig korrekt implementiert.",
        () -> GraphTest.class.getDeclaredMethod("testConstructor", AdjacencyMatrix.class));

    private static final Criterion H6 = Criterion.builder()
        .shortDescription("H6 | Graph")
        .addChildCriteria(H6_1, H6_2, H6_3)
        .build();

    private static final Criterion H7_1 = Criterion.builder()
        .shortDescription("Die Klasse NodePointerAdjacencyMatrix<L, D> ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerAdjacencyMatrixTest.class.getDeclaredMethod("testConstructor", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerAdjacencyMatrixTest.class.getDeclaredMethod("testDistance", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerAdjacencyMatrixTest.class.getDeclaredMethod("testPredecessor", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerAdjacencyMatrixTest.class.getDeclaredMethod("testOutgoingArcs", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H7_2 = Criterion.builder()
        .shortDescription("Die Klasse ArcPointerAdjacencyMatrix<L, D> ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerAdjacencyMatrixTest.class.getDeclaredMethod("testConstructor", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerAdjacencyMatrixTest.class.getDeclaredMethod("testGetDestination", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerAdjacencyMatrixTest.class.getDeclaredMethod("testGetLength", AdjacencyMatrix.class, HashMap.class, HashMap.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H7_3 = DEFAULT_CRITERION.apply("Der Konstruktor AdjacencyMatrix(Graph<L>) ist vollständig korrekt implementiert.",
        () -> AdjacencyMatrixTest.class.getDeclaredMethod("testConstructor", Graph.class));

    private static final Criterion H7 = Criterion.builder()
        .shortDescription("H7 | Graph als Adjazenzmatrix")
        .addChildCriteria(H7_1, H7_2, H7_3)
        .build();

    private static final Criterion H8_1 = Criterion.builder()
        .shortDescription("Die Klasse Point2DCollection ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> Point2DCollectionTest.class.getDeclaredMethod("testConstructor")))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H8_2 = DEFAULT_CRITERION.apply("Die Methode outgoingArcs in NodePointerPoint2D ist vollständig korrekt implementiert.",
        () -> NodePointerPoint2DTest.class.getDeclaredMethod("testOutgoingArcs", HashMap.class, HashMap.class, Point2DCollection.class));

    private static final Criterion H8_3 = Criterion.builder()
        .shortDescription("Die Methode NodePointerPoint2D ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerPoint2DTest.class.getDeclaredMethod("testConstructor", HashMap.class, HashMap.class, Point2DCollection.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerPoint2DTest.class.getDeclaredMethod("testPredecessor", HashMap.class, HashMap.class, Point2DCollection.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerPoint2DTest.class.getDeclaredMethod("testDistance", HashMap.class, HashMap.class, Point2DCollection.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> NodePointerPoint2DTest.class.getDeclaredMethod("testOutgoingArcs", HashMap.class, HashMap.class, Point2DCollection.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H8_4 = Criterion.builder()
        .shortDescription("Die Methode ArcPointerPoint2D ist vollständig korrekt implementiert.")
        .grader(Grader.testAwareBuilder()
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerPoint2DTest.class.getDeclaredMethod("testConstructor", HashMap.class, HashMap.class, Point2DCollection.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerPoint2DTest.class.getDeclaredMethod("testGetLength", HashMap.class, HashMap.class, Point2DCollection.class)))
            .requirePass(JUnitTestRef.ofMethod(() -> ArcPointerPoint2DTest.class.getDeclaredMethod("testGetDestination", HashMap.class, HashMap.class, Point2DCollection.class)))
            .pointsFailedMin()
            .pointsPassedMax().build())
        .build();

    private static final Criterion H8 = Criterion.builder()
        .shortDescription("H8 | Symmetrischer Graph auf Ortskoordinaten")
        .addChildCriteria(H8_1, H8_2, H8_3, H8_4)
        .build();

    private static final Rubric RUBRIC = Rubric.builder()
        .title("H07 | Dijkstra und Heaps")
        .addChildCriteria(H2, H3, H4, H5, H6, H7, H8)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }

    @Override
    public void configure(RubricConfiguration configuration) {
        configuration.addTransformer(new BytecodeTransformations());
        configuration.addTransformer(new AccessTransformer());
    }
}
