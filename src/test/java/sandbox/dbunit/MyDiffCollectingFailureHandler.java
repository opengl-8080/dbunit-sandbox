package sandbox.dbunit;

import org.dbunit.assertion.DefaultFailureHandler;
import org.dbunit.assertion.Difference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class MyDiffCollectingFailureHandler extends DefaultFailureHandler {

    private List<Difference> diffList = new ArrayList<>();

    @Override
    public void handle(Difference diff) {
        diffList.add(diff);
    }

    public void failIfExistsDifferences() {
        if (diffList.isEmpty()) {
            return;
        }
        String errorMessage = diffList.stream()
                .map(diff -> String.format("row=%d, column=%s, failMessage=%s",
                        diff.getRowIndex(),
                        diff.getColumnName(),
                        diff.getFailMessage()))
                .collect(Collectors.joining("\n"));
        fail(errorMessage);
    }
}
