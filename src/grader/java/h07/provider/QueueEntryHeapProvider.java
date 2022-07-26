package h07.provider;

import h07.implementation.PriorityQueueHeapImpl;
import h07.implementation.QueueEntry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static h07.TestConstants.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;

public class QueueEntryHeapProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        List<PriorityQueueHeapImpl<QueueEntry>> arguments = new ArrayList<>(ARGUMENT_COUNT);

        for (int i = 0; i < ARGUMENT_COUNT; i++) {

            PriorityQueueHeapImpl<QueueEntry> expectedQueue = new PriorityQueueHeapImpl<>(QUEUE_ENTRY_CMP, HEAP_CAPACITY);

            QueueEntry.createRandomEntryStream()
                .limit(HEAP_CAPACITY / 2)
                .distinct()
                .forEach(expectedQueue::add);

            arguments.add(expectedQueue);
        }

        return arguments.stream().map(Arguments::of);
    }
}
