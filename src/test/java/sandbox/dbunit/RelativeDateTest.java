package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class RelativeDateTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.ddl("""
        create table test_table (
            id integer primary key,
            now timestamp,
            date_value date,
            time_value time,
            timestamp_value timestamp
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/RelativeDateTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUpDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        ITable testTable = myDbUnitExtension.getConnection().createDataSet().getTable("test_table");

        System.out.println("[now]             = " + testTable.getValue(0, "now"));
        System.out.println("[now+2d]          = " + testTable.getValue(0, "date_value"));
        System.out.println("[now-3h+20m]      = " + testTable.getValue(0, "time_value"));
        System.out.println("[now+2y+1M 10:00] = " + testTable.getValue(0, "timestamp_value"));
    }
}
