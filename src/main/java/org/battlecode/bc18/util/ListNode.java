package org.battlecode.bc18.util;

import org.battlecode.bc18.pathfinder.Cell;

import java.util.Objects;

public class ListNode implements Cloneable{

    public Cell cell;
    private ListNode prev;
    private ListNode next;

    public ListNode(Cell cell, ListNode prev, ListNode next) {
        this.cell = cell;
        this.prev = prev;
        if (prev != null) prev.next = this;
        this.next = next;
        if (next != null) next.prev = this;
    }

    @Override
    public int hashCode() {
        return cell.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListNode)) return false;
        ListNode other = (ListNode) o;
        return Objects.equals(this.cell, other.cell) &&
            Objects.equals(this.prev, other.prev) &&
            Objects.equals(this.next, other.next);
    }

    @Override
    public ListNode clone() {
        return new ListNode(cell, prev == null ? null : prev.clone(), next == null ? null : next.clone());
    }

    public ListNode getNext() {
        return next;
    }

    public ListNode getPrev() {
        return prev;
    }

    public ListNode setNext(ListNode next) {
        ListNode old = this.next;
        old.prev = null;
        next.prev = this;
        this.next = next;
        return old;
    }

    public ListNode setPrev(ListNode prev) {
        ListNode old = this.prev;
        old.next = null;
        prev.next = this;
        this.prev = prev;
        return old;
    }
}
