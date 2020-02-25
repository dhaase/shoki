package com.jnape.palatable.shoki;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.Unit;
import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functions.builtin.fn2.Cons;
import com.jnape.palatable.shoki.api.Collection;
import com.jnape.palatable.shoki.api.EquivalenceRelation;
import com.jnape.palatable.shoki.api.HashingAlgorithm;
import com.jnape.palatable.shoki.api.Membership;

import static com.jnape.palatable.lambda.adt.Unit.UNIT;
import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Arrays.asList;

public final class ImmutableHashSet<A> implements Collection<Integer, A>, Membership<A> {

    private static final ImmutableHashSet<?> DEFAULT_EMPTY = new ImmutableHashSet<>(ImmutableHashMap.empty());

    private final ImmutableHashMap<A, Unit> table;

    private ImmutableHashSet(ImmutableHashMap<A, Unit> table) {
        this.table = table;
    }

    public ImmutableHashSet<A> add(A a) {
        return new ImmutableHashSet<>(table.put(a, UNIT));
    }

    @Override
    public SizeInfo.Known<Integer> sizeInfo() {
        return table.sizeInfo();
    }

    @Override
    public Maybe<A> head() {
        return table.head().fmap(Tuple2::_1);
    }

    @Override
    public ImmutableHashSet<A> tail() {
        return new ImmutableHashSet<>(table.tail());
    }

    @Override
    public boolean contains(A a) {
        return table.contains(a);
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }

    public static <A> ImmutableHashSet<A> empty(EquivalenceRelation<A> equivalenceRelation,
                                                HashingAlgorithm<A> hashingAlgorithm) {
        return new ImmutableHashSet<>(ImmutableHashMap.empty(equivalenceRelation, hashingAlgorithm));
    }

    @SuppressWarnings("unchecked")
    public static <A> ImmutableHashSet<A> empty() {
        return (ImmutableHashSet<A>) DEFAULT_EMPTY;
    }

    @SafeVarargs
    public static <A> ImmutableHashSet<A> of(A a, A... as) {
        return foldLeft(ImmutableHashSet::add, ImmutableHashSet.empty(), Cons.cons(a, asList(as)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        try {
            return obj instanceof ImmutableHashSet<?> &&
                ((ImmutableHashSet<A>) obj).table.sameEntries(table);
        } catch (ClassCastException cce) {
            return false;
        }
    }
}
