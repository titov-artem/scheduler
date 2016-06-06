package com.github.sc.scheduler.jdbc.repo.jooq;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class JooqField<T> implements TableField<Record, T> {

    private final Field<T> field;
    private final Table<Record> table;

    public JooqField(String name, Class<T> clazz, Table<Record> table) {
        this.field = DSL.field(name, clazz);
        this.table = table;
    }

    public JooqField(Field<T> field, Table<Record> table) {
        this.field = field;
        this.table = table;
    }

    @Override
    public Table<Record> getTable() {
        return table;
    }

    @Override
    public String getName() {
        return this.field.getName();
    }

    @Override
    public String getComment() {
        return this.field.getComment();
    }

    @Override
    public Converter<?, T> getConverter() {
        return this.field.getConverter();
    }

    @Override
    public Binding<?, T> getBinding() {
        return this.field.getBinding();
    }

    @Override
    public Class<T> getType() {
        return this.field.getType();
    }

    @Override
    public DataType<T> getDataType() {
        return this.field.getDataType();
    }

    @Override
    public DataType<T> getDataType(Configuration configuration) {
        return this.field.getDataType(configuration);
    }

    @Override
    public Field<T> as(String alias) {
        return this.field.as(alias);
    }

    @Override
    public Field<T> as(Field<?> otherField) {
        return this.field.as(otherField);
    }

    @Override
    public <Z> Field<Z> cast(Field<Z> field) {
        return this.field.cast(field);
    }

    @Override
    public <Z> Field<Z> cast(DataType<Z> type) {
        return this.field.cast(type);
    }

    @Override
    public <Z> Field<Z> cast(Class<Z> type) {
        return this.field.cast(type);
    }

    @Override
    public <Z> Field<Z> coerce(Field<Z> field) {
        return this.field.coerce(field);
    }

    @Override
    public <Z> Field<Z> coerce(DataType<Z> type) {
        return this.field.coerce(type);
    }

    @Override
    public <Z> Field<Z> coerce(Class<Z> type) {
        return this.field.coerce(type);
    }

    @Override
    public SortField<T> asc() {
        return this.field.asc();
    }

    @Override
    public SortField<T> desc() {
        return this.field.desc();
    }

    @Override
    public SortField<T> sort(SortOrder order) {
        return this.field.sort(order);
    }

    @Override
    public SortField<Integer> sortAsc(Collection<T> sortList) {
        return this.field.sortAsc(sortList);
    }

    @Override
    public SortField<Integer> sortAsc(T... sortList) {
        return this.field.sortAsc(sortList);
    }

    @Override
    public SortField<Integer> sortDesc(Collection<T> sortList) {
        return this.field.sortDesc(sortList);
    }

    @Override
    public SortField<Integer> sortDesc(T... sortList) {
        return this.field.sortDesc(sortList);
    }

    @Override
    public <Z> SortField<Z> sort(Map<T, Z> sortMap) {
        return this.field.sort(sortMap);
    }

    @Override
    public Field<T> neg() {
        return this.field.neg();
    }

    @Override
    public Field<T> add(Number value) {
        return this.field.add(value);
    }

    @Override
    public Field<T> add(Field<?> value) {
        return this.field.add(value);
    }

    @Override
    public Field<T> plus(Number value) {
        return this.field.plus(value);
    }

    @Override
    public Field<T> plus(Field<?> value) {
        return this.field.plus(value);
    }

    @Override
    public Field<T> sub(Number value) {
        return this.field.sub(value);
    }

    @Override
    public Field<T> sub(Field<?> value) {
        return this.field.sub(value);
    }

    @Override
    public Field<T> subtract(Number value) {
        return this.field.subtract(value);
    }

    @Override
    public Field<T> subtract(Field<?> value) {
        return this.field.subtract(value);
    }

    @Override
    public Field<T> minus(Number value) {
        return this.field.minus(value);
    }

    @Override
    public Field<T> minus(Field<?> value) {
        return this.field.minus(value);
    }

    @Override
    public Field<T> mul(Number value) {
        return this.field.mul(value);
    }

    @Override
    public Field<T> mul(Field<? extends Number> value) {
        return this.field.mul(value);
    }

    @Override
    public Field<T> multiply(Number value) {
        return this.field.multiply(value);
    }

    @Override
    public Field<T> multiply(Field<? extends Number> value) {
        return this.field.multiply(value);
    }

    @Override
    public Field<T> div(Number value) {
        return this.field.div(value);
    }

    @Override
    public Field<T> div(Field<? extends Number> value) {
        return this.field.div(value);
    }

    @Override
    public Field<T> divide(Number value) {
        return this.field.divide(value);
    }

    @Override
    public Field<T> divide(Field<? extends Number> value) {
        return this.field.divide(value);
    }

    @Override
    public Field<T> mod(Number value) {
        return this.field.mod(value);
    }

    @Override
    public Field<T> mod(Field<? extends Number> value) {
        return this.field.mod(value);
    }

    @Override
    public Field<T> modulo(Number value) {
        return this.field.modulo(value);
    }

    @Override
    public Field<T> modulo(Field<? extends Number> value) {
        return this.field.modulo(value);
    }

    @Override
    public Field<T> bitNot() {
        return this.field.bitNot();
    }

    @Override
    public Field<T> bitAnd(T value) {
        return this.field.bitAnd(value);
    }

    @Override
    public Field<T> bitAnd(Field<T> value) {
        return this.field.bitAnd(value);
    }

    @Override
    public Field<T> bitNand(T value) {
        return this.field.bitNand(value);
    }

    @Override
    public Field<T> bitNand(Field<T> value) {
        return this.field.bitNand(value);
    }

    @Override
    public Field<T> bitOr(T value) {
        return this.field.bitOr(value);
    }

    @Override
    public Field<T> bitOr(Field<T> value) {
        return this.field.bitOr(value);
    }

    @Override
    public Field<T> bitNor(T value) {
        return this.field.bitNor(value);
    }

    @Override
    public Field<T> bitNor(Field<T> value) {
        return this.field.bitNor(value);
    }

    @Override
    public Field<T> bitXor(T value) {
        return this.field.bitXor(value);
    }

    @Override
    public Field<T> bitXor(Field<T> value) {
        return this.field.bitXor(value);
    }

    @Override
    public Field<T> bitXNor(T value) {
        return this.field.bitXNor(value);
    }

    @Override
    public Field<T> bitXNor(Field<T> value) {
        return this.field.bitXNor(value);
    }

    @Override
    public Field<T> shl(Number value) {
        return this.field.shl(value);
    }

    @Override
    public Field<T> shl(Field<? extends Number> value) {
        return this.field.shl(value);
    }

    @Override
    public Field<T> shr(Number value) {
        return this.field.shr(value);
    }

    @Override
    public Field<T> shr(Field<? extends Number> value) {
        return this.field.shr(value);
    }

    @Override
    public Condition isNull() {
        return this.field.isNull();
    }

    @Override
    public Condition isNotNull() {
        return this.field.isNotNull();
    }

    @Override
    public Condition isDistinctFrom(T value) {
        return this.field.isDistinctFrom(value);
    }

    @Override
    public Condition isDistinctFrom(Field<T> field) {
        return this.field.isDistinctFrom(field);
    }

    @Override
    public Condition isNotDistinctFrom(T value) {
        return this.field.isNotDistinctFrom(value);
    }

    @Override
    public Condition isNotDistinctFrom(Field<T> field) {
        return this.field.isNotDistinctFrom(field);
    }

    @Override
    public Condition likeRegex(String pattern) {
        return this.field.likeRegex(pattern);
    }

    @Override
    public Condition likeRegex(Field<String> pattern) {
        return this.field.likeRegex(pattern);
    }

    @Override
    public Condition notLikeRegex(String pattern) {
        return this.field.notLikeRegex(pattern);
    }

    @Override
    public Condition notLikeRegex(Field<String> pattern) {
        return this.field.notLikeRegex(pattern);
    }

    @Override
    public Condition like(Field<String> value) {
        return this.field.like(value);
    }

    @Override
    public Condition like(Field<String> value, char escape) {
        return this.field.like(value, escape);
    }

    @Override
    public Condition like(String value) {
        return this.field.like(value);
    }

    @Override
    public Condition like(String value, char escape) {
        return this.field.like(value, escape);
    }

    @Override
    public Condition likeIgnoreCase(Field<String> field) {
        return this.field.likeIgnoreCase(field);
    }

    @Override
    public Condition likeIgnoreCase(Field<String> field, char escape) {
        return this.field.likeIgnoreCase(field, escape);
    }

    @Override
    public Condition likeIgnoreCase(String value) {
        return this.field.likeIgnoreCase(value);
    }

    @Override
    public Condition likeIgnoreCase(String value, char escape) {
        return this.field.likeIgnoreCase(value, escape);
    }

    @Override
    public Condition notLike(Field<String> field) {
        return this.field.notLike(field);
    }

    @Override
    public Condition notLike(Field<String> field, char escape) {
        return this.field.notLike(field, escape);
    }

    @Override
    public Condition notLike(String value) {
        return this.field.notLike(value);
    }

    @Override
    public Condition notLike(String value, char escape) {
        return this.field.notLike(value, escape);
    }

    @Override
    public Condition notLikeIgnoreCase(Field<String> field) {
        return this.field.notLikeIgnoreCase(field);
    }

    @Override
    public Condition notLikeIgnoreCase(Field<String> field, char escape) {
        return this.field.notLikeIgnoreCase(field, escape);
    }

    @Override
    public Condition notLikeIgnoreCase(String value) {
        return this.field.notLikeIgnoreCase(value);
    }

    @Override
    public Condition notLikeIgnoreCase(String value, char escape) {
        return this.field.notLikeIgnoreCase(value, escape);
    }

    @Override
    public Condition contains(T value) {
        return this.field.contains(value);
    }

    @Override
    public Condition contains(Field<T> value) {
        return this.field.contains(value);
    }

    @Override
    public Condition startsWith(T value) {
        return this.field.startsWith(value);
    }

    @Override
    public Condition startsWith(Field<T> value) {
        return this.field.startsWith(value);
    }

    @Override
    public Condition endsWith(T value) {
        return this.field.endsWith(value);
    }

    @Override
    public Condition endsWith(Field<T> value) {
        return this.field.endsWith(value);
    }

    @Override
    public Condition in(Collection<?> values) {
        return this.field.in(values);
    }

    @Override
    public Condition in(Result<? extends Record1<T>> result) {
        return this.field.in(result);
    }

    @Override
    public Condition in(T... values) {
        return this.field.in(values);
    }

    @Override
    public Condition in(Field<?>... values) {
        return this.field.in(values);
    }

    @Override
    public Condition in(Select<? extends Record1<T>> query) {
        return this.field.in(query);
    }

    @Override
    public Condition notIn(Collection<?> values) {
        return this.field.notIn(values);
    }

    @Override
    public Condition notIn(Result<? extends Record1<T>> result) {
        return this.field.notIn(result);
    }

    @Override
    public Condition notIn(T... values) {
        return this.field.notIn(values);
    }

    @Override
    public Condition notIn(Field<?>... values) {
        return this.field.notIn(values);
    }

    @Override
    public Condition notIn(Select<? extends Record1<T>> query) {
        return this.field.notIn(query);
    }

    @Override
    public Condition between(T minValue, T maxValue) {
        return this.field.between(minValue, maxValue);
    }

    @Override
    public Condition between(Field<T> minValue, Field<T> maxValue) {
        return this.field.between(minValue, maxValue);
    }

    @Override
    public Condition betweenSymmetric(T minValue, T maxValue) {
        return this.field.betweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition betweenSymmetric(Field<T> minValue, Field<T> maxValue) {
        return this.field.betweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition notBetween(T minValue, T maxValue) {
        return this.field.notBetween(minValue, maxValue);
    }

    @Override
    public Condition notBetween(Field<T> minValue, Field<T> maxValue) {
        return this.field.notBetween(minValue, maxValue);
    }

    @Override
    public Condition notBetweenSymmetric(T minValue, T maxValue) {
        return this.field.notBetweenSymmetric(minValue, maxValue);
    }

    @Override
    public Condition notBetweenSymmetric(Field<T> minValue, Field<T> maxValue) {
        return this.field.notBetweenSymmetric(minValue, maxValue);
    }

    @Override
    public BetweenAndStep<T> between(T minValue) {
        return this.field.between(minValue);
    }

    @Override
    public BetweenAndStep<T> between(Field<T> minValue) {
        return this.field.between(minValue);
    }

    @Override
    public BetweenAndStep<T> betweenSymmetric(T minValue) {
        return this.field.betweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<T> betweenSymmetric(Field<T> minValue) {
        return this.field.betweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<T> notBetween(T minValue) {
        return this.field.notBetween(minValue);
    }

    @Override
    public BetweenAndStep<T> notBetween(Field<T> minValue) {
        return this.field.notBetween(minValue);
    }

    @Override
    public BetweenAndStep<T> notBetweenSymmetric(T minValue) {
        return this.field.notBetweenSymmetric(minValue);
    }

    @Override
    public BetweenAndStep<T> notBetweenSymmetric(Field<T> minValue) {
        return this.field.notBetweenSymmetric(minValue);
    }

    @Override
    public Condition compare(Comparator comparator, T value) {
        return this.field.compare(comparator, value);
    }

    @Override
    public Condition compare(Comparator comparator, Field<T> field) {
        return this.field.compare(comparator, field);
    }

    @Override
    public Condition compare(Comparator comparator, Select<? extends Record1<T>> query) {
        return this.field.compare(comparator, query);
    }

    @Override
    public Condition compare(Comparator comparator, QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.compare(comparator, query);
    }

    @Override
    public Condition equal(T value) {
        return this.field.equal(value);
    }

    @Override
    public Condition equal(Field<T> field) {
        return this.field.equal(field);
    }

    @Override
    public Condition equal(Select<? extends Record1<T>> query) {
        return this.field.equal(query);
    }

    @Override
    public Condition equal(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.equal(query);
    }

    @Override
    public Condition eq(T value) {
        return this.field.eq(value);
    }

    @Override
    public Condition eq(Field<T> field) {
        return this.field.eq(field);
    }

    @Override
    public Condition eq(Select<? extends Record1<T>> query) {
        return this.field.eq(query);
    }

    @Override
    public Condition eq(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.eq(query);
    }

    @Override
    public Condition notEqual(T value) {
        return this.field.notEqual(value);
    }

    @Override
    public Condition notEqual(Field<T> field) {
        return this.field.notEqual(field);
    }

    @Override
    public Condition notEqual(Select<? extends Record1<T>> query) {
        return this.field.notEqual(query);
    }

    @Override
    public Condition notEqual(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.notEqual(query);
    }

    @Override
    public Condition ne(T value) {
        return this.field.ne(value);
    }

    @Override
    public Condition ne(Field<T> field) {
        return this.field.ne(field);
    }

    @Override
    public Condition ne(Select<? extends Record1<T>> query) {
        return this.field.ne(query);
    }

    @Override
    public Condition ne(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.ne(query);
    }

    @Override
    public Condition lessThan(T value) {
        return this.field.lessThan(value);
    }

    @Override
    public Condition lessThan(Field<T> field) {
        return this.field.lessThan(field);
    }

    @Override
    public Condition lessThan(Select<? extends Record1<T>> query) {
        return this.field.lessThan(query);
    }

    @Override
    public Condition lessThan(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.lessThan(query);
    }

    @Override
    public Condition lt(T value) {
        return this.field.lt(value);
    }

    @Override
    public Condition lt(Field<T> field) {
        return this.field.lt(field);
    }

    @Override
    public Condition lt(Select<? extends Record1<T>> query) {
        return this.field.lt(query);
    }

    @Override
    public Condition lt(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.lt(query);
    }

    @Override
    public Condition lessOrEqual(T value) {
        return this.field.lessOrEqual(value);
    }

    @Override
    public Condition lessOrEqual(Field<T> field) {
        return this.field.lessOrEqual(field);
    }

    @Override
    public Condition lessOrEqual(Select<? extends Record1<T>> query) {
        return this.field.lessOrEqual(query);
    }

    @Override
    public Condition lessOrEqual(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.lessOrEqual(query);
    }

    @Override
    public Condition le(T value) {
        return this.field.le(value);
    }

    @Override
    public Condition le(Field<T> field) {
        return this.field.le(field);
    }

    @Override
    public Condition le(Select<? extends Record1<T>> query) {
        return this.field.le(query);
    }

    @Override
    public Condition le(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.le(query);
    }

    @Override
    public Condition greaterThan(T value) {
        return this.field.greaterThan(value);
    }

    @Override
    public Condition greaterThan(Field<T> field) {
        return this.field.greaterThan(field);
    }

    @Override
    public Condition greaterThan(Select<? extends Record1<T>> query) {
        return this.field.greaterThan(query);
    }

    @Override
    public Condition greaterThan(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.greaterThan(query);
    }

    @Override
    public Condition gt(T value) {
        return this.field.gt(value);
    }

    @Override
    public Condition gt(Field<T> field) {
        return this.field.gt(field);
    }

    @Override
    public Condition gt(Select<? extends Record1<T>> query) {
        return this.field.gt(query);
    }

    @Override
    public Condition gt(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.gt(query);
    }

    @Override
    public Condition greaterOrEqual(T value) {
        return this.field.greaterOrEqual(value);
    }

    @Override
    public Condition greaterOrEqual(Field<T> field) {
        return this.field.greaterOrEqual(field);
    }

    @Override
    public Condition greaterOrEqual(Select<? extends Record1<T>> query) {
        return this.field.greaterOrEqual(query);
    }

    @Override
    public Condition greaterOrEqual(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.greaterOrEqual(query);
    }

    @Override
    public Condition ge(T value) {
        return this.field.ge(value);
    }

    @Override
    public Condition ge(Field<T> field) {
        return this.field.ge(field);
    }

    @Override
    public Condition ge(Select<? extends Record1<T>> query) {
        return this.field.ge(query);
    }

    @Override
    public Condition ge(QuantifiedSelect<? extends Record1<T>> query) {
        return this.field.ge(query);
    }

    @Override
    public Condition isTrue() {
        return this.field.isTrue();
    }

    @Override
    public Condition isFalse() {
        return this.field.isFalse();
    }

    @Override
    public Condition equalIgnoreCase(String value) {
        return this.field.equalIgnoreCase(value);
    }

    @Override
    public Condition equalIgnoreCase(Field<String> value) {
        return this.field.equalIgnoreCase(value);
    }

    @Override
    public Condition notEqualIgnoreCase(String value) {
        return this.field.notEqualIgnoreCase(value);
    }

    @Override
    public Condition notEqualIgnoreCase(Field<String> value) {
        return this.field.notEqualIgnoreCase(value);
    }

    @Override
    public Field<Integer> sign() {
        return this.field.sign();
    }

    @Override
    public Field<T> abs() {
        return this.field.abs();
    }

    @Override
    public Field<T> round() {
        return this.field.round();
    }

    @Override
    public Field<T> round(int decimals) {
        return this.field.round(decimals);
    }

    @Override
    public Field<T> floor() {
        return this.field.floor();
    }

    @Override
    public Field<T> ceil() {
        return this.field.ceil();
    }

    @Override
    public Field<BigDecimal> sqrt() {
        return this.field.sqrt();
    }

    @Override
    public Field<BigDecimal> exp() {
        return this.field.exp();
    }

    @Override
    public Field<BigDecimal> ln() {
        return this.field.ln();
    }

    @Override
    public Field<BigDecimal> log(int base) {
        return this.field.log(base);
    }

    @Override
    public Field<BigDecimal> pow(Number exponent) {
        return this.field.pow(exponent);
    }

    @Override
    public Field<BigDecimal> power(Number exponent) {
        return this.field.power(exponent);
    }

    @Override
    public Field<BigDecimal> acos() {
        return this.field.acos();
    }

    @Override
    public Field<BigDecimal> asin() {
        return this.field.asin();
    }

    @Override
    public Field<BigDecimal> atan() {
        return this.field.atan();
    }

    @Override
    public Field<BigDecimal> atan2(Number y) {
        return this.field.atan2(y);
    }

    @Override
    public Field<BigDecimal> atan2(Field<? extends Number> y) {
        return this.field.atan2(y);
    }

    @Override
    public Field<BigDecimal> cos() {
        return this.field.cos();
    }

    @Override
    public Field<BigDecimal> sin() {
        return this.field.sin();
    }

    @Override
    public Field<BigDecimal> tan() {
        return this.field.tan();
    }

    @Override
    public Field<BigDecimal> cot() {
        return this.field.cot();
    }

    @Override
    public Field<BigDecimal> sinh() {
        return this.field.sinh();
    }

    @Override
    public Field<BigDecimal> cosh() {
        return this.field.cosh();
    }

    @Override
    public Field<BigDecimal> tanh() {
        return this.field.tanh();
    }

    @Override
    public Field<BigDecimal> coth() {
        return this.field.coth();
    }

    @Override
    public Field<BigDecimal> deg() {
        return this.field.deg();
    }

    @Override
    public Field<BigDecimal> rad() {
        return this.field.rad();
    }

    @Override
    public Field<Integer> count() {
        return this.field.count();
    }

    @Override
    public Field<Integer> countDistinct() {
        return this.field.countDistinct();
    }

    @Override
    public Field<T> max() {
        return this.field.max();
    }

    @Override
    public Field<T> min() {
        return this.field.min();
    }

    @Override
    public Field<BigDecimal> sum() {
        return this.field.sum();
    }

    @Override
    public Field<BigDecimal> avg() {
        return this.field.avg();
    }

    @Override
    public Field<BigDecimal> median() {
        return this.field.median();
    }

    @Override
    public Field<BigDecimal> stddevPop() {
        return this.field.stddevPop();
    }

    @Override
    public Field<BigDecimal> stddevSamp() {
        return this.field.stddevSamp();
    }

    @Override
    public Field<BigDecimal> varPop() {
        return this.field.varPop();
    }

    @Override
    public Field<BigDecimal> varSamp() {
        return this.field.varSamp();
    }

    @Override
    public WindowPartitionByStep<Integer> countOver() {
        return this.field.countOver();
    }

    @Override
    public WindowPartitionByStep<T> maxOver() {
        return this.field.maxOver();
    }

    @Override
    public WindowPartitionByStep<T> minOver() {
        return this.field.minOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> sumOver() {
        return this.field.sumOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> avgOver() {
        return this.field.avgOver();
    }

    @Override
    public WindowIgnoreNullsStep<T> firstValue() {
        return this.field.firstValue();
    }

    @Override
    public WindowIgnoreNullsStep<T> lastValue() {
        return this.field.lastValue();
    }

    @Override
    public WindowIgnoreNullsStep<T> lead() {
        return this.field.lead();
    }

    @Override
    public WindowIgnoreNullsStep<T> lead(int offset) {
        return this.field.lead(offset);
    }

    @Override
    public WindowIgnoreNullsStep<T> lead(int offset, T defaultValue) {
        return this.field.lead(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<T> lead(int offset, Field<T> defaultValue) {
        return this.field.lead(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<T> lag() {
        return this.field.lag();
    }

    @Override
    public WindowIgnoreNullsStep<T> lag(int offset) {
        return this.field.lag(offset);
    }

    @Override
    public WindowIgnoreNullsStep<T> lag(int offset, T defaultValue) {
        return this.field.lag(offset, defaultValue);
    }

    @Override
    public WindowIgnoreNullsStep<T> lag(int offset, Field<T> defaultValue) {
        return this.field.lag(offset, defaultValue);
    }

    @Override
    public WindowPartitionByStep<BigDecimal> stddevPopOver() {
        return this.field.stddevPopOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> stddevSampOver() {
        return this.field.stddevSampOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> varPopOver() {
        return this.field.varPopOver();
    }

    @Override
    public WindowPartitionByStep<BigDecimal> varSampOver() {
        return this.field.varSampOver();
    }

    @Override
    public Field<String> upper() {
        return this.field.upper();
    }

    @Override
    public Field<String> lower() {
        return this.field.lower();
    }

    @Override
    public Field<String> trim() {
        return this.field.trim();
    }

    @Override
    public Field<String> rtrim() {
        return this.field.rtrim();
    }

    @Override
    public Field<String> ltrim() {
        return this.field.ltrim();
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length) {
        return this.field.rpad(length);
    }

    @Override
    public Field<String> rpad(int length) {
        return this.field.rpad(length);
    }

    @Override
    public Field<String> rpad(Field<? extends Number> length, Field<String> character) {
        return this.field.rpad(length, character);
    }

    @Override
    public Field<String> rpad(int length, char character) {
        return this.field.rpad(length, character);
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length) {
        return this.field.lpad(length);
    }

    @Override
    public Field<String> lpad(int length) {
        return this.field.lpad(length);
    }

    @Override
    public Field<String> lpad(Field<? extends Number> length, Field<String> character) {
        return this.field.lpad(length, character);
    }

    @Override
    public Field<String> lpad(int length, char character) {
        return this.field.lpad(length, character);
    }

    @Override
    public Field<String> repeat(Number count) {
        return this.field.repeat(count);
    }

    @Override
    public Field<String> repeat(Field<? extends Number> count) {
        return this.field.repeat(count);
    }

    @Override
    public Field<String> replace(Field<String> search) {
        return this.field.replace(search);
    }

    @Override
    public Field<String> replace(String search) {
        return this.field.replace(search);
    }

    @Override
    public Field<String> replace(Field<String> search, Field<String> replace) {
        return this.field.replace(search, replace);
    }

    @Override
    public Field<String> replace(String search, String replace) {
        return this.field.replace(search, replace);
    }

    @Override
    public Field<Integer> position(String search) {
        return this.field.position(search);
    }

    @Override
    public Field<Integer> position(Field<String> search) {
        return this.field.position(search);
    }

    @Override
    public Field<Integer> ascii() {
        return this.field.ascii();
    }

    @Override
    public Field<String> concat(Field<?>... fields) {
        return this.field.concat(fields);
    }

    @Override
    public Field<String> concat(String... values) {
        return this.field.concat(values);
    }

    @Override
    public Field<String> substring(int startingPosition) {
        return this.field.substring(startingPosition);
    }

    @Override
    public Field<String> substring(Field<? extends Number> startingPosition) {
        return this.field.substring(startingPosition);
    }

    @Override
    public Field<String> substring(int startingPosition, int length) {
        return this.field.substring(startingPosition, length);
    }

    @Override
    public Field<String> substring(Field<? extends Number> startingPosition, Field<? extends Number> length) {
        return this.field.substring(startingPosition, length);
    }

    @Override
    public Field<Integer> length() {
        return this.field.length();
    }

    @Override
    public Field<Integer> charLength() {
        return this.field.charLength();
    }

    @Override
    public Field<Integer> bitLength() {
        return this.field.bitLength();
    }

    @Override
    public Field<Integer> octetLength() {
        return this.field.octetLength();
    }

    @Override
    public Field<Integer> extract(DatePart datePart) {
        return this.field.extract(datePart);
    }

    @Override
    public Field<T> greatest(T... others) {
        return this.field.greatest(others);
    }

    @Override
    public Field<T> greatest(Field<?>... others) {
        return this.field.greatest(others);
    }

    @Override
    public Field<T> least(T... others) {
        return this.field.least(others);
    }

    @Override
    public Field<T> least(Field<?>... others) {
        return this.field.least(others);
    }

    @Override
    public Field<T> nvl(T defaultValue) {
        return this.field.nvl(defaultValue);
    }

    @Override
    public Field<T> nvl(Field<T> defaultValue) {
        return this.field.nvl(defaultValue);
    }

    @Override
    public <Z> Field<Z> nvl2(Z valueIfNotNull, Z valueIfNull) {
        return this.field.nvl2(valueIfNotNull, valueIfNull);
    }

    @Override
    public <Z> Field<Z> nvl2(Field<Z> valueIfNotNull, Field<Z> valueIfNull) {
        return this.field.nvl2(valueIfNotNull, valueIfNull);
    }

    @Override
    public Field<T> nullif(T other) {
        return this.field.nullif(other);
    }

    @Override
    public Field<T> nullif(Field<T> other) {
        return this.field.nullif(other);
    }

    @Override
    public <Z> Field<Z> decode(T search, Z result) {
        return this.field.decode(search, result);
    }

    @Override
    public <Z> Field<Z> decode(T search, Z result, Object... more) {
        return this.field.decode(search, result, more);
    }

    @Override
    public <Z> Field<Z> decode(Field<T> search, Field<Z> result) {
        return this.field.decode(search, result);
    }

    @Override
    public <Z> Field<Z> decode(Field<T> search, Field<Z> result, Field<?>... more) {
        return this.field.decode(search, result, more);
    }

    @Override
    public Field<T> coalesce(T option, T... options) {
        return this.field.coalesce(option, options);
    }

    @Override
    public Field<T> coalesce(Field<T> option, Field<?>... options) {
        return this.field.coalesce(option, options);
    }
}
