package ch.michu.tech.swissbudget.framework.data;

import java.util.UUID;
import org.jooq.Record;

public interface BaseRecordProvider<R, I> {

    R newRecord();

    R fromRecord(Record result);

    boolean fetchExists(UUID userId, I recordId);
}
