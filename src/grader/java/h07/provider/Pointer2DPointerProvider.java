package h07.provider;

import h07.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class Pointer2DPointerProvider extends AbstractProvider {

    public static final int POINTS_COUNT = 20;
    public static final int MAX_ARC_LENGTH = 10;
    public static final int MIN_CORD = -10;
    public static final int MAX_CORD = 10;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        List<Object[]> arguments = new ArrayList<>(ARGUMENT_COUNT);

        for (int i = 0; i < ARGUMENT_COUNT; i++) {
            List<Point2D> existingPoints = new ArrayList<>(POINTS_COUNT);

            for (int j = 0; j < POINTS_COUNT - 1; j++) existingPoints.add(new Point2D(RANDOM.nextDouble(MIN_CORD, MAX_CORD), RANDOM.nextDouble(MIN_CORD, MAX_CORD)));
            //add a point with MaxArcLength distance to the first point
            existingPoints.add(new Point2D(existingPoints.get(0).getX(), existingPoints.get(0).getY() + MAX_ARC_LENGTH));


            Point2DCollection collection = new Point2DCollection(existingPoints, MAX_ARC_LENGTH);

            HashMap<Point2D, NodePointerPoint2D> existingNodePointers = new HashMap<>();
            HashMap<Pair<Point2D, Point2D>, ArcPointerPoint2D> existingArcPointers = new HashMap<>();

            //create mocked NodePointer instances
            for (Point2D point : existingPoints) {
                existingNodePointers.put(point, spy(new NodePointerPoint2D(existingNodePointers, existingArcPointers, point, collection)));
            }

            //create mocked arcPointer instances
            for (Point2D from : existingPoints) {
                for (Point2D to : existingPoints) {
                    double length = Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2));
                    if (length > MAX_ARC_LENGTH) continue;
                    ArcPointerPoint2D mock = spy(new ArcPointerPoint2D(existingNodePointers, existingArcPointers, from, to, collection));
                    when(mock.getLength()).thenReturn(length);
                    when(mock.destination()).thenReturn(existingNodePointers.get(to));
                    existingArcPointers.put(new Pair<>(from, to), mock);
                }
            }

            //mock outgoingArcs method
            for (Point2D point : existingPoints) {
                NodePointerPoint2D mock = existingNodePointers.get(point);
                List<Point2D> otherPoints = new ArrayList<>(existingPoints);
                otherPoints.remove(point);
                List<ArcPointer<Double, Double>> outgoingArcs = otherPoints.stream()
                    .filter(destination -> existingArcPointers.containsKey(new Pair<>(point, destination)))
                    .map(destination -> (ArcPointer<Double, Double>) existingArcPointers.get(new Pair<>(point, destination))).toList();
                when(mock.outgoingArcs()).thenReturn(outgoingArcs.iterator());
            }


            //set fields of mocked nodePointers
            for (Map.Entry<Point2D, NodePointerPoint2D> entry : existingNodePointers.entrySet()) {
                try {
                    Field existingNodePointersField = NodePointerPoint2D.class.getDeclaredField("existingNodePointers");
                    existingNodePointersField.setAccessible(true);
                    existingNodePointersField.set(entry.getValue(), existingNodePointers);

                    Field existingArcPointersField = NodePointerPoint2D.class.getDeclaredField("existingArcPointers");
                    existingArcPointersField.setAccessible(true);
                    existingArcPointersField.set(entry.getValue(), existingArcPointers);

                    Field adjacencyMatrixField = NodePointerPoint2D.class.getDeclaredField("collection");
                    adjacencyMatrixField.setAccessible(true);
                    adjacencyMatrixField.set(entry.getValue(), collection);

                    Field row = NodePointerPoint2D.class.getDeclaredField("point");
                    row.setAccessible(true);
                    row.set(entry.getValue(), entry.getKey());

                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException("unable to set fields of class NodePointerPoint2D");
                }
            }

            //set fields of mocked arcPointers
            for (Map.Entry<Pair<Point2D, Point2D>, ArcPointerPoint2D> entry : existingArcPointers.entrySet()) {
                try {
                    Field existingNodePointersField = ArcPointerPoint2D.class.getDeclaredField("existingNodePointers");
                    existingNodePointersField.setAccessible(true);
                    existingNodePointersField.set(entry.getValue(), existingNodePointers);

                    Field existingArcPointersField = ArcPointerPoint2D.class.getDeclaredField("existingArcPointers");
                    existingArcPointersField.setAccessible(true);
                    existingArcPointersField.set(entry.getValue(), existingArcPointers);

                    Field adjacencyMatrixField = ArcPointerPoint2D.class.getDeclaredField("collection");
                    adjacencyMatrixField.setAccessible(true);
                    adjacencyMatrixField.set(entry.getValue(), collection);

                    Field row = ArcPointerPoint2D.class.getDeclaredField("source");
                    row.setAccessible(true);
                    row.set(entry.getValue(), entry.getKey().getElement1());

                    Field column = ArcPointerPoint2D.class.getDeclaredField("destination");
                    column.setAccessible(true);
                    column.set(entry.getValue(), entry.getKey().getElement2());
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException("unable to set fields of class ArcPointerPoint2D");
                }
            }

            arguments.add(new Object[]{existingNodePointers, existingArcPointers, collection});
        }
        return arguments.stream().map(Arguments::of);
    }
}
