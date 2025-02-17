/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: https://www.jooq.org/legal/licensing
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static java.lang.Boolean.TRUE;
import static org.jooq.impl.Keywords.K_CASE;
import static org.jooq.impl.Keywords.K_ELSE;
import static org.jooq.impl.Keywords.K_END;
import static org.jooq.impl.Keywords.K_NULL;
import static org.jooq.impl.Keywords.K_SWITCH;
import static org.jooq.impl.Keywords.K_THEN;
import static org.jooq.impl.Keywords.K_TRUE;
import static org.jooq.impl.Keywords.K_WHEN;
import static org.jooq.impl.Names.NQ_CASE;
import static org.jooq.impl.QOM.tuple;
import static org.jooq.impl.Tools.BooleanDataKey.DATA_FORCE_CASE_ELSE_NULL;

import java.util.List;
import java.util.Map;

import org.jooq.CaseConditionStep;
import org.jooq.CaseWhenStep;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function3;
// ...
import org.jooq.impl.QOM.CaseSimple;
import org.jooq.impl.QOM.UTuple2;
import org.jooq.impl.QOM.UnmodifiableList;

/**
 * @author Lukas Eder
 */
final class CaseWhenStepImpl<V, T>
extends
    AbstractField<T>
implements
    CaseWhenStep<V, T>,
    QOM.CaseSimple<V, T>
{

    private final Field<V>                          value;
    private final List<UTuple2<Field<V>, Field<T>>> when;
    private Field<T>                                else_;

    CaseWhenStepImpl(Field<V> value, Field<V> compareValue, Field<T> result) {
        this(value, result.getDataType());

        when(compareValue, result);
    }

    CaseWhenStepImpl(Field<V> value, Map<? extends Field<V>, ? extends Field<T>> map) {
        this(value, dataType(map));

        mapFields(map);
    }

    CaseWhenStepImpl(Field<V> value, DataType<T> type) {
        super(NQ_CASE, type);

        this.value = value;
        this.when = new QueryPartList<>();
    }

    @SuppressWarnings("unchecked")
    private static final <T> DataType<T> dataType(Map<? extends Field<?>, ? extends Field<T>> map) {
        if (map.isEmpty())
            return (DataType<T>) SQLDataType.OTHER;
        else
            return map.entrySet().iterator().next().getValue().getDataType();
    }

    // -------------------------------------------------------------------------
    // XXX: QueryPart API
    // -------------------------------------------------------------------------

    @Override
    public final Field<T> otherwise(T result) {
        return else_(result);
    }

    @Override
    public final Field<T> otherwise(Field<T> result) {
        return else_(result);
    }

    @Override
    public final Field<T> else_(T result) {
        return else_(Tools.field(result));
    }

    @Override
    public final Field<T> else_(Field<T> result) {
        this.else_ = result;

        return this;
    }

    @Override
    public final CaseWhenStep<V, T> when(V compareValue, T result) {
        return when(Tools.field(compareValue, value), Tools.field(result));
    }

    @Override
    public final CaseWhenStep<V, T> when(V compareValue, Field<T> result) {
        return when(Tools.field(compareValue, value), result);
    }

    @Override
    public final CaseWhenStep<V, T> when(Field<V> compareValue, T result) {
        return when(compareValue, Tools.field(result));
    }

    @Override
    public final CaseWhenStep<V, T> when(Field<V> compareValue, Field<T> result) {
        when.add(tuple(compareValue, result));

        return this;
    }

    @Override
    public final CaseWhenStep<V, T> mapValues(Map<V, T> values) {
        values.forEach((k, v) -> when(k, v));
        return this;
    }

    @Override
    public final CaseWhenStep<V, T> mapFields(Map<? extends Field<V>, ? extends Field<T>> fields) {
        fields.forEach((k, v) -> when(k, v));
        return this;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {










            // The DERBY dialect doesn't support the simple CASE clause
            case DERBY:
                acceptSearched(ctx);
                break;

            default:
                acceptNative(ctx);
                break;
        }
    }













































    private final void acceptSearched(Context<?> ctx) {

        CaseConditionStep<T> w = null;
        for (UTuple2<Field<V>, Field<T>> e : when)
            if (w == null)
                w = DSL.when(value.eq(e.$1()), e.$2());
            else
                w = w.when(value.eq(e.$1()), e.$2());

        if (w != null)
            if (else_ != null)
                ctx.visit(w.else_(else_));
            else
                ctx.visit(w);
    }

    private final void acceptNative(Context<?> ctx) {
        ctx.visit(K_CASE);

        ctx.sql(' ')
           .visit(value)
           .formatIndentStart();

        for (UTuple2<Field<V>, Field<T>> e : when)
            ctx.formatSeparator()
               .visit(K_WHEN).sql(' ')
               .visit(e.$1()).sql(' ')
               .visit(K_THEN).sql(' ')
               .visit(e.$2());

        if (else_ != null)
            ctx.formatSeparator()
               .visit(K_ELSE).sql(' ')
               .visit(else_);
        else if (TRUE.equals(ctx.data(DATA_FORCE_CASE_ELSE_NULL)))
            ctx.formatSeparator()
               .visit(K_ELSE).sql(' ').visit(K_NULL);

        ctx.formatIndentEnd()
           .formatSeparator()
           .visit(K_END);
    }

    // -------------------------------------------------------------------------
    // XXX: Query Object Model
    // -------------------------------------------------------------------------

    @Override
    public final Function3<? super Field<V>, ? super UnmodifiableList<? extends UTuple2<Field<V>, Field<T>>>, ? super Field<T>, ? extends CaseSimple<V, T>> $constructor() {
        return (v, w, e) -> {
            CaseWhenStepImpl<V, T> r = new CaseWhenStepImpl<>(v, getDataType());
            w.forEach(t -> r.when(t.$1(), t.$2()));
            r.else_(e);
            return r;
        };
    }

    @Override
    public final Field<V> $arg1() {
        return value;
    }

    @Override
    public final CaseSimple<V, T> $arg1(Field<V> newArg1) {
        return $constructor().apply(newArg1, $when(), $else());
    }

    @Override
    public final UnmodifiableList<? extends UTuple2<Field<V>, Field<T>>> $arg2() {
        return QOM.unmodifiable(when);
    }

    @Override
    public final CaseSimple<V, T> $arg2(UnmodifiableList<? extends UTuple2<Field<V>, Field<T>>> w) {
        return $constructor().apply($value(), w, $else());
    }

    @Override
    public final Field<T> $arg3() {
        return else_;
    }

    @Override
    public final CaseSimple<V, T> $arg3(Field<T> e) {
        return $constructor().apply($value(), $when(), e);
    }
}
