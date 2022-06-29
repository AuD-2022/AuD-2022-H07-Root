package h07.provider;

import h07.implementation.QueueEntry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static h07.TestConstants.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;

public class QueueEntryHeapProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        Stream<QueueEntry[]> arguments = Stream.empty();

        for (int i = 0; i < ARGUMENT_COUNT; i++) {
            QueueEntry[] array = QueueEntry.createRandomEntryStream()
                .limit(HEAP_CAPACITY / 2)
                .distinct()
                .sorted(QUEUE_ENTRY_CMP.reversed())
                .toArray(ignored -> new QueueEntry[HEAP_CAPACITY]);

            arguments = Stream.concat(arguments, Stream.<QueueEntry[]>of(array));
        }

        return arguments.map((QueueEntry[] entries) -> Arguments.of((Object) entries));
    }
}
