package sandbox.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDbUnitTest {
    protected static IDatabaseTester databaseTester;
    private static IDatabaseConnection dbUnitConnection;

    protected static void ddl(String sql) throws SQLException {
        try (PreparedStatement ps = dbUnitConnection.getConnection().prepareStatement(sql)) {
            ps.execute();
        }
    }

    @BeforeAll
    static void createJdbcDatabaseTester(TestInfo testInfo) throws Exception {
        String testClassName = testInfo.getTestClass().map(Class::getSimpleName).orElse("test");

        databaseTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:" + testClassName );
        dbUnitConnection = databaseTester.getConnection();
    }

    @AfterAll
    static void closeConnection() throws Exception {
        if (dbUnitConnection != null) {
            dbUnitConnection.close();
        }
    }

    protected IDatabaseConnection getConnection() {
        return dbUnitConnection;
    }

    protected XmlDataSet readXmlDataSet(String path) throws Exception  {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            return new XmlDataSet(inputStream);
        }
    }
}
