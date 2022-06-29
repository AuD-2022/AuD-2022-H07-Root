package h07.implementation;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static h07.TestConstants.*;
import static org.junit.jupiter.api.Assertions.fail;

public class QueueEntry {

    public static final Comparator<QueueEntry> QUEUE_ENTRY_CMP = Comparator.comparingInt((QueueEntry queueEntry) -> {
        if (queueEntry == null) fail("a queueEntry that was passed to the comparator is null");
        return queueEntry.value % 10;
    });
    public static final QueueEntry UNUSED_ENTRY = new QueueEntry(MAX_QUEUE_ENTRY_VALUE + 1, -1);
    public static int nextID = 0;

    public final int value;
    public final int id;

    public QueueEntry(int value) {
        this.value = value;
        this.id = nextID++;
    }

    public QueueEntry(int value, int id) {
        this.value = value;
        this.id = id;
    }

    public static void reset() {
        nextID = 0;
    }

    public static QueueEntry createRandomEntry() {
        return new QueueEntry(RANDOM.nextInt(MAX_QUEUE_ENTRY_VALUE + 1));
    }

    public static Stream<QueueEntry> createRandomEntryStream() {
        return Stream.generate(QueueEntry::createRandomEntry);
    }

    @Override
    public String toString() {
        return "QueueEntry{" +
            "id=" + id +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueueEntry that = (QueueEntry) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
