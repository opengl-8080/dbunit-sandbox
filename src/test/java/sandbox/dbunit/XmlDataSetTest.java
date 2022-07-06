package sandbox.dbunit;

import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.InputStream;

public class XmlDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.ddl("""
        create table test_table (
            id integer primary key,
            value1 varchar(8),
            value2 varchar(8)
        )""");
    }

    @Test
    void test() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/sandbox/dbunit/XmlDataSetTest/test.xml");) {
            XmlDataSet xmlDataSet = new XmlDataSet(inputStream);
            myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
            myDbUnitExtension.getDatabaseTester().onSetup();
        }

        myDbUnitExtension.printTable("test_table");
    }
}
