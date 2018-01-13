package org.battlecode.bc18.bots.util;

/**
 * An immutable 2-tuple.
 * @param <F> The type of `first`
 * @param <S> The type of `second`
 *
 * @author Ryan Butler
 */
public class Pair<F, S> {
    private final F first; //first member of pair
    private final S second; //second member of pair

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        //Generics are compile-time only, so we cant do instanceof Pair<F, S>
        if (!(obj instanceof Pair<?, ?>)) return false;
        Pair<?, ?> other = (Pair<?, ?>) obj;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    @Override
    public int hashCode() {
        return (first.hashCode() << 16) | (second.hashCode() & 0xFFFF);
    }
}
