package sandbox.dbunit;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.dbunit.Assertion.assertEquals;

public class RowOrderingTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.sql("""
        create table test_table (
            id integer primary key,
            value varchar(8)
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUp = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/RowOrderingTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUp);
        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/RowOrderingTest/expected.xml");

        assertEquals(expected, actual);
    }

    @Test
    void testSortedTable() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        SortedTable sortedActualTestTable =
            new SortedTable(actual.getTable("test_table"), new String[]{"value"});

        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/RowOrderingTest/expected.xml");
        ITable expectedTestTable = expected.getTable("test_table");

        assertEquals(expectedTestTable, sortedActualTestTable);
    }
}
