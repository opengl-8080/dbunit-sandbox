package sandbox.dbunit;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.dbunit.Assertion.assertEquals;

public class EqualityComparisonTest extends AbstractDbUnitTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        // DB初期化(テーブル作成)
        ddl("""
        create table foo_table (
            id integer primary key,
            text varchar(32),
            numeric integer
        )""");
        ddl("""
        create table bar_table (
            id integer primary key,
            text varchar(32)
        )""");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        XmlDataSet setUpDataSet = readXmlDataSet("/sandbox/dbunit/EqualityComparisonTest/setUp.xml");
        databaseTester.setDataSet(setUpDataSet);

        databaseTester.onSetup();
    }

    @Test
    void expectedの方がカラムが多い() throws Exception {
        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/EqualityComparisonTest/expectedの方がカラムが多い.xml");
        IDataSet actual = getConnection().createDataSet();

        assertEquals(expected, actual);
    }

    @Test
    void expectedの方がカラムが少ない() throws Exception {
        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/EqualityComparisonTest/expectedの方がカラムが少ない.xml");
        IDataSet actual = getConnection().createDataSet();

        assertEquals(expected, actual);
    }

    @Test
    void expectedの方がテーブルが多い() throws Exception {
        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/EqualityComparisonTest/expectedの方がテーブルが多い.xml");
        IDataSet actual = getConnection().createDataSet();

        assertEquals(expected, actual);
    }

    @Test
    void expectedの方がテーブルが少ない() throws Exception {
        XmlDataSet expected = readXmlDataSet("/sandbox/dbunit/EqualityComparisonTest/expectedの方がテーブルが少ない.xml");
        IDataSet actual = getConnection().createDataSet();

        assertEquals(expected, actual);
    }
}
