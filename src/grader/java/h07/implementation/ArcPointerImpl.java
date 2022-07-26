package h07.implementation;

import h07.ArcPointer;
import h07.NodePointer;

public class ArcPointerImpl implements ArcPointer<Integer, Integer> {

    public Integer length;
    public NodePointer<Integer, Integer> destination;

    public ArcPointerImpl() {}

    public ArcPointerImpl(Integer length, NodePointer<Integer, Integer> destination) {
        this.length = length;
        this.destination = destination;
    }

    @Override
    public Integer getLength() {
        return length;
    }

    @Override
    public NodePointer<Integer, Integer> destination() {
        return destination;
    }
}
