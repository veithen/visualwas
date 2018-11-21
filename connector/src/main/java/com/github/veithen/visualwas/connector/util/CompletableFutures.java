/*
 * #%L
 * VisualWAS
 * %%
 * Copyright (C) 2013 - 2018 Andreas Veithen
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.github.veithen.visualwas.connector.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import com.google.common.util.concurrent.FutureCallback;

public final class CompletableFutures {
    private CompletableFutures() {}

    public static <T> CompletableFuture<T> callAsync(Callable<T> callable, Executor executor) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    public static <T> void addCallback(CompletableFuture<T> future, FutureCallback<? super T> callback, Executor executor) {
        future.whenCompleteAsync((result, t) -> {
            if (t != null) {
                if (t instanceof CompletionException) {
                    t = t.getCause();
                }
                callback.onFailure(t);
            } else {
                callback.onSuccess(result);
            }
        }, executor);
    }

    public static <T> CompletableFuture<T> immediateFailedFuture(Throwable throwable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        return future;
    }

    public static <T> void setFuture(CompletableFuture<T> that, CompletableFuture<? extends T> other) {
        other.whenComplete((result, t) -> {
            if (t == null) {
                that.complete(result);
            } else {
                that.completeExceptionally(t);
            }
        });
    }

    public static <T> CompletableFuture<List<T>> allAsList(List<CompletableFuture<T>> futures) {
        CompletableFuture<List<T>> listResult = new CompletableFuture<>();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()])).whenComplete((result, t) -> {
            if (t != null) {
                listResult.completeExceptionally(t);
            } else {
                List<T> list = new ArrayList<>(futures.size());
                for (CompletableFuture<? extends T> future : futures) {
                    try {
                        list.add(future.get());
                    } catch (ExecutionException | InterruptedException ex) {
                        // We should never get here.
                        throw new Error(ex);
                    }
                }
                listResult.complete(list);
            }
        });
        return listResult;
    }
}
