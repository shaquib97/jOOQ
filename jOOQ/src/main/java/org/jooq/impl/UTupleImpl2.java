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

import static org.jooq.impl.QOM.tuple;

import org.jooq.Context;
import org.jooq.QueryPart;
// ...
// ...
import org.jooq.impl.QOM.UTuple2;

/**
 * A generic tuple of degree 2, which acts as a {@link QueryPart} for traversal,
 * replacement, etc.
 *
 * @author Lukas Eder
 */
final class UTupleImpl2<Q1 extends QueryPart, Q2 extends QueryPart>
extends
    AbstractQueryPart
implements
    UTuple2<Q1, Q2>
{

    private final Q1 part1;
    private final Q2 part2;

    UTupleImpl2(Q1 part1, Q2 part2) {
        this.part1 = part1;
        this.part2 = part2;
    }

    // -------------------------------------------------------------------------
    // XXX: QueryPart API
    // -------------------------------------------------------------------------

    @Override
    public final void accept(Context<?> ctx) {

        // This is unlikely to be called directly:
        ctx.sql('(').visit(part1).sql(", ").visit(part2).sql(')');
    }

    // -------------------------------------------------------------------------
    // XXX: Query Object Model
    // -------------------------------------------------------------------------

    @Override
    public final Q1 $1() {
        return part1;
    }

    @Override
    public final Q2 $2() {
        return part2;
    }

    @Override
    public final UTuple2<Q1, Q2> $1(Q1 newPart1) {
        return tuple(newPart1, part2);
    }

    @Override
    public final UTuple2<Q1, Q2> $2(Q2 newPart2) {
        return tuple(part1, newPart2);
    }

    @Override
    public final <R> R $traverse(Traverser<?, R> traverser) {
        return QOM.traverse(traverser, this, $1(), $2());
    }

    @Override
    public final QueryPart $replace(Replacer replacer) {
        return QOM.replace(
            this,
            $1(),
            $2(),
            (p1, p2) -> tuple(p1, p2),
            replacer
        );
    }
}
