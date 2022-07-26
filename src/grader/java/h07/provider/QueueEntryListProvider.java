package h07.provider;

import h07.implementation.QueueEntry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static h07.TestConstants.*;
import static h07.implementation.QueueEntry.QUEUE_ENTRY_CMP;


public class QueueEntryListProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Stream<List<QueueEntry>> arguments = Stream.empty();

        for (int i = 0; i < ARGUMENT_COUNT; i++) {
            arguments = Stream.concat(arguments, Stream.of(QueueEntry
                .createRandomEntryStream()
                .limit(LIST_SIZE)
                .distinct()
                .sorted(QUEUE_ENTRY_CMP.reversed())
                .collect(Collectors.toList())));
        }

        return arguments.map(Arguments::of);
    }
}

