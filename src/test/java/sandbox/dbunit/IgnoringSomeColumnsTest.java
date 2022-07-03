package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.dbunit.Assertion.assertEquals;

public class IgnoringSomeColumnsTest extends AbstractDbUnitTest {
    @BeforeAll
    static void beforeAll() throws Exception {
        // DB初期化(テーブル作成)
        ddl("""
        create table foo_table (
            id integer primary key,
            text varchar(32),
            numeric integer
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = readXmlDataSet("/sandbox/dbunit/IgnoringSomeColumnsTest/setUp.xml");
        databaseTester.setDataSet(setUpDataSet);

        databaseTester.onSetup();
    }

    @Test
    void test() throws Exception {
        ITable actualFooTable = getConnection().createDataSet().getTable("foo_table");

        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/IgnoringSomeColumnsTest/expected.xml");
        ITable expectedFooTable = expected.getTable("foo_table");

        ITable filteredActualFooTable = DefaultColumnFilter
                .includedColumnsTable(actualFooTable, expectedFooTable.getTableMetaData().getColumns());

        assertEquals(expectedFooTable, filteredActualFooTable);
    }
}
