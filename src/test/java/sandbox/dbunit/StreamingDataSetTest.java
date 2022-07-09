package sandbox.dbunit;

import org.dbunit.DefaultOperationListener;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.stream.StreamingDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.xml.sax.InputSource;

import java.net.URL;

/**
 * このクラスは PostgreSQL を使って動作させること。
 */
public class StreamingDataSetTest {
    @RegisterExtension
    static MyDbUnitExtension myDbUnitExtension = new MyDbUnitExtension();

    @BeforeAll
    static void beforeAll() {
        // DB初期化(テーブル作成)
        myDbUnitExtension.sql("drop table if exists test_table");
        myDbUnitExtension.sql("""
        create table test_table (
            id integer primary key,
            value varchar(1024)
        )""");
    }

    @Test
    void testNoStreaming() throws Exception {
        myDbUnitExtension.getDatabaseTester().setOperationListener(new DefaultOperationListener() {
            @Override
            public void connectionRetrieved(IDatabaseConnection connection) {
                super.connectionRetrieved(connection);
                connection.getConfig().setProperty("http://www.dbunit.org/features/batchedStatements", true);
            }
        });

        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        final URL url = this.getClass().getResource("/sandbox/dbunit/StreamingDataSetTest/setUp.xml");
        FlatXmlDataSet dataSet = builder.build(url);
        myDbUnitExtension.getDatabaseTester().setDataSet(dataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }

    @Test
    void testStreaming() throws Exception {
        myDbUnitExtension.getDatabaseTester().setSetUpOperation(DatabaseOperation.INSERT);

        myDbUnitExtension.getDatabaseTester().setOperationListener(new DefaultOperationListener() {
            @Override
            public void connectionRetrieved(IDatabaseConnection connection) {
                super.connectionRetrieved(connection);
                connection.getConfig().setProperty("http://www.dbunit.org/features/batchedStatements", true);
            }
        });

        URL url = this.getClass().getResource("/sandbox/dbunit/StreamingDataSetTest/setUp.xml");
        FlatXmlProducer producer = new FlatXmlProducer(new InputSource(url.toString()));
        StreamingDataSet dataSet = new StreamingDataSet(producer);
        myDbUnitExtension.getDatabaseTester().setDataSet(dataSet);

        myDbUnitExtension.getDatabaseTester().onSetup();
    }
//
//    private void printMemory(String label) {
//        Runtime runtime = Runtime.getRuntime();
//        long max = runtime.maxMemory();
//        long total = runtime.totalMemory();
//        long free = runtime.freeMemory();
//        System.out.printf("[%s] max=%d, total=%d, free=%d%n", label, max, total, free);
//    }
//    final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//
//    private void start() {
//        Runtime runtime = Runtime.getRuntime();
//        service.scheduleAtFixedRate(() -> {
//            long total = runtime.totalMemory();
//            long free = runtime.freeMemory();
//            long usage = total - free;
//            System.out.printf("%s,%s,%s%n", total, usage, free);
//            System.out.flush();
//        }, 0, 1, TimeUnit.SECONDS);
//    }
//
//    @AfterEach
//    void afterEach() {
//        service.shutdown();
//    }
}
