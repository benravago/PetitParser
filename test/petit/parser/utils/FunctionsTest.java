package petit.parser.utils;

import org.junit.jupiter.api.Test;

import petit.parser.utils.Functions;

import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.utils.Functions.*;

import java.util.List;
import static java.util.Arrays.asList;

/**
 * Tests {@link Functions}.
 */
class FunctionsTest {

  static final List<Object> EMPTY = asList();
  static final List<Object> ONE = asList('a');
  static final List<Object> TWO = asList('a', 'b');
  static final List<Object> THREE = asList('a', 'b', 'c');

  @Test
  void testFirstOfList() {
    assertEquals('a', firstOfList().apply(ONE));
    assertEquals('a', firstOfList().apply(TWO));
    assertEquals('a', firstOfList().apply(THREE));
  }

  @Test
  void testLastOfList() {
    assertEquals('a', lastOfList().apply(ONE));
    assertEquals('b', lastOfList().apply(TWO));
    assertEquals('c', lastOfList().apply(THREE));
  }

  @Test
  void testNthOfListOfList() {
    assertEquals('a', nthOfList(0).apply(ONE));
    assertEquals('a', nthOfList(0).apply(TWO));
    assertEquals('a', nthOfList(0).apply(THREE));
    assertEquals('b', nthOfList(1).apply(TWO));
    assertEquals('b', nthOfList(1).apply(THREE));
    assertEquals('c', nthOfList(2).apply(THREE));
    assertEquals('a', nthOfList(-1).apply(ONE));
    assertEquals('b', nthOfList(-1).apply(TWO));
    assertEquals('c', nthOfList(-1).apply(THREE));
    assertEquals('a', nthOfList(-2).apply(TWO));
    assertEquals('b', nthOfList(-2).apply(THREE));
    assertEquals('a', nthOfList(-3).apply(THREE));
  }

  @Test
  void testPermutationOfList() {
    assertEquals(EMPTY, permutationOfList().apply(EMPTY));
    assertEquals(EMPTY, permutationOfList().apply(THREE));
    assertEquals(asList('c', 'a'), permutationOfList(-1, 0).apply(THREE));
    assertEquals(asList('a', 'a'), permutationOfList(-3, 0).apply(THREE));
  }

  @Test
  void testWithoutSeparators() {
    assertEquals(EMPTY, withoutSeparators().apply(EMPTY));
    assertEquals(ONE, withoutSeparators().apply(ONE));
    assertEquals(ONE, withoutSeparators().apply(TWO));
    assertEquals(asList('a', 'c'), withoutSeparators().apply(THREE));
  }

  @Test
  void testConstant() {
    assertEquals((Object) 'a', constant('a').apply('b'));
    assertEquals((Object) 'b', constant('b').apply('c'));
  }

}
