package org.battlecode.bc18.util;

import org.battlecode.bc18.pathfinder.Cell;

import java.util.Objects;

public class ListNode implements Cloneable{

    public Cell cell;
    public ListNode prev;
    public ListNode next;

    public ListNode(Cell cell, ListNode prev, ListNode next) {
        this.cell = cell;
        this.prev = prev;
        this.next = next;
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
}
