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

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.recursive.comparison.FieldLocation.rootFieldLocation;
import static org.assertj.core.util.Arrays.isArray;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

// logically immutable
public final class DualValue {

  final FieldLocation fieldLocation;
  final Object actual;
  final Object expected;
  private final int hashCode;

  public DualValue(List<String> path, Object actual, Object expected) {
    this(new FieldLocation(path), actual, expected);
  }

  static DualValue rootDualValue(Object actual, Object expected) {
    return new DualValue(rootFieldLocation(), actual, expected);
  }

  public DualValue(FieldLocation fieldLocation, Object actualFieldValue, Object expectedFieldValue) {
    this.fieldLocation = requireNonNull(fieldLocation, "fieldLocation must not be null");
    actual = actualFieldValue;
    expected = expectedFieldValue;
    hashCode = Objects.hash(actual, expected);
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof DualValue)) return false;
    DualValue that = (DualValue) other;
    return actual == that.actual && expected == that.expected && fieldLocation.equals(that.fieldLocation);
  }

  /**
   * If we want to detect potential cycles in the recursive comparison, we need to check if an object has already been visited.
   * <p>
   * We must ignore the {@link FieldLocation} otherwise we would not find cycles. Let's take for example a {@code Person} class
   * with a neighbor field. We have a cycle between the person instance and its neighbor instance, ex: Jack has Tim as neighbor
   * and vice versa, when we navigate to Tim we find that its neighbor is Jack, we have already visited it but the location is
   * different, Jack is both the root object and root.neighbor.neighbor (Jack=root, Tim=root.neighbor and Tim.neighbor=Jack)
   *
   * @param dualValue the {@link DualValue} to compare
   * @return true if dual values references the same values (ignoring the field location)
   */
  public boolean sameValues(DualValue dualValue) {
    return actual == dualValue.actual && expected == dualValue.expected;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return format("DualValue [fieldLocation=%s, actual=%s, expected=%s]", fieldLocation, actual, expected);
  }

  public List<String> getDecomposedPath() {
    return unmodifiableList(fieldLocation.getDecomposedPath());
  }

  public String getConcatenatedPath() {
    return fieldLocation.getPathToUseInRules();
  }

  public String getFieldName() {
    return fieldLocation.getFieldName();
  }

  public boolean isActualJavaType() {
    return TypeChecks.isJavaType(actual);
  }

  public boolean isExpectedJavaType() {
    return TypeChecks.isJavaType(expected);
  }

  public boolean hasSomeJavaTypeValue() {
    return isActualJavaType() || isExpectedJavaType();
  }

  public boolean isExpectedFieldAnArray() {
    return isArray(expected);
  }

  public boolean isActualFieldAnArray() {
    return isArray(actual);
  }

  public boolean isActualFieldAnOptional() {
    return actual instanceof Optional;
  }

  public boolean isActualFieldAnOptionalInt() {
    return actual instanceof OptionalInt;
  }

  public boolean isActualFieldAnOptionalLong() {
    return actual instanceof OptionalLong;
  }

  public boolean isActualFieldAnOptionalDouble() {
    return actual instanceof OptionalDouble;
  }

  public boolean isActualFieldAnEmptyOptionalOfAnyType() {
    return isActualFieldAnEmptyOptional()
           || isActualFieldAnEmptyOptionalInt()
           || isActualFieldAnEmptyOptionalLong()
           || isActualFieldAnEmptyOptionalDouble();
  }

  private boolean isActualFieldAnEmptyOptional() {
    return isActualFieldAnOptional() && !((Optional<?>) actual).isPresent();
  }

  private boolean isActualFieldAnEmptyOptionalInt() {
    return isActualFieldAnOptionalInt() && !((OptionalInt) actual).isPresent();
  }

  private boolean isActualFieldAnEmptyOptionalLong() {
    return isActualFieldAnOptionalLong() && !((OptionalLong) actual).isPresent();
  }

  private boolean isActualFieldAnEmptyOptionalDouble() {
    return isActualFieldAnOptionalDouble() && !((OptionalDouble) actual).isPresent();
  }

  public boolean isExpectedFieldAnOptional() {
    return expected instanceof Optional;
  }

  public boolean isExpectedFieldAnAtomicReference() {
    return expected instanceof AtomicReference;
  }

  public boolean isActualFieldAnAtomicReference() {
    return actual instanceof AtomicReference;
  }

  public boolean isExpectedFieldAnAtomicReferenceArray() {
    return expected instanceof AtomicReferenceArray;
  }

  public boolean isActualFieldAnAtomicReferenceArray() {
    return actual instanceof AtomicReferenceArray;
  }

  public boolean isExpectedFieldAnAtomicInteger() {
    return expected instanceof AtomicInteger;
  }

  public boolean isActualFieldAnAtomicInteger() {
    return actual instanceof AtomicInteger;
  }

  public boolean isExpectedFieldAnAtomicIntegerArray() {
    return expected instanceof AtomicIntegerArray;
  }

  public boolean isActualFieldAnAtomicIntegerArray() {
    return actual instanceof AtomicIntegerArray;
  }

  public boolean isExpectedFieldAnAtomicLong() {
    return expected instanceof AtomicLong;
  }

  public boolean isActualFieldAnAtomicLong() {
    return actual instanceof AtomicLong;
  }

  public boolean isExpectedFieldAnAtomicLongArray() {
    return expected instanceof AtomicLongArray;
  }

  public boolean isActualFieldAnAtomicLongArray() {
    return actual instanceof AtomicLongArray;
  }

  public boolean isExpectedFieldAnAtomicBoolean() {
    return expected instanceof AtomicBoolean;
  }

  public boolean isActualFieldAnAtomicBoolean() {
    return actual instanceof AtomicBoolean;
  }

  public boolean isActualFieldAMap() {
    return actual instanceof Map;
  }

  public boolean isExpectedFieldAMap() {
    return expected instanceof Map;
  }

  public boolean isActualFieldASortedMap() {
    return actual instanceof SortedMap;
  }

  public boolean isExpectedFieldASortedMap() {
    return expected instanceof SortedMap;
  }

  public boolean isActualFieldAnOrderedCollection() {
    return TypeChecks.isAnOrderedCollection(actual);
  }

  public boolean isExpectedFieldAnOrderedCollection() {
    return TypeChecks.isAnOrderedCollection(expected);
  }

  public boolean isActualFieldAnIterable() {
    return TypeChecks.isAnIterable(actual);
  }

  public boolean isExpectedFieldAnIterable() {
    return TypeChecks.isAnIterable(expected);
  }

  public boolean isExpectedAnEnum() {
    return expected != null && expected.getClass().isEnum();
  }

  public boolean isActualAnEnum() {
    return actual != null && actual.getClass().isEnum();
  }

  public boolean hasNoContainerValues() {
    return !TypeChecks.isContainer(actual) && !isExpectedAContainer();
  }

  // TODO test
  public boolean isExpectedAContainer() {
    return TypeChecks.isContainer(expected);
  }

  public boolean hasNoNullValues() {
    return actual != null && expected != null;
  }

  public boolean hasPotentialCyclingValues() {
    return isPotentialCyclingValue(actual) && isPotentialCyclingValue(expected);
  }

  private static boolean isPotentialCyclingValue(Object object) {
    if (object == null) return false;
    // java.lang are base types that can't cycle to themselves or other types
    // we could check more types, but that's a good start
    String canonicalName = object.getClass().getCanonicalName();
    // canonicalName is null for anonymous and local classes, return true as they can cycle back to other objects.
    if (canonicalName == null) return true;
    // enums can refer back to other object but since they are constants it is very unlikely that they generate cycles.
    if (object.getClass().isEnum()) return false;
    return !canonicalName.startsWith("java.lang");
  }

}
