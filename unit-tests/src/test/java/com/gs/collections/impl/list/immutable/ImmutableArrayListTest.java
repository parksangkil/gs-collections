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

package com.gs.collections.impl.list.immutable;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.AddToList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit test for {@link ImmutableArrayList}.
 */
public class ImmutableArrayListTest extends AbstractImmutableListTestCase
{
    @Override
    protected ImmutableList<Integer> classUnderTest()
    {
        return this.newList(1, 2, 3);
    }

    @Test
    public void newWith()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3);
        ImmutableList<Integer> with = list.newWith(4);
        Assert.assertNotEquals(list, with);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4), with);
    }

    @Test
    public void newWithAll()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3);
        ImmutableList<Integer> withAll = list.newWithAll(FastList.newListWith(4, 5));
        Assert.assertNotEquals(list, withAll);
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), withAll);
    }

    @Test
    public void newWithOut()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3, 4);
        ImmutableList<Integer> without4 = list.newWithout(4);
        Assert.assertNotEquals(list, without4);
        Assert.assertEquals(FastList.newListWith(1, 2, 3), without4);

        ImmutableList<Integer> without1 = list.newWithout(1);
        Assert.assertNotEquals(list, without1);
        Assert.assertEquals(FastList.newListWith(2, 3, 4), without1);
    }

    @Test
    public void newWithoutAll()
    {
        ImmutableList<Integer> list = this.newList(1, 2, 3, 4, 5);
        ImmutableList<Integer> withoutAll = list.newWithoutAll(FastList.newListWith(4, 5));
        Assert.assertNotEquals(list, withoutAll);
        Assert.assertEquals(FastList.newListWith(1, 2, 3), withoutAll);
        Assert.assertEquals(FastList.newListWith(1, 2, 3), list.newWithoutAll(HashBag.newBagWith(4, 4, 5)));
        ImmutableList<Integer> largeList = this.newList(Interval.oneTo(20).toArray());
        ImmutableList<Integer> largeWithoutAll = largeList.newWithoutAll(FastList.newList(Interval.oneTo(10)));
        Assert.assertEquals(FastList.newList(Interval.fromTo(11, 20)), largeWithoutAll);
        ImmutableList<Integer> largeWithoutAll2 = largeWithoutAll.newWithoutAll(Interval.fromTo(11, 15));
        Assert.assertEquals(FastList.newList(Interval.fromTo(16, 20)), largeWithoutAll2);
        ImmutableList<Integer> largeWithoutAll3 = largeWithoutAll2.newWithoutAll(UnifiedSet.newSet(Interval.fromTo(16, 19)));
        Assert.assertEquals(FastList.newListWith(20), largeWithoutAll3);
    }

    private ImmutableArrayList<Integer> newList(Integer... elements)
    {
        return ImmutableArrayList.newListWith(elements);
    }

    private ImmutableList<Integer> newListWith(int one, int two)
    {
        return ImmutableArrayList.newListWith(one, two);
    }

    private ImmutableList<Integer> newListWith(int one, int two, int three)
    {
        return ImmutableArrayList.newListWith(one, two, three);
    }

    private ImmutableList<Integer> newListWith(int... littleElements)
    {
        Integer[] bigElements = new Integer[littleElements.length];
        for (int i = 0; i < littleElements.length; i++)
        {
            bigElements[i] = littleElements[i];
        }
        return ImmutableArrayList.newListWith(bigElements);
    }

    @Test
    public void newListWith()
    {
        ImmutableList<Integer> collection = ImmutableArrayList.newListWith(1);
        Assert.assertTrue(collection.notEmpty());
        Assert.assertEquals(1, collection.size());
        Assert.assertTrue(collection.contains(1));
    }

    @Test
    public void newListWithVarArgs()
    {
        ImmutableList<Integer> collection = this.newListWith(1, 2, 3, 4);
        Assert.assertTrue(collection.notEmpty());
        Assert.assertEquals(4, collection.size());
        Assert.assertTrue(collection.containsAllArguments(1, 2, 3, 4));
        Assert.assertTrue(collection.containsAllIterable(Interval.oneTo(4)));
    }

    @Test
    public void toSet()
    {
        ImmutableArrayList<Integer> integers = ImmutableArrayList.newListWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void toMap()
    {
        ImmutableArrayList<Integer> integers = ImmutableArrayList.newListWith(1, 2, 3, 4);
        MutableMap<String, String> map =
                integers.toMap(Functions.getToString(), Functions.getToString());
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void serialization()
    {
        ImmutableList<Integer> collection = ImmutableArrayList.newListWith(1, 2, 3, 4, 5);
        ImmutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Assert.assertEquals(5, deserializedCollection.size());
        Assert.assertTrue(deserializedCollection.containsAllArguments(1, 2, 3, 4, 5));
        Verify.assertEqualsAndHashCode(collection, deserializedCollection);
    }

    @Test(expected = IllegalArgumentException.class)
    public void forEachWithIndexIllegalFrom()
    {
        MutableList<Integer> result = Lists.mutable.of();
        this.newList(1, 2).forEachWithIndex(-1, 2, new AddToList(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void forEachWithIndexIllegalTo()
    {
        MutableList<Integer> result = Lists.mutable.of();
        this.newList(1, 2).forEachWithIndex(1, -2, new AddToList(result));
    }

    @Test
    @Override
    public void get()
    {
        final ImmutableList<Integer> list = this.classUnderTest();
        Verify.assertThrows(ArrayIndexOutOfBoundsException.class, new Runnable()
        {
            public void run()
            {
                list.get(list.size() + 1);
            }
        });
        Verify.assertThrows(ArrayIndexOutOfBoundsException.class, new Runnable()
        {
            public void run()
            {
                list.get(-1);
            }
        });
    }

    @Test(expected = IllegalStateException.class)
    @Override
    public void iteratorRemove()
    {
        this.classUnderTest().iterator().remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void groupByUniqueKey()
    {
        this.newList(1, 2, 3).groupByUniqueKey(Functions.getPassThru());
    }
}
