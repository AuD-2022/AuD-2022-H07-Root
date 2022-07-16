package h07;

import java.util.Random;

public class TestConstants {

    //general
    public static final long SEED = 0L;
    public static final Random RANDOM = new Random(SEED);
    public static final int ARGUMENT_COUNT = 2;
    public static final int TEST_ITERATIONS = 5;

    //graphProvider
    public static final int MIN_NODE_COUNT = 5; // >= 2
    public static final int MAX_NODE_COUNT = 10;
    public static final int MAX_OUTGOING_ARC_COUNT = 2;
    public static final int MAX_NODE_DISTANCE = 100;

    //pathProvider
    public static final int MIN_PATH_LENGTH = 10;
    public static final int MAX_PATH_LENGTH = 50;

    //Points
    public static final int POINTS_COUNT_COLLECTION_TEST = 20;
    public static final int POINTS_COUNT = 5;
    public static final int MAX_ARC_LENGTH_POINT = 10;
    public static final int MIN_CORD = -10;
    public static final int MAX_CORD = 10;

    //HeapProvider
    public static final int HEAP_CAPACITY = 20;

    //ListProvider
    public static final int LIST_SIZE = 20;

    //QueueEntry
    public static final int MAX_QUEUE_ENTRY_VALUE = 100;
}
