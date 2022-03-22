package h07;

import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricForSubmission;
import org.sourcegrade.jagr.api.rubric.RubricProvider;

@RubricForSubmission("h07")
public class H07_RubricProvider implements RubricProvider {

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H07")
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}
