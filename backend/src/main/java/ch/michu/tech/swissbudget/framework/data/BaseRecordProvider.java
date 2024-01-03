package ch.michu.tech.swissbudget.framework.data;

import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record;

public interface BaseRecordProvider<R, I> {

    R newRecord(DSLContext db);

    R fromRecord(DSLContext db, Record result);

    boolean fetchExists(DSLContext db, UUID userId, I recordId);
}
