package com.kaonstudio.testlocationtracker.ui.map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import kotlin.jvm.internal.Intrinsics;

public abstract class DataState<T> {

    @Nullable
    private final T data;
    @Nullable
    private final String message;

    @Nullable
    public final T getData() {
        return this.data;
    }

    @Nullable
    public final String getMessage() {
        return this.message;
    }

    private DataState() {
        data = null;
        message = null;
    }

    private DataState(@NotNull String message) {
        data = null;
        this.message = message;
    }

    private DataState(@NotNull T data) {
        this.data = data;
        this.message = null;
    }

    public static final class Success<T> extends DataState<T> {
        public Success(T data) {
            super(data);
        }
    }

    public static final class Loading<T> extends DataState<T> {
        public Loading() {
            super();
        }
    }

    public static final class Error<T> extends DataState<T> {
        public Error(@NotNull String message) {
            super(message);
        }
    }

}
