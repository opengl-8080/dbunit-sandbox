package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.dbunit.Assertion.assertEquals;

public class IgnoringSomeColumnsTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("""
        create table foo_table (
            id integer primary key,
            text varchar(32),
            numeric integer
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/IgnoringSomeColumnsTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUpDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        ITable actualFooTable = myDbUnitExtension.getConnection().createDataSet().getTable("foo_table");

        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/IgnoringSomeColumnsTest/expected.xml");
        ITable expectedFooTable = expected.getTable("foo_table");

        ITable filteredActualFooTable = DefaultColumnFilter
                .includedColumnsTable(actualFooTable, expectedFooTable.getTableMetaData().getColumns());

        assertEquals(expectedFooTable, filteredActualFooTable);
    }
}
