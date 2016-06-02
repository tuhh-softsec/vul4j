/**
 * Copyright (c) 2015 Company.
 * All rights reserved.
 */
package com.mycompany.io;

@FunctionalInterface
public interface UseInstance<T, X extends Throwable> {
  void accept(T instance) throws X;
}
