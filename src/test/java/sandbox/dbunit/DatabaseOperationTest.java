package sandbox.dbunit;

import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class DatabaseOperationTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("""
        create table foo_table (
            id integer primary key,
            value varchar(32)
        )""");
        myDbUnitExtension.sql("""
        create table bar_table (
            id integer primary key,
            foo_id integer,
            foreign key (foo_id) references foo_table (id)
        )""");
    }

    @BeforeEach
    void setUp() {
        myDbUnitExtension.sql("truncate table bar_table");
        myDbUnitExtension.sql("truncate table foo_table");

        myDbUnitExtension.sql("insert into foo_table values (9, 'HOGE')");
        myDbUnitExtension.sql("insert into foo_table values (99, 'FUGA')");
        myDbUnitExtension.sql("insert into bar_table values (10, 9)");
        myDbUnitExtension.sql("insert into bar_table values (100, 99)");

        System.out.println("[初期状態]");
        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }

    private void printTables() {
        System.out.println("[onSetup()実行後]");
        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }

    @Test
    void testUpdate() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.UPDATE);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testUpdate.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testCleanInsert() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testCleanInsert.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testInsert() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.INSERT);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testInsert.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testDelete() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.DELETE);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testDelete.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testDeleteAll() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.DELETE_ALL);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testDeleteAll.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testTruncate() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.TRUNCATE_TABLE);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testTruncate.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testRefresh() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.REFRESH);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testRefresh.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        printTables();
    }

    @Test
    void testManual() throws Exception {
        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testManual.xml");

        DatabaseOperation.CLEAN_INSERT.execute(myDbUnitExtension.getConnection(), xmlDataSet);

        System.out.println("execute()実行後");
        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }

    @Test
    void testOnTearDown() throws Exception {
        myDbUnitExtension.getDatabaseTester().setTearDownOperation(DatabaseOperation.UPDATE);

        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DatabaseOperationTest/testOnTearDown.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);

        myDbUnitExtension.getDatabaseTester().onTearDown();

        printTables();
    }
}
