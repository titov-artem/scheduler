package com.github.scheduler.repo.jdbc.jooq;

import com.google.common.collect.Lists;
import org.jooq.*;

import java.util.Collections;
import java.util.List;

public class JooqPrimaryKey implements UniqueKey<Record> {

    private final Table<Record> table;
    private final List<TableField<Record, ?>> fields;

    public JooqPrimaryKey(Table<Record> table, String... fields) {
        this.table = table;
        this.fields = Lists.newArrayListWithCapacity(fields.length);
        for (String field : fields) {
            this.fields.add(new JooqField(field, Object.class, table));
        }
    }

    public JooqPrimaryKey(Table<Record> table, Field<?>... fields) {
        this.table = table;
        this.fields = Lists.newArrayListWithCapacity(fields.length);
        for (Field<?> field : fields) {
            this.fields.add(new JooqField(field, table));
        }
    }

    @Override
    public List<ForeignKey<?, Record>> getReferences() {
        return Collections.emptyList();
    }

    @Override
    public boolean isPrimary() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Table<Record> getTable() {
        return table;
    }

    @Override
    public List<TableField<Record, ?>> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public TableField<Record, ?>[] getFieldsArray() {
        return fields.toArray(new TableField[fields.size()]);
    }

    @Override
    public Constraint constraint() {
        return null;
    }
}
