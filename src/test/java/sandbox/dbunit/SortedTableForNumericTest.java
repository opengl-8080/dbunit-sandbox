package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.dbunit.Assertion.assertEquals;

public class SortedTableForNumericTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.ddl("""
        create table test_table (
            id integer primary key,
            text varchar(8),
            numeric integer
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUp = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/SortedTableForNumericTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUp);
        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void testDefault() throws Exception {
        ITable actualTestTable = myDbUnitExtension.getConnection().createDataSet().getTable("test_table");
        SortedTable sortedActualTestTable = new SortedTable(actualTestTable, new String[]{"numeric", "text"});

        ITable expectedTestTable = myDbUnitExtension
                                        .readXmlDataSet("/sandbox/dbunit/SortedTableForNumericTest/expected.xml")
                                        .getTable("test_table");

        assertEquals(expectedTestTable, sortedActualTestTable);
    }

    @Test
    void testUseComparableTrue() throws Exception {
        ITable actualTestTable = myDbUnitExtension.getConnection().createDataSet().getTable("test_table");
        SortedTable sortedActualTestTable = new SortedTable(actualTestTable, new String[]{"numeric", "text"});
        sortedActualTestTable.setUseComparable(true);

        ITable expectedTestTable = myDbUnitExtension
                .readXmlDataSet("/sandbox/dbunit/SortedTableForNumericTest/expected.xml")
                .getTable("test_table");

        assertEquals(expectedTestTable, sortedActualTestTable);
    }
}
