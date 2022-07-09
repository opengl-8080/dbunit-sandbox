package sandbox.dbunit;

import org.dbunit.DatabaseUnitException;
import org.dbunit.assertion.comparer.value.ValueComparer;
import org.dbunit.assertion.comparer.value.ValueComparerTemplateBase;
import org.dbunit.assertion.comparer.value.ValueComparers;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static org.dbunit.Assertion.assertWithValueComparer;

public class ValueComparerTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("""
        create table test_table (
            id integer primary key,
            text varchar(32),
            numeric integer,
            timestamp_value timestamp
        )""");
    }

    @BeforeEach
    void setUp() throws Exception {
        XmlDataSet setUp = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUp);
        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/expected.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "numeric", ValueComparers.isActualGreaterThanExpected,
            "timestamp_value", ValueComparers.isActualWithinOneSecondNewerOfExpectedTimestamp
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testGreaterThanOrEqual() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testGreaterThanOrEqual.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "numeric", ValueComparers.isActualGreaterThanOrEqualToExpected
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testLessThan() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testLessThan.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "numeric", ValueComparers.isActualLessThanExpected
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testLessOrEqualThan() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testLessOrEqualThan.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "numeric", ValueComparers.isActualLessOrEqualToThanExpected
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testContainingExpectedString() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testContainingExpectedString.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "numeric", ValueComparers.isActualContainingExpectedStringValueComparer,
            "text", ValueComparers.isActualContainingExpectedStringValueComparer
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testWithinOneSecondNewer() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testWithinOneSecondNewer.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "timestamp_value", ValueComparers.isActualWithinOneSecondNewerOfExpectedTimestamp
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testWithinOneSecondOlder() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testWithinOneSecondOlder.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "timestamp_value", ValueComparers.isActualWithinOneSecondOlderOfExpectedTimestamp
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        // 絶対バグってる...
        // https://sourceforge.net/p/dbunit/code.git/ci/master/tree/src/test/java/org/dbunit/assertion/comparer/value/IsActualWithinToleranceOfExpectedTimestampValueComparerTest.java
        // テストケース見ても、 lowToleranceValueInMillis の方が大きいようなケースがない
        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testWithinOneMinuteNewer() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testWithinOneMinuteNewer.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "timestamp_value", ValueComparers.isActualWithinOneMinuteNewerOfExpectedTimestamp
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testNotEqualTo() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testNotEqualTo.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "text", ValueComparers.isActualNotEqualToExpected
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }

    @Test
    void testCustomValueComparer() throws Exception {
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/ValueComparerTest/testCustomValueComparer.xml");
        ITable expectedTable = expected.getTable("test_table");

        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualTable = actual.getTable("test_table");

        Map<String, ValueComparer> comparers = Map.of(
            "text", new ValueComparerTemplateBase() {
                @Override
                protected boolean isExpected(ITable expectedTable, ITable actualTable, int rowNum, String columnName, DataType dataType, Object expectedValue, Object actualValue) throws DatabaseUnitException {
                    if (expectedValue == null || actualValue == null) {
                        return expectedValue == actualValue;
                    }
                    return expectedValue.toString().equalsIgnoreCase(actualValue.toString());
                }

                @Override
                protected String getFailPhrase() {
                    return "not equals ignore case";
                }
            }
        );
        ValueComparer defaultComparer = ValueComparers.isActualEqualToExpected;

        assertWithValueComparer(expectedTable, actualTable, defaultComparer, comparers);
    }
}
