package sandbox.dbunit;

import org.dbunit.assertion.DiffCollectingFailureHandler;
import org.dbunit.assertion.Difference;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.dbunit.Assertion.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CollectDifferencesTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.ddl("""
        create table test_table (
            id integer primary key,
            value varchar(8)
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUp = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CollectDifferencesTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUp);
        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void testDefault() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CollectDifferencesTest/expected.xml");

        assertEquals(expected, actual);
    }

    @Test
    void testCollectDifferences() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CollectDifferencesTest/expected.xml");

        DiffCollectingFailureHandler failureHandler = new DiffCollectingFailureHandler();

        assertEquals(expected, actual, failureHandler);

        @SuppressWarnings("unchecked")
        List<Difference> diffList = failureHandler.getDiffList();

        if (!diffList.isEmpty()) {
            String errorMessage = diffList.stream()
                    .map(diff -> String.format("row=%d, column=%s, failMessage=%s",
                            diff.getRowIndex(),
                            diff.getColumnName(),
                            diff.getFailMessage()))
                    .collect(Collectors.joining("\n"));
            fail(errorMessage);
        }
    }

    @Test
    void testMyCollectDifferences() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CollectDifferencesTest/expected.xml");

        MyDiffCollectingFailureHandler failureHandler = new MyDiffCollectingFailureHandler();

        assertEquals(expected, actual, failureHandler);

        failureHandler.failIfExistsDifferences();
    }
}
