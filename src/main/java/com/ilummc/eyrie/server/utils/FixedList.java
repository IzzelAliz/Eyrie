package com.ilummc.eyrie.server.utils;

import java.util.ArrayList;
import java.util.Collection;

public class FixedList<E> extends ArrayList<E> {

    private final int maxIndex;
    private int index = 0;

    public FixedList(int size) {
        super(size);
        maxIndex = size;
    }

    @Override
    public boolean add(E e) {
        if (this.size() == maxIndex) {
            this.set(index == maxIndex ? index = 0 : index++, e);
            return true;
        } else {
            index++;
            return super.add(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }
}
