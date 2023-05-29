package petit.parser.primitive;

import org.junit.jupiter.api.Test;

import petit.parser.primitive.CharacterPredicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link CharacterPredicate}.
 */
class CharacterPredicateTest {

  @Test
  void testAny() {
    var predicate = CharacterPredicate.any();
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
  }

  @Test
  void testAnyOf() {
    var predicate = CharacterPredicate.anyOf("uncopyrightable");
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('g'));
    assertTrue(predicate.test('h'));
    assertTrue(predicate.test('i'));
    assertTrue(predicate.test('o'));
    assertTrue(predicate.test('p'));
    assertTrue(predicate.test('r'));
    assertTrue(predicate.test('t'));
    assertTrue(predicate.test('y'));
    assertFalse(predicate.test('x'));
  }

  @Test
  void testAnyOfEmpty() {
    var predicate = CharacterPredicate.anyOf("");
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  void testNone() {
    var predicate = CharacterPredicate.none();
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  void testNoneOf() {
    var predicate = CharacterPredicate.noneOf("uncopyrightable");
    assertTrue(predicate.test('x'));
    assertFalse(predicate.test('c'));
    assertFalse(predicate.test('g'));
    assertFalse(predicate.test('h'));
    assertFalse(predicate.test('i'));
    assertFalse(predicate.test('o'));
    assertFalse(predicate.test('p'));
    assertFalse(predicate.test('r'));
    assertFalse(predicate.test('t'));
    assertFalse(predicate.test('y'));
  }

  @Test
  void testNoneOfEmpty() {
    var predicate = CharacterPredicate.noneOf("");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
  }

  @Test
  void testOf() {
    var predicate = CharacterPredicate.of('a');
    assertTrue(predicate.test('a'));
    assertFalse(predicate.test('b'));
  }

  @Test
  void testNot() {
    var source = CharacterPredicate.of('a');
    var predicate = source.not();
    assertFalse(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertSame(source, predicate.not());
  }

  @Test
  void testRangesInvalidSize() {
    assertThrows(IllegalArgumentException.class, () -> CharacterPredicate.ranges(new char[]{}, new char[]{'a'}));
  }

  @Test
  void testRangesInvalidOrder() {
    assertThrows(IllegalArgumentException.class, () -> CharacterPredicate.ranges(new char[]{'b'}, new char[]{'a'}));
  }

  @Test
  void testRangesInvalidSequence() {
    assertThrows(IllegalArgumentException.class, () -> CharacterPredicate.ranges(new char[]{'a', 'c'}, new char[]{'c', 'f'}));
  }

  @Test
  void testPatternWithSingle() {
    var predicate = CharacterPredicate.pattern("abc");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('d'));
  }

  @Test
  void testPatternWithRange() {
    var predicate = CharacterPredicate.pattern("a-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertFalse(predicate.test('d'));
  }

  @Test
  void testPatternWithOverlappingRange() {
    var predicate = CharacterPredicate.pattern("b-da-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertFalse(predicate.test('e'));
  }

  @Test
  void testPatternWithAdjacentRange() {
    var predicate = CharacterPredicate.pattern("c-ea-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  void testPatternWithPrefixRange() {
    var predicate = CharacterPredicate.pattern("a-ea-c");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  void testPatternWithPostfixRange() {
    var predicate = CharacterPredicate.pattern("a-ec-e");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  void testPatternWithRepeatedRange() {
    var predicate = CharacterPredicate.pattern("a-ea-e");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('b'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertFalse(predicate.test('f'));
  }

  @Test
  void testPatternWithComposed() {
    var predicate = CharacterPredicate.pattern("ac-df-");
    assertTrue(predicate.test('a'));
    assertTrue(predicate.test('c'));
    assertTrue(predicate.test('d'));
    assertTrue(predicate.test('f'));
    assertTrue(predicate.test('-'));
    assertFalse(predicate.test('b'));
    assertFalse(predicate.test('e'));
    assertFalse(predicate.test('g'));
  }

  @Test
  void testPatternWithNegatedSingle() {
    var predicate = CharacterPredicate.pattern("^a");
    assertTrue(predicate.test('b'));
    assertFalse(predicate.test('a'));
  }

  @Test
  void testPatternWithNegatedRange() {
    var predicate = CharacterPredicate.pattern("^a-c");
    assertTrue(predicate.test('d'));
    assertFalse(predicate.test('a'));
    assertFalse(predicate.test('b'));
    assertFalse(predicate.test('c'));
  }

  @Test
  void testRange() {
    var predicate = CharacterPredicate.range('e', 'o');
    assertFalse(predicate.test('d'));
    assertTrue(predicate.test('e'));
    assertTrue(predicate.test('i'));
    assertTrue(predicate.test('o'));
    assertFalse(predicate.test('p'));
  }

}
