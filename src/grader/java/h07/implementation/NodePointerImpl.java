package h07.implementation;

import h07.ArcPointer;
import h07.NodePointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodePointerImpl implements NodePointer<Integer, Integer> {

    public Integer distance;
    public NodePointer<Integer, Integer> predecessor;
    public List<ArcPointerImpl> outgoingArcs = new ArrayList<>();
    public final int id;
    public static int nextId = 0;

    public NodePointerImpl() {
        id = nextId++;
    }

    public NodePointerImpl(int distance) {
        this();
        this.distance = distance;
    }

    public NodePointerImpl(int distance, NodePointer<Integer, Integer> predecessor, List<ArcPointerImpl> outgoingArcs) {
        this();
        this.distance = distance;
        this.predecessor = predecessor;
        this.outgoingArcs = outgoingArcs;
    }

    public static void resetIds() {
        nextId = 0;
    }

    public void reset() {
        distance = null;
        predecessor = null;
    }

    @Override
    public @Nullable Integer getDistance() {
        return distance;
    }

    @Override
    public void setDistance(@NotNull Integer distance) {
        this.distance = distance;
    }

    @Override
    public @Nullable NodePointer<Integer, Integer> getPredecessor() {
        return predecessor;
    }

    @Override
    public void setPredecessor(@NotNull NodePointer<Integer, Integer> predecessor) {
        this.predecessor = predecessor;
    }

    @Override
    public Iterator<ArcPointer<Integer, Integer>> outgoingArcs() {
        return outgoingArcs.stream().map(arcImpl -> (ArcPointer<Integer, Integer>) arcImpl).toList().iterator();
    }

    public void addOutgoingArc(ArcPointerImpl arcPointer) {
        outgoingArcs.add(arcPointer);
    }

    @Override
    public String toString() {
        return "NodePointerImpl{" +
            "id=" + id +
            '}';
    }

}
