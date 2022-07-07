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
        create table foo_table (
            id integer primary key,
            value varchar(8)
        )""");
        myDbUnitExtension.ddl("""
        create table bar_table (
            id integer primary key,
            foo_id integer,
            foreign key (foo_id) references foo_table (id)
        )""");
    }

    @Test
    void test() throws Exception {
        try (InputStream inputStream = this.getClass().getResourceAsStream("/sandbox/dbunit/XmlDataSetTest/test.xml");) {
            XmlDataSet xmlDataSet = new XmlDataSet(inputStream);
            myDbUnitExtension.getDatabaseTester().setDataSet(xmlDataSet);
            myDbUnitExtension.getDatabaseTester().onSetup();
        }

        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }
}
