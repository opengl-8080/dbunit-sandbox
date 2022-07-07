package sandbox.dbunit;

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Method;
import java.net.URL;

public class FlatXmlDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.ddl("""
        create table foo_table (
            id integer primary key,
            value varchar(32)
        )""");
        myDbUnitExtension.ddl("""
        create table bar_table (
            id integer primary key,
            foo_id integer,
            foreign key (foo_id) references foo_table (id)
        )""");
    }

    @Test
    void testStandard(TestInfo testInfo) throws Exception {
        load(testInfo);

        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }

    @Test
    void testFirstRecordHasNull(TestInfo testInfo) throws Exception {
        load(testInfo);

        myDbUnitExtension.printTable("foo_table");
    }

    @Test
    void testCaseSensingTrue() throws Exception {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();

        builder.setColumnSensing(true); // true を設定

        URL xml = this.getClass().getResource("/sandbox/dbunit/FlatXmlDataSetTest/testFirstRecordHasNull.xml");
        FlatXmlDataSet dataSet = builder.build(xml);
        myDbUnitExtension.getDatabaseTester().setDataSet(dataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("foo_table");
    }

    private void load(TestInfo testInfo) throws Exception {
        String methodName = testInfo.getTestMethod().map(Method::getName).orElseThrow();
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        URL xml = this.getClass().getResource("/sandbox/dbunit/FlatXmlDataSetTest/" + methodName + ".xml");
        FlatXmlDataSet dataSet = builder.build(xml);
        myDbUnitExtension.getDatabaseTester().setDataSet(dataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();
    }
}
