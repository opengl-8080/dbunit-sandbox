package sandbox.dbunit;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.dbunit.Assertion.assertEquals;

public class AssertSpecifiedTableTest {

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
        myDbUnitExtension.sql("""
        create table bar_table (
            id integer primary key,
            text varchar(32)
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/AssertSpecifiedTableTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUpDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void testGetTable() throws Exception {
        IDataSet actual = myDbUnitExtension.getConnection().createDataSet();
        ITable actualFooTable = actual.getTable("foo_table");

        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/AssertSpecifiedTableTest/expected.xml");
        ITable expectedFooTable = expected.getTable("foo_table");

        assertEquals(expectedFooTable, actualFooTable);
    }

    @Test
    void testCreateQueryTable() throws Exception {
        ITable actualFooTable = myDbUnitExtension.getConnection().createQueryTable("foo_table", "select * from foo_table order by id");

        XmlDataSet expected = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/AssertSpecifiedTableTest/expected.xml");
        ITable expectedFooTable = expected.getTable("foo_table");

        assertEquals(expectedFooTable, actualFooTable);
    }
}
