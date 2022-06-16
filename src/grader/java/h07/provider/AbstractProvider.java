package h07.provider;

import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Random;

public abstract class AbstractProvider implements ArgumentsProvider {

    public static final long SEED = 0L;
    public static final int ARGUMENT_COUNT = 5;
    public static final Random RANDOM = new Random(SEED);

}
