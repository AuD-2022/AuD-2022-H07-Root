package h07.provider;

import h07.implementation.ArcPointerImpl;
import h07.implementation.NodePointerImpl;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static h07.TestConstants.*;

public class PathProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {

        List<List<NodePointerImpl>> arguments = new ArrayList<>(ARGUMENT_COUNT);

        for (int i = 0; i < ARGUMENT_COUNT - 1; i++) {

            int pathLength = RANDOM.nextInt(MIN_PATH_LENGTH, MAX_PATH_LENGTH + 1);
            List<NodePointerImpl> nodes = new ArrayList<>(pathLength);

            for (int j = 0; j < pathLength; j++) {
                NodePointerImpl nextNode = new NodePointerImpl(1, j > 0 ? nodes.get(j - 1) : null, new ArrayList<>());
                if (j > 0) nodes.get(j - 1).addOutgoingArc(new ArcPointerImpl(1, nextNode));
                nodes.add(nextNode);
            }

            arguments.add(nodes);
        }

        arguments.add(new ArrayList<>(List.of(new NodePointerImpl(1, null, new ArrayList<>()))));

        return arguments.stream().map(Arguments::of);
    }

}
