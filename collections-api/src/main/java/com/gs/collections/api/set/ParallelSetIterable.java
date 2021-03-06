/*
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.api.set;

import com.gs.collections.api.ParallelIterable;
import com.gs.collections.api.annotation.Beta;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.multimap.set.SetMultimap;

/**
 * @since 5.0
 */
@Beta
public interface ParallelSetIterable<T> extends ParallelIterable<T>
{
    /**
     * Creates a parallel iterable for selecting elements from the current iterable.
     */
    ParallelSetIterable<T> select(Predicate<? super T> predicate);

    <P> ParallelSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    /**
     * Creates a parallel iterable for rejecting elements from the current iterable.
     */
    ParallelSetIterable<T> reject(Predicate<? super T> predicate);

    <P> ParallelSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter);

    <S> ParallelSetIterable<S> selectInstancesOf(Class<S> clazz);

    <V> SetMultimap<V, T> groupBy(Function<? super T, ? extends V> function);

    <V> SetMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function);
}
