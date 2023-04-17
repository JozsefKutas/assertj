/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2023 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Stream;

import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Arrays.isArray;

class TypeChecks {

  static final Class<?>[] DEFAULT_ORDERED_COLLECTION_TYPES = array(List.class, SortedSet.class, LinkedHashSet.class);

  static final Class<?>[] ATOMIC_TYPES = array(AtomicReference.class, AtomicReferenceArray.class,
                                               AtomicBoolean.class,
                                               AtomicInteger.class, AtomicIntegerArray.class,
                                               AtomicLong.class, AtomicLongArray.class);

  static boolean isJavaType(Object value) {
    if (value == null) return false;
    String className = value.getClass().getName();
    return className.startsWith("java.")
      || className.startsWith("javax.")
      || className.startsWith("sun.")
      || className.startsWith("com.sun.");
  }

  static boolean isAnIterable(Object value) {
    // Don't consider Path as an Iterable as recursively comparing them leads to a stack overflow, here's why:
    // Iterable are compared element by element recursively
    // Ex: /tmp/foo.txt path has /tmp as its first element
    // so /tmp is going to be compared recursively but /tmp first element is itself leading to an infinite recursion
    // Don't consider ValueNode as an Iterable as they only contain one value and iterating them does not make sense.
    // Don't consider or ObjectNode as an Iterable as it holds a map but would only iterate on values and not entries.
    return value instanceof Iterable && !(value instanceof Path || isAJsonValueNode(value) || isAnObjectNode(value));
  }

  private static boolean isAJsonValueNode(Object value) {
    try {
      Class<?> valueNodeClass = Class.forName("com.fasterxml.jackson.databind.node.ValueNode");
      return valueNodeClass.isInstance(value);
    } catch (ClassNotFoundException e) {
      // value cannot be a ValueNode because the class couldn't be located
      return false;
    }
  }

  private static boolean isAnObjectNode(Object value) {
    try {
      Class<?> objectNodeClass = Class.forName("com.fasterxml.jackson.databind.node.ObjectNode");
      return objectNodeClass.isInstance(value);
    } catch (ClassNotFoundException e) {
      // value cannot be an ObjectNode because the class couldn't be located
      return false;
    }
  }

  static boolean isAnOrderedCollection(Object value) {
    return Stream.of(DEFAULT_ORDERED_COLLECTION_TYPES).anyMatch(type -> type.isInstance(value));
  }

  static boolean isAtomic(Object value) {
    return Stream.of(ATOMIC_TYPES).anyMatch(type -> type.isInstance(value));
  }

  static boolean isContainer(Object value) {
    return value instanceof Iterable || value instanceof Map || value instanceof Optional
      || isAtomic(value) || isArray(value);
  }

}
