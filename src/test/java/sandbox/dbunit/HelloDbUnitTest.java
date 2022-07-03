package sandbox.dbunit;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.dbunit.Assertion.assertEquals;

public class HelloDbUnitTest {
    static IDatabaseTester databaseTester;
    static IDatabaseConnection connection;

    @BeforeAll
    static void beforeAll() throws Exception {
        databaseTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:test");
        connection = databaseTester.getConnection();

        // DB初期化(テーブル作成)
        Connection jdbcConnection = connection.getConnection();
        try (
            PreparedStatement ps = jdbcConnection.prepareStatement("""
            create table test_table (
                id integer primary key,
                value varchar(8)
            )""");
        ) {
            ps.execute();
        }
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = readXmlDataSet("/sandbox/dbunit/HelloDbUnitTest/test/setUp.xml");
        databaseTester.setDataSet(setUpDataSet);

        databaseTester.onSetup();
    }

    @Test
    void test() throws Exception {
        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/HelloDbUnitTest/test/expected.xml");
        IDataSet actual = connection.createDataSet();

        assertEquals(expected, actual);
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private XmlDataSet readXmlDataSet(String path) throws Exception  {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            return new XmlDataSet(inputStream);
        }
    }
}
