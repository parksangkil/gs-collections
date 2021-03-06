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

package com.gs.collections.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.ByteIterable;
import com.gs.collections.api.CharIterable;
import com.gs.collections.api.DoubleIterable;
import com.gs.collections.api.FloatIterable;
import com.gs.collections.api.IntIterable;
import com.gs.collections.api.LongIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.ShortIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ByteHashBag;
import com.gs.collections.impl.bag.mutable.primitive.CharHashBag;
import com.gs.collections.impl.bag.mutable.primitive.DoubleHashBag;
import com.gs.collections.impl.bag.mutable.primitive.FloatHashBag;
import com.gs.collections.impl.bag.mutable.primitive.IntHashBag;
import com.gs.collections.impl.bag.mutable.primitive.LongHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ShortHashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Functions0;
import com.gs.collections.impl.block.factory.Functions2;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractRichIterableTestCase
{
    protected abstract <T> RichIterable<T> newWith(T... littleElements);

    @Test
    public void testNewCollection()
    {
        RichIterable<Object> collection = this.newWith();
        Verify.assertIterableEmpty(collection);
        Verify.assertIterableSize(0, collection);
    }

    @Test
    public void equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.newWith(1, 2, 3), this.newWith(1, 2, 3));
        Assert.assertNotEquals(this.newWith(1, 2, 3), this.newWith(1, 2));
    }

    @Test
    public void contains()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assert.assertTrue(collection.contains(1));
        Assert.assertTrue(collection.contains(4));
        Assert.assertFalse(collection.contains(5));
    }

    @Test
    public void containsAllIterable()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assert.assertTrue(collection.containsAllIterable(FastList.newListWith(1, 2)));
        Assert.assertFalse(collection.containsAllIterable(FastList.newListWith(1, 5)));
    }

    @Test
    public void containsAllArray()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assert.assertTrue(collection.containsAllArguments(1, 2));
        Assert.assertFalse(collection.containsAllArguments(1, 5));
    }

    @Test
    public void containsAllCollection()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assert.assertTrue(collection.containsAll(FastList.newListWith(1, 2)));
        Assert.assertFalse(collection.containsAll(FastList.newListWith(1, 5)));
    }

    @Test
    public void forEach()
    {
        MutableList<Integer> result = Lists.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWith((argument1, argument2) -> { result.add(argument1 + argument2); }, 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableBag<Integer> elements = Bags.mutable.of();
        MutableBag<Integer> indexes = Bags.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex((object, index) -> {
            elements.add(object);
            indexes.add(index);
        });
        Assert.assertEquals(Bags.mutable.of(1, 2, 3, 4), elements);
        Assert.assertEquals(Bags.mutable.of(0, 1, 2, 3), indexes);
    }

    @Test
    public void select()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3)), 1, 2);
        RichIterable<Integer> result = this.newWith(-1, 2, 3, 4, 5).select(Predicates.lessThan(3));
        Verify.assertNotContains(3, result);
        Verify.assertNotContains(4, result);
        Verify.assertNotContains(5, result);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Test
    public void selectWith()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3), 1, 2);
        RichIterable<Integer> result = this.newWith(-1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3);
        Verify.assertNotContains(3, result);
        Verify.assertNotContains(4, result);
        Verify.assertNotContains(5, result);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4, 5).selectWith(
                        Predicates2.<Integer>lessThan(),
                        3),
                1, 2);
    }

    @Test
    public void selectWith_target()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 1, 2);
        Verify.denyContainsAny(this.newWith(-1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 3, 4, 5);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()),
                1, 2);
    }

    @Test
    public void reject()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Test
    public void rejectWith()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()),
                3, 4);
    }

    @Test
    public void rejectWith_target()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()),
                3, 4);
    }

    @Test
    public void selectInstancesOf()
    {
        RichIterable<Number> numbers = this.<Number>newWith(1, 2.0, 3, 4.0, 5);
        Assert.assertEquals(HashBag.newBagWith(1, 3, 5), numbers.selectInstancesOf(Integer.class).toBag());
        Assert.assertEquals(HashBag.newBagWith(1, 2.0, 3, 4.0, 5), numbers.selectInstancesOf(Number.class).toBag());
    }

    @Test
    public void collect()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(Functions.getToString()), "1", "2", "3", "4");
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(Functions.getToString(), UnifiedSet.<String>newSet()), "1", "2", "3", "4");
    }

    @Test
    public void collectBoolean()
    {
        BooleanIterable result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive());
        Assert.assertEquals(BooleanHashBag.newBagWith(true, false), result.toBag());
    }

    @Test
    public void collectBooleanWithTarget()
    {
        MutableBooleanCollection target = new BooleanArrayList();
        BooleanIterable result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(BooleanHashBag.newBagWith(true, false), result.toBag());
    }

    @Test
    public void collectBooleanWithBagTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(BooleanHashBag.newBagWith(true, false), result);
    }

    @Test
    public void collectByte()
    {
        ByteIterable result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Assert.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3, (byte) 4), result.toBag());
    }

    @Test
    public void collectByteWithTarget()
    {
        MutableByteCollection target = new ByteArrayList();
        ByteIterable result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3, (byte) 4), result.toBag());
    }

    @Test
    public void collectByteWithBagTarget()
    {
        ByteHashBag target = new ByteHashBag();
        ByteHashBag result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3, (byte) 4), result);
    }

    @Test
    public void collectChar()
    {
        CharIterable result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Assert.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2, (char) 3, (char) 4), result.toBag());
    }

    @Test
    public void collectCharWithTarget()
    {
        MutableCharCollection target = new CharArrayList();
        CharIterable result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2, (char) 3, (char) 4), result.toBag());
    }

    @Test
    public void collectCharWithBagTarget()
    {
        CharHashBag target = new CharHashBag();
        CharHashBag result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2, (char) 3, (char) 4), result);
    }

    @Test
    public void collectDouble()
    {
        DoubleIterable result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Assert.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d, 4.0d), result.toBag());
    }

    @Test
    public void collectDoubleWithTarget()
    {
        MutableDoubleCollection target = new DoubleArrayList();
        DoubleIterable result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d, 4.0d), result.toBag());
    }

    @Test
    public void collectDoubleWithBagTarget()
    {
        DoubleHashBag target = new DoubleHashBag();
        DoubleHashBag result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d, 4.0d), result);
    }

    @Test
    public void collectFloat()
    {
        FloatIterable result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Assert.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f, 4.0f), result.toBag());
    }

    @Test
    public void collectFloatWithTarget()
    {
        MutableFloatCollection target = new FloatArrayList();
        FloatIterable result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f, 4.0f), result.toBag());
    }

    @Test
    public void collectFloatWithBagTarget()
    {
        FloatHashBag target = new FloatHashBag();
        FloatHashBag result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f, 4.0f), result);
    }

    @Test
    public void collectInt()
    {
        IntIterable result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Assert.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectIntWithTarget()
    {
        MutableIntCollection target = new IntArrayList();
        IntIterable result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectIntWithBagTarget()
    {
        IntHashBag target = new IntHashBag();
        IntHashBag result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), result);
    }

    @Test
    public void collectLong()
    {
        LongIterable result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Assert.assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectLongWithTarget()
    {
        MutableLongCollection target = new LongArrayList();
        LongIterable result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectLongWithBagTarget()
    {
        LongHashBag target = new LongHashBag();
        LongHashBag result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), result);
    }

    @Test
    public void collectShort()
    {
        ShortIterable result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Assert.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3, (short) 4), result.toBag());
    }

    @Test
    public void collectShortWithTarget()
    {
        MutableShortCollection target = new ShortArrayList();
        ShortIterable result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3, (short) 4), result.toBag());
    }

    @Test
    public void collectShortWithBagTarget()
    {
        ShortHashBag target = new ShortHashBag();
        ShortHashBag result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assert.assertSame("Target list sent as parameter not returned", target, result);
        Assert.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3, (short) 4), result);
    }

    @Test
    public void flatCollect()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Function<Integer, MutableList<String>> function = object -> FastList.newListWith(String.valueOf(object));

        Verify.assertListsEqual(
                FastList.newListWith("1", "2", "3", "4"),
                collection.flatCollect(function).toSortedList());

        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1", "2", "3", "4"),
                collection.flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void detect()
    {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detect(Predicates.equal(3)));
        Assert.assertNull(this.newWith(1, 2, 3, 4, 5).detect(Predicates.equal(6)));
    }

    @Test(expected = NoSuchElementException.class)
    public void min_empty_throws()
    {
        this.newWith().min(Comparators.naturalOrder());
    }

    @Test(expected = NoSuchElementException.class)
    public void max_empty_throws()
    {
        this.newWith().max(Comparators.naturalOrder());
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws()
    {
        this.newWith(1, null, 2).min(Comparators.naturalOrder());
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws()
    {
        this.newWith(1, null, 2).max(Comparators.naturalOrder());
    }

    @Test
    public void min()
    {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).min(Comparators.naturalOrder()));
    }

    @Test
    public void max()
    {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max(Comparators.naturalOrder()));
    }

    @Test(expected = NullPointerException.class)
    public void min_null_throws_without_comparator()
    {
        this.newWith(1, null, 2).min();
    }

    @Test(expected = NullPointerException.class)
    public void max_null_throws_without_comparator()
    {
        this.newWith(1, null, 2).max();
    }

    @Test
    public void min_without_comparator()
    {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(3, 1, 2).min());
    }

    @Test
    public void max_without_comparator()
    {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max());
    }

    @Test
    public void minBy()
    {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).minBy(Functions.getToString()));
    }

    @Test
    public void maxBy()
    {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).maxBy(Functions.getToString()));
    }

    @Test
    public void detectWith()
    {
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detectWith(Predicates2.equal(), 3));
        Assert.assertNull(this.newWith(1, 2, 3, 4, 5).detectWith(Predicates2.equal(), 6));
    }

    @Test
    public void detectIfNone()
    {
        Function0<Integer> function = new PassThruFunction0<Integer>(6);
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detectIfNone(Predicates.equal(3), function));
        Assert.assertEquals(Integer.valueOf(6), this.newWith(1, 2, 3, 4, 5).detectIfNone(Predicates.equal(6), function));
    }

    @Test
    public void detectWithIfNoneBlock()
    {
        Function0<Integer> function = new PassThruFunction0<Integer>(-42);
        Assert.assertEquals(
                Integer.valueOf(5),
                this.newWith(1, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.<Integer>greaterThan(),
                        4,
                        function));
        Assert.assertEquals(
                Integer.valueOf(-42),
                this.newWith(1, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.<Integer>lessThan(),
                        0,
                        function));
    }

    @Test
    public void allSatisfy()
    {
        Assert.assertTrue(this.newWith(1, 2, 3).allSatisfy(Predicates.instanceOf(Integer.class)));
        Assert.assertFalse(this.newWith(1, 2, 3).allSatisfy(Predicates.equal(1)));
    }

    @Test
    public void allSatisfyWith()
    {
        Assert.assertTrue(this.newWith(1, 2, 3).allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assert.assertFalse(this.newWith(1, 2, 3).allSatisfyWith(Predicates2.equal(), 1));
    }

    @Test
    public void noneSatisfy()
    {
        Assert.assertTrue(this.newWith(1, 2, 3).noneSatisfy(Predicates.instanceOf(Boolean.class)));
        Assert.assertFalse(this.newWith(1, 1, 3).noneSatisfy(Predicates.equal(1)));
        Assert.assertTrue(this.newWith(1, 2, 3).noneSatisfy(Predicates.equal(4)));
    }

    @Test
    public void noneSatisfyWith()
    {
        Assert.assertTrue(this.newWith(1, 2, 3).noneSatisfyWith(Predicates2.instanceOf(), Boolean.class));
        Assert.assertFalse(this.newWith(1, 2, 3).noneSatisfyWith(Predicates2.equal(), 1));
    }

    @Test
    public void anySatisfy()
    {
        Assert.assertFalse(this.newWith(1, 2, 3).anySatisfy(Predicates.instanceOf(String.class)));
        Assert.assertTrue(this.newWith(1, 2, 3).anySatisfy(Predicates.instanceOf(Integer.class)));
    }

    @Test
    public void anySatisfyWith()
    {
        Assert.assertFalse(this.newWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assert.assertTrue(this.newWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void count()
    {
        Assert.assertEquals(3, this.newWith(1, 2, 3).count(Predicates.instanceOf(Integer.class)));
    }

    @Test
    public void countWith()
    {
        Assert.assertEquals(3, this.newWith(1, 2, 3).countWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(
                this.newWith(1, 2, 3).collectIf(
                        Predicates.instanceOf(Integer.class),
                        Functions.getToString()),
                "1", "2", "3");
        Verify.assertContainsAll(
                this.newWith(1, 2, 3).collectIf(
                        Predicates.instanceOf(Integer.class),
                        Functions.getToString(),
                        UnifiedSet.<String>newSet()),
                "1", "2", "3");
    }

    @Test
    public void collectWith()
    {
        Assert.assertEquals(
                Bags.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1).toBag());
    }

    @Test
    public void collectWith_target()
    {
        Assert.assertEquals(
                Bags.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, FastList.<Integer>newList()).toBag());
    }

    @Test
    public void getFirst()
    {
        Assert.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assert.assertNotEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getFirst());
    }

    @Test
    public void getLast()
    {
        Assert.assertNotEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getLast());
        Assert.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
    }

    @Test
    public void isEmpty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Verify.assertIterableNotEmpty(this.newWith(1, 2));
        Assert.assertTrue(this.newWith(1, 2).notEmpty());
    }

    @Test
    public void iterator()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assert.assertTrue(iterator.hasNext());
            Integer integer = iterator.next();
            Assert.assertEquals(3, integer.intValue() + i);
        }
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_throws()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assert.assertTrue(iterator.hasNext());
            iterator.next();
        }
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }

    @Test
    public void injectInto()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Integer result = objects.injectInto(1, AddFunction.INTEGER);
        Assert.assertEquals(Integer.valueOf(7), result);
        int sum = objects.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assert.assertEquals(6, sum);
    }

    @Test
    public void injectIntoInt()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        int result = objects.injectInto(1, AddFunction.INTEGER_TO_INT);
        Assert.assertEquals(7, result);
        int sum = objects.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assert.assertEquals(6, sum);
    }

    @Test
    public void injectIntoLong()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long result = objects.injectInto(1, AddFunction.INTEGER_TO_LONG);
        Assert.assertEquals(7, result);
        long sum = objects.injectInto(0, AddFunction.INTEGER_TO_LONG);
        Assert.assertEquals(6, sum);
    }

    @Test
    public void injectIntoDouble()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        double result = objects.injectInto(1, AddFunction.INTEGER_TO_DOUBLE);
        Assert.assertEquals(7.0d, result, 0.001);
        double sum = objects.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        Assert.assertEquals(6.0d, sum, 0.001);
    }

    @Test
    public void injectIntoFloat()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        float result = objects.injectInto(1, AddFunction.INTEGER_TO_FLOAT);
        Assert.assertEquals(7.0f, result, 0.001f);
        float sum = objects.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        Assert.assertEquals(6.0f, sum, 0.001f);
    }

    @Test
    public void sumFloat()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        float expected = objects.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        double actual = objects.sumOfFloat(Integer::floatValue);
        Assert.assertEquals(expected, actual, 0.001);
    }

    @Test
    public void sumDouble()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        double expected = objects.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        double actual = objects.sumOfDouble(Integer::doubleValue);
        Assert.assertEquals(expected, actual, 0.001);
    }

    @Test
    public void sumInteger()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long expected = objects.injectInto(0L, AddFunction.INTEGER_TO_LONG);
        long actual = objects.sumOfInt(integer -> integer);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void sumLong()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long expected = objects.injectInto(0, AddFunction.INTEGER_TO_LONG);
        long actual = objects.sumOfLong(Integer::longValue);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void toArray()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        Integer[] array2 = objects.toArray(new Integer[3]);
        Verify.assertSize(3, array2);
    }

    @Test
    public void partition()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partition(IntegerPredicates.isEven());
        Assert.assertEquals(this.newWith(-2, 0, 2, 4, 6, 8), result.getSelected());
        Assert.assertEquals(this.newWith(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Test
    public void partitionWith()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partitionWith(Predicates2.in(), FastList.newListWith(-2, 0, 2, 4, 6, 8));
        Assert.assertEquals(this.newWith(-2, 0, 2, 4, 6, 8), result.getSelected());
        Assert.assertEquals(this.newWith(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Test
    public void toList()
    {
        MutableList<Integer> list = this.newWith(1, 2, 3, 4).toList();
        Verify.assertContainsAll(list, 1, 2, 3, 4);
    }

    @Test
    public void toBag()
    {
        MutableBag<Integer> bag = this.newWith(1, 2, 3, 4).toBag();
        Verify.assertContainsAll(bag, 1, 2, 3, 4);
    }

    @Test
    public void toSortedList_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(2, 1, 5, 3, 4);
        MutableList<Integer> list = integers.toSortedList();
        Verify.assertStartsWith(list, 1, 2, 3, 4, 5);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assert.assertEquals(FastList.newListWith(4, 3, 2, 1), list);
    }

    @Test(expected = NullPointerException.class)
    public void toSortedList_with_null()
    {
        this.newWith(2, 4, null, 1, 3).toSortedList();
    }

    @Test
    public void toSortedListBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedListBy(Functions.getToString());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
    }

    @Test
    public void toSortedSet_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3, 2, 1, 3, 4);
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSet_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 4, 2, 1, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.<Integer>reverseOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSetBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSetBy(Functions.getToString());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test(expected = NullPointerException.class)
    public void toSortedListBy_with_null()
    {
        this.newWith(2, 4, null, 1, 3).toSortedListBy(Functions.getIntegerPassThru());
    }

    @Test
    public void toSet()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void toMap()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableMap<String, String> map =
                integers.toMap(Functions.getToString(), Functions.getToString());
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void toSortedMap()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Functions.getIntegerPassThru(), Functions.getToString());
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), map.keySet().toList());
    }

    @Test
    public void toSortedMap_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Comparators.<Integer>reverseNaturalOrder(),
                Functions.getIntegerPassThru(), Functions.getToString());
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(Comparators.<Integer>reverseNaturalOrder(), 1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), map.keySet().toList());
    }

    @Test
    public void testToString()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Assert.assertEquals("[1, 2, 3]", collection.toString());
    }

    @Test
    public void makeString()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Assert.assertEquals(collection.toString(), '[' + collection.makeString() + ']');
    }

    @Test
    public void makeStringWithSeparator()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Assert.assertEquals(collection.toString(), '[' + collection.makeString(", ") + ']');
    }

    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Assert.assertEquals(collection.toString(), collection.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        Assert.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparator()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, ", ");
        Assert.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<Object> collection = this.<Object>newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, "[", ", ", "]");
        Assert.assertEquals(collection.toString(), builder.toString());
    }

    @Test
    public void groupBy()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4, 5, 6, 7);
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);

        MutableMap<Boolean, RichIterable<Integer>> expected =
                UnifiedMap.newWithKeysValues(
                        Boolean.TRUE, this.newWith(1, 3, 5, 7),
                        Boolean.FALSE, this.newWith(2, 4, 6));

        Multimap<Boolean, Integer> multimap = collection.groupBy(isOddFunction);
        Assert.assertEquals(expected, multimap.toMap());

        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> multimap2 = collection.groupBy(
                isOddFunction,
                this.<Integer>newWith().groupBy(function).toMutable());
        Assert.assertEquals(expected, multimap2.toMap());
    }

    @Test
    public void groupByEach()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4, 5, 6, 7);

        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableMultimap<Integer, Integer> expected = this.<Integer>newWith().groupByEach(function).toMutable();
        for (int i = 1; i < 8; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 7));
        }

        Multimap<Integer, Integer> actual =
                collection.groupByEach(function);
        Assert.assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                collection.groupByEach(function, this.<Integer>newWith().groupByEach(function).toMutable());
        Assert.assertEquals(expected, actualWithTarget);
    }

    @Test
    public void zip()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        List<Object> nulls = Collections.nCopies(collection.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(collection.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(collection.size() - 1, null);

        RichIterable<Pair<String, Object>> pairs = collection.zip(nulls);
        Assert.assertEquals(
                collection.toSet(),
                pairs.collect(Functions.<String>firstOfPair()).toSet());
        Assert.assertEquals(
                nulls,
                pairs.collect(Functions.secondOfPair(), Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsPlusOne = collection.zip(nullsPlusOne);
        Assert.assertEquals(
                collection.toSet(),
                pairsPlusOne.collect(Functions.<String>firstOfPair()).toSet());
        Assert.assertEquals(nulls, pairsPlusOne.collect(Functions.secondOfPair(), Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsMinusOne = collection.zip(nullsMinusOne);
        Assert.assertEquals(collection.size() - 1, pairsMinusOne.size());
        Assert.assertTrue(collection.containsAllIterable(pairsMinusOne.collect(Functions.<String>firstOfPair())));

        Assert.assertEquals(
                collection.zip(nulls).toSet(),
                collection.zip(nulls, UnifiedSet.<Pair<String, Object>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<Pair<String, Integer>> pairs = collection.zipWithIndex();

        Assert.assertEquals(
                collection.toSet(),
                pairs.collect(Functions.<String>firstOfPair()).toSet());
        Assert.assertEquals(
                Interval.zeroTo(collection.size() - 1).toSet(),
                pairs.collect(Functions.<Integer>secondOfPair(), UnifiedSet.<Integer>newSet()));

        Assert.assertEquals(
                collection.zipWithIndex().toSet(),
                collection.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Test
    public void chunk()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = collection.chunk(2);
        RichIterable<Integer> sizes = groups.collect(RichIterable::size);
        Assert.assertEquals(FastList.newListWith(2, 2, 2, 1), sizes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        collection.chunk(0);
    }

    @Test
    public void chunk_large_size()
    {
        RichIterable<String> collection = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        Assert.assertEquals(collection, collection.chunk(10).getFirst());
    }

    @Test
    public void empty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Assert.assertTrue(this.newWith().isEmpty());
        Assert.assertFalse(this.newWith().notEmpty());
    }

    @Test
    public void notEmpty()
    {
        RichIterable<Integer> notEmpty = this.newWith(1);
        Verify.assertIterableNotEmpty(notEmpty);
    }

    @Test
    public void aggregateByMutating()
    {
        Function0<AtomicInteger> valueCreator = Functions0.zeroAtomicInteger();
        RichIterable<Integer> collection = this.newWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(Functions.getToString(), valueCreator, AtomicInteger::addAndGet);
        if (collection instanceof Set)
        {
            Assert.assertEquals(1, aggregation.get("1").intValue());
            Assert.assertEquals(2, aggregation.get("2").intValue());
            Assert.assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            Assert.assertEquals(3, aggregation.get("1").intValue());
            Assert.assertEquals(4, aggregation.get("2").intValue());
            Assert.assertEquals(3, aggregation.get("3").intValue());
        }
    }

    @Test
    public void aggregateByNonMutating()
    {
        MapIterable<String, Integer> aggregation =
                this.newWith(1, 1, 1, 2, 2, 3).aggregateBy(
                        Functions.getToString(),
                        Functions0.value(0),
                        Functions2.integerAddition());

        if (this.newWith(1, 1, 1, 2, 2, 3) instanceof Set)
        {
            Assert.assertEquals(1, aggregation.get("1").intValue());
            Assert.assertEquals(2, aggregation.get("2").intValue());
            Assert.assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            Assert.assertEquals(3, aggregation.get("1").intValue());
            Assert.assertEquals(4, aggregation.get("2").intValue());
            Assert.assertEquals(3, aggregation.get("3").intValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void groupByUniqueKey()
    {
        this.newWith(1, 2, 3).groupByUniqueKey(Functions.getPassThru());
    }
}
