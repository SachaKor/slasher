package ch.gaps.slasher.corrector;

import ch.gaps.slasher.database.driver.database.Database;
import ch.gaps.slasher.utils.CorrectorResult;

public interface Corrector {
    void setDatabase(Database database);
    CorrectorResult checkStatement(String stmt);
}
