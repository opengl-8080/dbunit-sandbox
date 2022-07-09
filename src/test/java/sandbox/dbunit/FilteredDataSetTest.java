package sandbox.dbunit;

import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.filter.IncludeTableFilter;
import org.dbunit.dataset.xml.XmlDataSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 各テストは単独で動かすと期待通りにならない可能性があるので注意。
 * FilteredDataSet でフィルターされた結果は、 CLEAN_INSERT の CLEAN のときにも適用されてしまうので、
 * 除外されたテーブルは CLEAN が実行されず前のテストのデータが残るため。
 */
public class FilteredDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("""
        create table hoge_table (
            id integer primary key,
            value varchar(32)
        )""");
        myDbUnitExtension.sql("""
        create table fuga_table (
            id integer primary key,
            value varchar(32)
        )""");
        myDbUnitExtension.sql("""
        create table piyo_table (
            id integer primary key,
            hoge_id integer,
            foreign key (hoge_id) references hoge_table (id)
        )""");
    }

    @Test
    void testStandard() throws Exception {
        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/FilteredDataSetTest/testStandard.xml");
        FilteredDataSet filteredDataSet = new FilteredDataSet(new String[]{"hoge_table", "piyo_table"}, xmlDataSet);
        myDbUnitExtension.getDatabaseTester().setDataSet(filteredDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("hoge_table");
        myDbUnitExtension.printTable("fuga_table");
        myDbUnitExtension.printTable("piyo_table");
    }

    @Test
    void testIncludeFilter() throws Exception {
        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/FilteredDataSetTest/testIncludeFilter.xml");
        IncludeTableFilter filter = new IncludeTableFilter(new String[]{"piyo_table", "hoge_table"});
        FilteredDataSet filteredDataSet = new FilteredDataSet(filter, xmlDataSet);
        myDbUnitExtension.getDatabaseTester().setDataSet(filteredDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("hoge_table");
        myDbUnitExtension.printTable("fuga_table");
        myDbUnitExtension.printTable("piyo_table");
    }

    @Test
    void testExcludeFilter() throws Exception {
        XmlDataSet xmlDataSet = myDbUnitExtension.readXmlDataSet("/sandbox/dbunit/FilteredDataSetTest/testExcludeFilter.xml");
        ExcludeTableFilter filter = new ExcludeTableFilter(new String[]{"piyo_table", "hoge_table"});
        FilteredDataSet filteredDataSet = new FilteredDataSet(filter, xmlDataSet);
        myDbUnitExtension.getDatabaseTester().setDataSet(filteredDataSet);
        myDbUnitExtension.getDatabaseTester().onSetup();

        myDbUnitExtension.printTable("hoge_table");
        myDbUnitExtension.printTable("fuga_table");
        myDbUnitExtension.printTable("piyo_table");
    }

    @Test
    void testPattern() throws Exception {
        IncludeTableFilter filterWithAsterisk = new IncludeTableFilter(new String[]{"*d"});

        assertFalse(filterWithAsterisk.accept("first"));
        assertTrue(filterWithAsterisk.accept("second"));
        assertTrue(filterWithAsterisk.accept("third"));
        assertFalse(filterWithAsterisk.accept("forth"));
        assertFalse(filterWithAsterisk.accept("fifth"));

        IncludeTableFilter filterWithQuestion = new IncludeTableFilter(new String[]{"f????"});

        assertTrue(filterWithQuestion.accept("first"));
        assertFalse(filterWithQuestion.accept("second"));
        assertFalse(filterWithQuestion.accept("third"));
        assertTrue(filterWithQuestion.accept("forth"));
        assertTrue(filterWithQuestion.accept("fifth"));

        IncludeTableFilter filterWithPatterns = new IncludeTableFilter(new String[]{"s*", "*th"});

        assertFalse(filterWithPatterns.accept("first"));
        assertTrue(filterWithPatterns.accept("second"));
        assertFalse(filterWithPatterns.accept("third"));
        assertTrue(filterWithPatterns.accept("forth"));
        assertTrue(filterWithPatterns.accept("fifth"));
    }
}
