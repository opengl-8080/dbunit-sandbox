package sandbox.dbunit;

import org.dbunit.DefaultOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.InputStream;

public class XlsDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.ddl("""
        create table foo_table (
            id integer primary key,
            value_1 varchar(8),
            value_2 varchar(8)
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
        try (InputStream inputStream = this.getClass().getResourceAsStream("/sandbox/dbunit/XlsDataSetTest/test.xlsx");) {
            XlsDataSet xlsDataSet = new XlsDataSet(inputStream);
            myDbUnitExtension.getDatabaseTester().setDataSet(xlsDataSet);
            myDbUnitExtension.getDatabaseTester().onSetup();
        }

        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }

    @Test
    void testWithReplacementDataSet() throws Exception {
        myDbUnitExtension.getDatabaseTester().setOperationListener(new DefaultOperationListener() {
            @Override
            public void connectionRetrieved(IDatabaseConnection connection) {
                super.connectionRetrieved(connection);
                final DatabaseConfig config = connection.getConfig();
                config.setProperty("http://www.dbunit.org/features/allowEmptyFields", true);
            }
        });

        try (InputStream inputStream = this.getClass().getResourceAsStream("/sandbox/dbunit/XlsDataSetTest/test.xlsx");) {
            XlsDataSet xlsDataSet = new XlsDataSet(inputStream);
            final ReplacementDataSet replacementDataSet = new ReplacementDataSet(xlsDataSet);
            replacementDataSet.addReplacementObject(null, "");
            myDbUnitExtension.getDatabaseTester().setDataSet(replacementDataSet);
            myDbUnitExtension.getDatabaseTester().onSetup();
        }

        myDbUnitExtension.printTable("foo_table");
    }
}
