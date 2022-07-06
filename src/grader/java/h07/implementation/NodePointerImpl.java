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
    public String name;
    public static int nextId = 0;

    public NodePointerImpl() {
        id = nextId++;
        this.name = "NodePointerImpl{id=%d}".formatted(id);
    }

    public NodePointerImpl(Integer distance) {
        this();
        this.distance = distance;
    }

    public NodePointerImpl(Integer distance, NodePointer<Integer, Integer> predecessor, List<ArcPointerImpl> outgoingArcs) {
        this();
        this.distance = distance;
        this.predecessor = predecessor;
        this.outgoingArcs = outgoingArcs;
    }

    private NodePointerImpl(Integer distance, NodePointer<Integer, Integer> predecessor, List<ArcPointerImpl> outgoingArcs, String name, int id) {
        this.id = id;
        this.name = name;
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

    public NodePointerImpl setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "NodePointerImpl{" +
            "id=" + id +
            ", predecessor=" + (predecessor == null ? "null" : predecessor instanceof NodePointerImpl pred ? pred.name : predecessor.toString()) +
            ", distance=" + distance +
            ", outgoingArcs=" + outgoingArcsToString() +
            '}';
    }

    private String outgoingArcsToString() {
        StringBuilder str = new StringBuilder("[");

        for (int i = 0; i < outgoingArcs.size(); i++) {
            NodePointer<Integer, Integer> destination = outgoingArcs.get(i).destination;
            str.append("{destination=");
            str.append(destination instanceof NodePointerImpl dest ? dest.name : destination.toString());
            str.append(", length=");
            str.append(outgoingArcs.get(i).length);
            str.append("}");
            if (i < outgoingArcs.size() - 2) str.append(", ");
        }

        str.append("]");
        return str.toString();
    }

    @Override
    public NodePointerImpl clone() {
        return new NodePointerImpl(distance, predecessor, new ArrayList<>(outgoingArcs), name, id);
    }
}
