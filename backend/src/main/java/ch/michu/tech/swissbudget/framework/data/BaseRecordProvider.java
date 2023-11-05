package ch.michu.tech.swissbudget.framework.data;

import org.jooq.Record;

public interface BaseRecordProvider<R, I> {

    R newRecord();

    R fromRecord(Record result);

    boolean fetchExists(String userId, I recordId);
}
