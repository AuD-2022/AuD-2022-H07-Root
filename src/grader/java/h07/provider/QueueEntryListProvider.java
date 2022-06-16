package h07.provider;

import h07.implementation.QueueEntry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class QueueEntryListProvider extends AbstractProvider {

    public static final int LIST_SIZE = 50; // > 0

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Stream<List<QueueEntry>> arguments = Stream.empty();

        for (int i = 0; i < ARGUMENT_COUNT; i++) {
            arguments = Stream.concat(arguments, Stream.of(QueueEntry
                .createRandomEntryStream()
                .limit(LIST_SIZE)
                .distinct()
                .sorted(QueueEntry.CMP.reversed())
                .collect(Collectors.toList())));
        }

        return arguments.map(Arguments::of);
    }
}

