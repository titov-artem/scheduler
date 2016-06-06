package com.github.sc.scheduler.jdbc.repo.jooq;

import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class JooqTable implements Table<Record>, QueryPartInternal {

    private final Table<Record> table;
    private final UniqueKey<Record> primaryKey;

    private JooqTable(String name, @Nullable Field<?> primaryKeyField) {
        this.table = DSL.table(name);
        if (primaryKeyField != null) {
            this.primaryKey = new JooqPrimaryKey(this.table, primaryKeyField);
        } else {
            this.primaryKey = null;
        }
    }

    public static Table<Record> table(String name) {
        return new JooqTable(name, null);
    }

    public static Table<Record> table(String name, Field<?> primaryKey) {
        return new JooqTable(name, primaryKey);
    }

    @Override
    public Schema getSchema() {
        return this.table.getSchema();
    }

    @Override
    public String getName() {
        return this.table.getName();
    }

    @Override
    public String getComment() {
        return this.table.getComment();
    }

    @Override
    public RecordType<Record> recordType() {
        return this.table.recordType();
    }

    @Override
    public Class getRecordType() {
        return this.table.getRecordType();
    }

    @Override
    public DataType<Record> getDataType() {
        return this.table.getDataType();
    }

    @Override
    public Record newRecord() {
        return this.table.newRecord();
    }

    @Override
    public Identity<Record, ?> getIdentity() {
        return this.table.getIdentity();
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        if (primaryKey != null) return primaryKey;
        return this.table.getPrimaryKey();
    }

    @Override
    public TableField<Record, ? extends Number> getRecordVersion() {
        return this.table.getRecordVersion();
    }

    @Override
    public TableField<Record, ? extends Date> getRecordTimestamp() {
        return this.table.getRecordTimestamp();
    }

    @Override
    public List<UniqueKey<Record>> getKeys() {
        return this.table.getKeys();
    }

    @Override
    public <O extends Record> List<ForeignKey<O, Record>> getReferencesFrom(Table<O> other) {
        return this.table.getReferencesFrom(other);
    }

    @Override
    public List<ForeignKey<Record, ?>> getReferences() {
        return this.table.getReferences();
    }

    @Override
    public <O extends Record> List<ForeignKey<Record, O>> getReferencesTo(Table<O> other) {
        return this.table.getReferencesTo(other);
    }

    @Override
    public Table<Record> as(String alias) {
        return this.table.as(alias);
    }

    @Override
    public Table<Record> as(String alias, String... fieldAliases) {
        return this.table.as(alias, fieldAliases);
    }

    @Override
    public TableOptionalOnStep<Record> join(TableLike<?> table, JoinType type) {
        return this.table.join(table, type);
    }

    @Override
    public TableOnStep<Record> join(TableLike<?> table) {
        return this.table.join(table);
    }

    @Override
    public TableOnStep<Record> join(SQL sql) {
        return this.table.join(sql);
    }

    @Override
    public TableOnStep<Record> join(String sql) {
        return this.table.join(sql);
    }

    @Override
    public TableOnStep<Record> join(String sql, Object... bindings) {
        return this.table.join(sql, bindings);
    }

    @Override
    public TableOnStep<Record> join(String sql, QueryPart... parts) {
        return this.table.join(sql, parts);
    }

    @Override
    public TableOnStep<Record> join(Name name) {
        return this.table.join(name);
    }

    @Override
    public TableOnStep<Record> innerJoin(TableLike<?> table) {
        return this.table.innerJoin(table);
    }

    @Override
    public TableOnStep<Record> innerJoin(SQL sql) {
        return this.table.innerJoin(sql);
    }

    @Override
    public TableOnStep<Record> innerJoin(String sql) {
        return this.table.innerJoin(sql);
    }

    @Override
    public TableOnStep<Record> innerJoin(String sql, Object... bindings) {
        return this.table.innerJoin(sql, bindings);
    }

    @Override
    public TableOnStep<Record> innerJoin(String sql, QueryPart... parts) {
        return this.table.innerJoin(sql, parts);
    }

    @Override
    public TableOnStep<Record> innerJoin(Name name) {
        return this.table.innerJoin(name);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(TableLike<?> table) {
        return this.table.leftJoin(table);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(SQL sql) {
        return this.table.leftJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(String sql) {
        return this.table.leftJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(String sql, Object... bindings) {
        return this.table.leftJoin(sql, bindings);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(String sql, QueryPart... parts) {
        return this.table.leftJoin(sql, parts);
    }

    @Override
    public TablePartitionByStep<Record> leftJoin(Name name) {
        return this.table.leftJoin(name);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(TableLike<?> table) {
        return this.table.leftOuterJoin(table);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(SQL sql) {
        return this.table.leftOuterJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(String sql) {
        return this.table.leftOuterJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(String sql, Object... bindings) {
        return this.table.leftOuterJoin(sql, bindings);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(String sql, QueryPart... parts) {
        return this.table.leftOuterJoin(sql, parts);
    }

    @Override
    public TablePartitionByStep<Record> leftOuterJoin(Name name) {
        return this.table.leftOuterJoin(name);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(TableLike<?> table) {
        return this.table.rightJoin(table);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(SQL sql) {
        return this.table.rightJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(String sql) {
        return this.table.rightJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(String sql, Object... bindings) {
        return this.table.rightJoin(sql, bindings);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(String sql, QueryPart... parts) {
        return this.table.rightJoin(sql, parts);
    }

    @Override
    public TablePartitionByStep<Record> rightJoin(Name name) {
        return this.table.rightJoin(name);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(TableLike<?> table) {
        return this.table.rightOuterJoin(table);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(SQL sql) {
        return this.table.rightOuterJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(String sql) {
        return this.table.rightOuterJoin(sql);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(String sql, Object... bindings) {
        return this.table.rightOuterJoin(sql, bindings);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(String sql, QueryPart... parts) {
        return this.table.rightOuterJoin(sql, parts);
    }

    @Override
    public TablePartitionByStep<Record> rightOuterJoin(Name name) {
        return this.table.rightOuterJoin(name);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(TableLike<?> table) {
        return this.table.fullOuterJoin(table);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(SQL sql) {
        return this.table.fullOuterJoin(sql);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(String sql) {
        return this.table.fullOuterJoin(sql);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(String sql, Object... bindings) {
        return this.table.fullOuterJoin(sql, bindings);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(String sql, QueryPart... parts) {
        return this.table.fullOuterJoin(sql, parts);
    }

    @Override
    public TableOnStep<Record> fullOuterJoin(Name name) {
        return this.table.fullOuterJoin(name);
    }

    @Override
    public Table<Record> crossJoin(TableLike<?> table) {
        return this.table.crossJoin(table);
    }

    @Override
    public Table<Record> crossJoin(SQL sql) {
        return this.table.crossJoin(sql);
    }

    @Override
    public Table<Record> crossJoin(String sql) {
        return this.table.crossJoin(sql);
    }

    @Override
    public Table<Record> crossJoin(String sql, Object... bindings) {
        return this.table.crossJoin(sql, bindings);
    }

    @Override
    public Table<Record> crossJoin(String sql, QueryPart... parts) {
        return this.table.crossJoin(sql, parts);
    }

    @Override
    public Table<Record> crossJoin(Name name) {
        return this.table.crossJoin(name);
    }

    @Override
    public Table<Record> naturalJoin(TableLike<?> table) {
        return this.table.naturalJoin(table);
    }

    @Override
    public Table<Record> naturalJoin(SQL sql) {
        return this.table.naturalJoin(sql);
    }

    @Override
    public Table<Record> naturalJoin(String sql) {
        return this.table.naturalJoin(sql);
    }

    @Override
    public Table<Record> naturalJoin(String sql, Object... bindings) {
        return this.table.naturalJoin(sql, bindings);
    }

    @Override
    public Table<Record> naturalJoin(Name name) {
        return this.table.naturalJoin(name);
    }

    @Override
    public Table<Record> naturalJoin(String sql, QueryPart... parts) {
        return this.table.naturalJoin(sql, parts);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(TableLike<?> table) {
        return this.table.naturalLeftOuterJoin(table);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(SQL sql) {
        return this.table.naturalLeftOuterJoin(sql);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(String sql) {
        return this.table.naturalLeftOuterJoin(sql);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(String sql, Object... bindings) {
        return this.table.naturalLeftOuterJoin(sql, bindings);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(String sql, QueryPart... parts) {
        return this.table.naturalLeftOuterJoin(sql, parts);
    }

    @Override
    public Table<Record> naturalLeftOuterJoin(Name name) {
        return this.table.naturalLeftOuterJoin(name);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(TableLike<?> table) {
        return this.table.naturalRightOuterJoin(table);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(SQL sql) {
        return this.table.naturalRightOuterJoin(sql);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(String sql) {
        return this.table.naturalRightOuterJoin(sql);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(String sql, Object... bindings) {
        return this.table.naturalRightOuterJoin(sql, bindings);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(String sql, QueryPart... parts) {
        return this.table.naturalRightOuterJoin(sql, parts);
    }

    @Override
    public Table<Record> naturalRightOuterJoin(Name name) {
        return this.table.naturalRightOuterJoin(name);
    }

    @Override
    public Table<Record> crossApply(TableLike<?> table) {
        return this.table.crossApply(table);
    }

    @Override
    public Table<Record> crossApply(SQL sql) {
        return this.table.crossApply(sql);
    }

    @Override
    public Table<Record> crossApply(String sql) {
        return this.table.crossApply(sql);
    }

    @Override
    public Table<Record> crossApply(String sql, Object... bindings) {
        return this.table.crossApply(sql, bindings);
    }

    @Override
    public Table<Record> crossApply(String sql, QueryPart... parts) {
        return this.table.crossApply(sql, parts);
    }

    @Override
    public Table<Record> crossApply(Name name) {
        return this.table.crossApply(name);
    }

    @Override
    public Table<Record> outerApply(TableLike<?> table) {
        return this.table.outerApply(table);
    }

    @Override
    public Table<Record> outerApply(SQL sql) {
        return this.table.outerApply(sql);
    }

    @Override
    public Table<Record> outerApply(String sql) {
        return this.table.outerApply(sql);
    }

    @Override
    public Table<Record> outerApply(String sql, Object... bindings) {
        return this.table.outerApply(sql, bindings);
    }

    @Override
    public Table<Record> outerApply(String sql, QueryPart... parts) {
        return this.table.outerApply(sql, parts);
    }

    @Override
    public Table<Record> outerApply(Name name) {
        return this.table.outerApply(name);
    }

    @Override
    public TableOnStep<Record> straightJoin(TableLike<?> table) {
        return this.table.straightJoin(table);
    }

    @Override
    public TableOnStep<Record> straightJoin(SQL sql) {
        return this.table.straightJoin(sql);
    }

    @Override
    public TableOnStep<Record> straightJoin(String sql) {
        return this.table.straightJoin(sql);
    }

    @Override
    public TableOnStep<Record> straightJoin(String sql, Object... bindings) {
        return this.table.straightJoin(sql, bindings);
    }

    @Override
    public TableOnStep<Record> straightJoin(String sql, QueryPart... parts) {
        return this.table.straightJoin(sql, parts);
    }

    @Override
    public TableOnStep<Record> straightJoin(Name name) {
        return this.table.straightJoin(name);
    }

    @Override
    public Condition eq(Table<Record> table) {
        return this.table.eq(table);
    }

    @Override
    public Condition equal(Table<Record> table) {
        return this.table.equal(table);
    }

    @Override
    public Condition ne(Table<Record> table) {
        return this.table.ne(table);
    }

    @Override
    public Condition notEqual(Table<Record> table) {
        return this.table.notEqual(table);
    }

    @Override
    public Table<Record> useIndex(String... indexes) {
        return this.table.useIndex(indexes);
    }

    @Override
    public Table<Record> useIndexForJoin(String... indexes) {
        return this.table.useIndexForJoin(indexes);
    }

    @Override
    public Table<Record> useIndexForOrderBy(String... indexes) {
        return this.table.useIndexForOrderBy(indexes);
    }

    @Override
    public Table<Record> useIndexForGroupBy(String... indexes) {
        return this.table.useIndexForGroupBy(indexes);
    }

    @Override
    public Table<Record> ignoreIndex(String... indexes) {
        return this.table.ignoreIndex(indexes);
    }

    @Override
    public Table<Record> ignoreIndexForJoin(String... indexes) {
        return this.table.ignoreIndexForJoin(indexes);
    }

    @Override
    public Table<Record> ignoreIndexForOrderBy(String... indexes) {
        return this.table.ignoreIndexForOrderBy(indexes);
    }

    @Override
    public Table<Record> ignoreIndexForGroupBy(String... indexes) {
        return this.table.ignoreIndexForGroupBy(indexes);
    }

    @Override
    public Table<Record> forceIndex(String... indexes) {
        return this.table.forceIndex(indexes);
    }

    @Override
    public Table<Record> forceIndexForJoin(String... indexes) {
        return this.table.forceIndexForJoin(indexes);
    }

    @Override
    public Table<Record> forceIndexForOrderBy(String... indexes) {
        return this.table.forceIndexForOrderBy(indexes);
    }

    @Override
    public Table<Record> forceIndexForGroupBy(String... indexes) {
        return this.table.forceIndexForGroupBy(indexes);
    }

    @Override
    public DivideByOnStep divideBy(Table<?> divisor) {
        return this.table.divideBy(divisor);
    }

    @Override
    public TableOnStep<Record> leftSemiJoin(TableLike<?> table) {
        return this.table.leftSemiJoin(table);
    }

    @Override
    public TableOnStep<Record> leftAntiJoin(TableLike<?> table) {
        return this.table.leftAntiJoin(table);
    }


    @Override
    public Row fieldsRow() {
        return this.table.fieldsRow();
    }

    @Override
    public <T> Field<T> field(Field<T> field) {
        return this.table.field(field);
    }

    @Override
    public Field<?> field(String name) {
        return this.table.field(name);
    }

    @Override
    public <T> Field<T> field(String name, Class<T> type) {
        return this.table.field(name, type);
    }

    @Override
    public <T> Field<T> field(String name, DataType<T> dataType) {
        return this.table.field(name, dataType);
    }

    @Override
    public Field<?> field(Name name) {
        return this.table.field(name);
    }

    @Override
    public <T> Field<T> field(Name name, Class<T> type) {
        return this.table.field(name, type);
    }

    @Override
    public <T> Field<T> field(Name name, DataType<T> dataType) {
        return this.table.field(name, dataType);
    }

    @Override
    public Field<?> field(int index) {
        return this.table.field(index);
    }

    @Override
    public <T> Field<T> field(int index, Class<T> type) {
        return this.table.field(index, type);
    }

    @Override
    public <T> Field<T> field(int index, DataType<T> dataType) {
        return this.table.field(index, dataType);
    }

    @Override
    public Field<?>[] fields() {
        return new Field<?>[0];
    }

    @Override
    public Field<?>[] fields(Field<?>... fields) {
        return new Field<?>[0];
    }

    @Override
    public Field<?>[] fields(String... fieldNames) {
        return new Field<?>[0];
    }

    @Override
    public Field<?>[] fields(Name... fieldNames) {
        return new Field<?>[0];
    }

    @Override
    public Field<?>[] fields(int... fieldIndexes) {
        return new Field<?>[0];
    }

    @Override
    public Table<Record> asTable() {
        return this.table.asTable();
    }

    @Override
    public Table<Record> asTable(String alias) {
        return this.table.asTable(alias);
    }

    @Override
    public Table<Record> asTable(String alias, String... fieldAliases) {
        return this.table.asTable(alias, fieldAliases);
    }

    @Override
    public void accept(Context<?> ctx) {
        ((QueryPartInternal) this.table).accept(ctx);
    }

    @Override
    public void toSQL(RenderContext ctx) {
        ((QueryPartInternal) this.table).toSQL(ctx);
    }

    @Override
    public void bind(BindContext ctx) throws DataAccessException {
        ((QueryPartInternal) this.table).bind(ctx);
    }

    @Override
    public Clause[] clauses(Context<?> ctx) {
        return ((QueryPartInternal) this.table).clauses(ctx);
    }

    @Override
    public boolean declaresFields() {
        return ((QueryPartInternal) this.table).declaresFields();
    }

    @Override
    public boolean declaresTables() {
        return ((QueryPartInternal) this.table).declaresTables();
    }

    @Override
    public boolean declaresWindows() {
        return ((QueryPartInternal) this.table).declaresWindows();
    }

    @Override
    public boolean declaresCTE() {
        return ((QueryPartInternal) this.table).declaresCTE();
    }

    @Override
    public boolean generatesCast() {
        return ((QueryPartInternal) this.table).generatesCast();
    }
}
