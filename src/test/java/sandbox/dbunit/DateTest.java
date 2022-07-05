package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.ddl("""
        create table test_table (
            id integer primary key,
            date_value date,
            time_value time,
            timestamp_value timestamp
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/DateTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUpDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        ITable testTable = myDbUnitExtension.getConnection().createDataSet().getTable("test_table");

        Date dateValue = (Date) testTable.getValue(0, "date_value");
        Time timeValue = (Time) testTable.getValue(0, "time_value");
        Timestamp timestampValue = (Timestamp) testTable.getValue(0, "timestamp_value");

        // 以下のテストは全て成功する
        assertEquals(dateValue, Date.valueOf("2022-07-12"));
        assertEquals(timeValue, Time.valueOf("12:13:14"));
        assertEquals(timestampValue, Timestamp.valueOf("2022-08-01 12:30:42.123"));
    }
}
