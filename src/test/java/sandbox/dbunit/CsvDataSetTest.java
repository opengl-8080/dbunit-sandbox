package sandbox.dbunit;

import org.dbunit.DefaultOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.csv.CsvURLDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URL;

public class CsvDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        myDbUnitExtension.ddl("""
        create table foo_table (
            id integer primary key,
            value_1 varchar(32),
            value_2 varchar(32)
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
        myDbUnitExtension.getDatabaseTester().setOperationListener(new DefaultOperationListener() {
            @Override
            public void connectionRetrieved(IDatabaseConnection connection) {
                super.connectionRetrieved(connection);
                final DatabaseConfig config = connection.getConfig();
                config.setProperty("http://www.dbunit.org/features/allowEmptyFields", true);
            }
        });

        URL base = this.getClass().getResource("/sandbox/dbunit/CsvDataSetTest/");

        CsvURLDataSet csvURLDataSet = new CsvURLDataSet(base);
        myDbUnitExtension.getDatabaseTester().setDataSet(csvURLDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("foo_table");
        myDbUnitExtension.printTable("bar_table");
    }
}
