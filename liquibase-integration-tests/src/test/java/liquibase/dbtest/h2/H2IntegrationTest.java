package liquibase.dbtest.h2;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.dbtest.AbstractIntegrationTest;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.diff.output.report.DiffToReport;
import liquibase.exception.ValidationFailedException;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import org.junit.Ignore;
import org.junit.Test;

public class H2IntegrationTest extends AbstractIntegrationTest {

    private final String changeSpecifyDbmsChangeLog;
    private final String dbmsExcludeChangelog;

    public H2IntegrationTest() throws Exception {
        super("h2", DatabaseFactory.getInstance().getDatabase("h2"));
        this.changeSpecifyDbmsChangeLog = "changelogs/h2/complete/change.specify.dbms.changelog.xml";
        this.dbmsExcludeChangelog = "changelogs/h2/complete/dbms.exclude.changelog.xml";
    }

    @Override
    protected boolean isDatabaseProvidedByTravisCI() {
        // H2 is an in-process database
        return true;
    }

    @Test
    public void diffToPrintStream() throws Exception{
        if (getDatabase() == null) {
            return;
        }

        runCompleteChangeLog();

        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(getDatabase(), null, new CompareControl());
        new DiffToReport(diffResult, System.out).print();
    }

    // TODO: This test currently makes the whole VM exit with exit code -1, but does not generate a dump file
    // (not a "genuine" VM crash). I need to disable this test until I can find out how to catch/debug this.
    @Test
    @Ignore
    public void diffToChangeLog() throws Exception{
        if (getDatabase() == null) {
            return;
        }

        runCompleteChangeLog();

        DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(getDatabase(), null, new CompareControl());
        new DiffToChangeLog(diffResult, new DiffOutputControl(true, true, true, null)).print(System.out);
    }

    @Test
    public void snapshot() throws Exception {
        if (getDatabase() == null) {
            return;
        }


        runCompleteChangeLog();
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(getDatabase().getDefaultSchema(), getDatabase(), new SnapshotControl(getDatabase()));
        System.out.println(snapshot);
    }

    @Test
    public void canSpecifyDbmsForIndividualChanges() throws Exception {
        runChangeLogFile(changeSpecifyDbmsChangeLog);
    }

    @Test
    public void h2IsExcludedFromRunningChangeset() throws Exception {
        runChangeLogFile(dbmsExcludeChangelog);
    }

    @Test
    public void runYamlChangelog() throws Exception {
        if (getDatabase() == null) {
            return;
        }

        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase(liquibase);

        //run again to test changelog testing logic
        liquibase = createLiquibase("changelogs/yaml/common.tests.changelog.yaml");
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }


    }

    @Test
    public void runJsonChangelog() throws Exception {
        if (getDatabase() == null) {
            return;
        }

        Liquibase liquibase = createLiquibase(completeChangeLog);
        clearDatabase(liquibase);

        //run again to test changelog testing logic
        liquibase = createLiquibase("changelogs/json/common.tests.changelog.json");
        try {
            liquibase.update(this.contexts);
        } catch (ValidationFailedException e) {
            e.printDescriptiveError(System.out);
            throw e;
        }
    }

    @Test
    @Override
    public void generateChangeLog_noChanges() throws Exception {
        super.generateChangeLog_noChanges();    //To change body of overridden methods use File | Settings | File Templates.
    }

    //    @Test
//    public void testUpdateWithTurkishLocale() throws Exception {
//        Locale originalDefault = Locale.getDefault();
//
//        Locale.setDefault(new Locale("tr","TR"));
//        testRunChangeLog();
//        Locale.setDefault(originalDefault);
//    }


}
