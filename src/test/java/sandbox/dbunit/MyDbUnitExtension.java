package sandbox.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyDbUnitExtension implements BeforeAllCallback, AfterAllCallback {
    private IDatabaseTester databaseTester;
    private IDatabaseConnection connection;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        String testClassName = context.getRequiredTestClass().getSimpleName();
        databaseTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + testClassName );
//        databaseTester = new JdbcDatabaseTester("org.postgresql.Driver", "jdbc:postgresql://localhost/postgres", "postgres", "postgres");
        connection = databaseTester.getConnection();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public void ddl(String sql) {
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public XmlDataSet readXmlDataSet(String path) {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            return new XmlDataSet(inputStream);
        } catch (DataSetException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printTable(String tableName) {
        try {
            System.out.println(tableName + " {");
            ITable table = this.getConnection().createDataSet().getTable(tableName);
            ITableMetaData metaData = table.getTableMetaData();

            for (int row=0; row<table.getRowCount(); row++) {
                List<String> values = new ArrayList<>();
                for (Column column : metaData.getColumns()) {
                    Object value = table.getValue(row, column.getColumnName());
                    values.add(column.getColumnName() + "=" + format(value));
                }
                System.out.println("  " + String.join(", ", values));
            }
            System.out.println("}");
        } catch (DataSetException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String format(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return value.toString();
    }

    public IDatabaseTester getDatabaseTester() {
        return databaseTester;
    }

    public IDatabaseConnection getConnection() {
        return connection;
    }
}
