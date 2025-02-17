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
package org.jooq;

import org.jetbrains.annotations.*;


import java.util.Collection;

import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

/**
 * An intermediate type for the construction of a <code>JOIN</code> clause,
 * where there must be a join criteria added using an <code>ON</code> clause
 * (with a {@link Condition}), or using a <code>USING</code> clause (with a list
 * of {@link Field}).
 * <p>
 * <h3>Referencing <code>XYZ*Step</code> types directly from client code</h3>
 * <p>
 * It is usually not recommended to reference any <code>XYZ*Step</code> types
 * directly from client code, or assign them to local variables. When writing
 * dynamic SQL, creating a statement's components dynamically, and passing them
 * to the DSL API statically is usually a better choice. See the manual's
 * section about dynamic SQL for details: <a href=
 * "https://www.jooq.org/doc/latest/manual/sql-building/dynamic-sql">https://www.jooq.org/doc/latest/manual/sql-building/dynamic-sql</a>.
 * <p>
 * Drawbacks of referencing the <code>XYZ*Step</code> types directly:
 * <ul>
 * <li>They're operating on mutable implementations (as of jOOQ 3.x)</li>
 * <li>They're less composable and not easy to get right when dynamic SQL gets
 * complex</li>
 * <li>They're less readable</li>
 * <li>They might have binary incompatible changes between minor releases</li>
 * </ul>
 *
 * @author Lukas Eder
 */
public interface TableOnStep<R extends Record> {

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>, connecting them
     * with each other with {@link Operator#AND}.
     */
    @NotNull
    @Support
    TableOnConditionStep<R> on(Condition condition);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>, connecting them
     * with each other with {@link Operator#AND}.
     */
    @NotNull
    @Support
    TableOnConditionStep<R> on(Condition... conditions);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>.
     */
    @NotNull
    @Support
    TableOnConditionStep<R> on(Field<Boolean> condition);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>.
     * <p>
     * <b>NOTE</b>: When inserting plain SQL into jOOQ objects, you must
     * guarantee syntax integrity. You may also create the possibility of
     * malicious SQL injection. Be sure to properly use bind variables and/or
     * escape literals when concatenated into SQL clauses!
     *
     * @see DSL#condition(SQL)
     * @see SQL
     */
    @NotNull
    @Support
    @PlainSQL
    TableOnConditionStep<R> on(SQL sql);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>.
     * <p>
     * <b>NOTE</b>: When inserting plain SQL into jOOQ objects, you must
     * guarantee syntax integrity. You may also create the possibility of
     * malicious SQL injection. Be sure to properly use bind variables and/or
     * escape literals when concatenated into SQL clauses!
     *
     * @see DSL#condition(String)
     * @see SQL
     */
    @NotNull
    @Support
    @PlainSQL
    TableOnConditionStep<R> on(String sql);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>.
     * <p>
     * <b>NOTE</b>: When inserting plain SQL into jOOQ objects, you must
     * guarantee syntax integrity. You may also create the possibility of
     * malicious SQL injection. Be sure to properly use bind variables and/or
     * escape literals when concatenated into SQL clauses!
     *
     * @see DSL#condition(String, Object...)
     * @see DSL#sql(String, Object...)
     * @see SQL
     */
    @NotNull
    @Support
    @PlainSQL
    TableOnConditionStep<R> on(String sql, Object... bindings);

    /**
     * Add an <code>ON</code> clause to the <code>JOIN</code>.
     * <p>
     * <b>NOTE</b>: When inserting plain SQL into jOOQ objects, you must
     * guarantee syntax integrity. You may also create the possibility of
     * malicious SQL injection. Be sure to properly use bind variables and/or
     * escape literals when concatenated into SQL clauses!
     *
     * @see DSL#condition(String, QueryPart...)
     * @see DSL#sql(String, QueryPart...)
     * @see SQL
     */
    @NotNull
    @Support
    @PlainSQL
    TableOnConditionStep<R> on(String sql, QueryPart... parts);

    /**
     * Join a table with the <code>USING(column [, column…])</code> syntax.
     * <p>
     * If this is not supported by your RDBMS, then jOOQ will try to emulate
     * this behaviour using the information provided in this query.
     */
    @NotNull
    @Support
    Table<Record> using(Field<?>... fields);

    /**
     * Join a table with the <code>USING(column [, column…])</code> syntax.
     * <p>
     * If this is not supported by your RDBMS, then jOOQ will try to emulate
     * this behaviour using the information provided in this query.
     */
    @NotNull
    @Support
    Table<Record> using(Collection<? extends Field<?>> fields);

    /**
     * Join the table on a non-ambiguous foreign key relationship between the
     * two joined tables.
     * <p>
     * See {@link #onKey(ForeignKey)} for examples.
     *
     * @see #onKey(ForeignKey)
     * @throws DataAccessException If there is no non-ambiguous key definition
     *             known to jOOQ
     */
    @NotNull
    @Support
    TableOnConditionStep<R> onKey() throws DataAccessException;

    /**
     * Join the table on a non-ambiguous foreign key relationship between the
     * two joined tables.
     * <p>
     * See {@link #onKey(ForeignKey)} for examples.
     *
     * @see #onKey(ForeignKey)
     * @throws DataAccessException If there is no non-ambiguous key definition
     *             known to jOOQ
     */
    @NotNull
    @Support
    TableOnConditionStep<R> onKey(TableField<?, ?>... keyFields) throws DataAccessException;

    /**
     * Join the table on a non-ambiguous foreign key relationship between the
     * two joined tables.
     * <p>
     * An example: <pre><code>
     * // There is a single foreign key relationship between A and B and it can
     * // be obtained by A.getReferencesTo(B) or vice versa. The order of A and
     * // B is not important
     * A.join(B).onKey();
     *
     * // There are several foreign key relationships between A and B. In order
     * // to disambiguate, you can provide a formal org.jooq.Key reference from
     * // the generated Keys class
     * A.join(B).onKey(key);
     *
     * // There are several foreign key relationships between A and B. In order
     * // to disambiguate, you can provide any non-ambiguous foreign key column
     * A.join(B).onKey(B.A_ID);
     * </code></pre>
     */
    @NotNull
    @Support
    TableOnConditionStep<R> onKey(ForeignKey<?, ?> key);
}
