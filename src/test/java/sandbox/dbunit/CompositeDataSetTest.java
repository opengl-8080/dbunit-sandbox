package sandbox.dbunit;

import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class CompositeDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.sql("""
        create table foo_table (
            id integer primary key,
            value varchar(8)
        )""");
        myDbUnitExtension.sql("""
        create table bar_table (
            id integer primary key,
            foo_id integer,
            foreign key (foo_id) references foo_table (id)
        )""");
    }

    @Test
    void test() throws Exception {
        XmlDataSet dataSet1 = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CompositeDataSetTest/dataSet1.xml");
        XmlDataSet dataSet2 = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/CompositeDataSetTest/dataSet2.xml");

        CompositeDataSet compositeDataSet = new CompositeDataSet(dataSet1, dataSet2);
        myDbUnitExtension.getDatabaseTester().setDataSet(compositeDataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }
}
