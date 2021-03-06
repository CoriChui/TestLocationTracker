package com.kaonstudio.testlocationtracker.utils;

import org.jetbrains.annotations.Nullable;

public class SingleEvent<T> {

    private boolean hasBeenHandled = false;
    private final T content;

    public SingleEvent(T content) {
        this.content = content;
    }


    @Nullable
    public final T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public final T peekContent() {
        return this.content;
    }
}
