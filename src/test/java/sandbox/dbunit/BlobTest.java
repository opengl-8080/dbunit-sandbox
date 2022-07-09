package sandbox.dbunit;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BlobTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("""
        create table test_table (
            id integer primary key,
            text blob,
            base64 blob,
            file blob,
            url blob
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/BlobTest/setUp.xml");
        myDbUnitExtension.getDatabaseTester().setDataSet(setUpDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void test() throws Exception {
        ITable testTable = myDbUnitExtension.getConnection().createDataSet().getTable("test_table");

        assertEquals(toString(testTable.getValue(0, "text")), "あいうえお");
        assertEquals(toString(testTable.getValue(0, "base64")), "かきくけこ");
        assertEquals(toString(testTable.getValue(0, "file")), "さしすせそ");
        assertEquals(toString(testTable.getValue(0, "url")), "たちつてと");
    }

    private String toString(Object value) {
        byte[] bytes = (byte[]) value;
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
