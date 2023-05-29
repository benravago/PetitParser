package petit.parser;

import org.junit.jupiter.api.Test;

import petit.parser.primitive.CharacterParser;

import static petit.parser.Assertions.*;
import static petit.parser.primitive.CharacterParser.*;

/**
 * Tests {@link CharacterParser}.
 */
class CharacterTest {

  @Test
  void testAny() {
    var parser = any();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "any character expected");
  }

  @Test
  void testAnyWithMessage() {
    var parser = any("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testAnyOf() {
    var parser = anyOf("uncopyrightable");
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "g", 'g');
    assertSuccess(parser, "h", 'h');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertSuccess(parser, "p", 'p');
    assertSuccess(parser, "r", 'r');
    assertSuccess(parser, "t", 't');
    assertSuccess(parser, "y", 'y');
    assertFailure(parser, "x", "any of 'uncopyrightable' expected");
  }

  @Test
  void testAnyOfSpecial() {
    var parser = anyOf("\n\r\t");
    assertSuccess(parser, "\n", '\n');
    assertSuccess(parser, "\r", '\r');
    assertSuccess(parser, "\t", '\t');
    assertFailure(parser, "x", "any of '\\n\\r\\t' expected");
  }

  @Test
  void testAnyOfWithMessage() {
    var parser = anyOf("uncopyrightable", "wrong");
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "g", 'g');
    assertSuccess(parser, "h", 'h');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertSuccess(parser, "p", 'p');
    assertSuccess(parser, "r", 'r');
    assertSuccess(parser, "t", 't');
    assertSuccess(parser, "y", 'y');
    assertFailure(parser, "x", "wrong");
  }

  @Test
  void testAnyOfEmpty() {
    var parser = anyOf("");
    assertFailure(parser, "a", "any of '' expected");
    assertFailure(parser, "b", "any of '' expected");
    assertFailure(parser, "", "any of '' expected");
  }

  @Test
  void testNone() {
    var parser = none();
    assertFailure(parser, "a", "no character expected");
    assertFailure(parser, "b", "no character expected");
    assertFailure(parser, "", "no character expected");
  }

  @Test
  void testNoneWithMessage() {
    var parser = none("wrong");
    assertFailure(parser, "a", "wrong");
    assertFailure(parser, "b", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testNoneOf() {
    var parser = noneOf("uncopyrightable");
    assertSuccess(parser, "x", 'x');
    assertFailure(parser, "c", "none of 'uncopyrightable' expected");
    assertFailure(parser, "g", "none of 'uncopyrightable' expected");
    assertFailure(parser, "h", "none of 'uncopyrightable' expected");
    assertFailure(parser, "i", "none of 'uncopyrightable' expected");
    assertFailure(parser, "o", "none of 'uncopyrightable' expected");
    assertFailure(parser, "p", "none of 'uncopyrightable' expected");
    assertFailure(parser, "r", "none of 'uncopyrightable' expected");
    assertFailure(parser, "t", "none of 'uncopyrightable' expected");
    assertFailure(parser, "y", "none of 'uncopyrightable' expected");
  }

  @Test
  void testNoneOfSpecial() {
    var parser = noneOf("\b\f");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "\b", "none of '\\b\\f' expected");
    assertFailure(parser, "\f", "none of '\\b\\f' expected");
  }

  @Test
  void testNoneOfWithMessage() {
    var parser = noneOf("uncopyrightable", "wrong");
    assertSuccess(parser, "x", 'x');
    assertFailure(parser, "c", "wrong");
    assertFailure(parser, "g", "wrong");
    assertFailure(parser, "h", "wrong");
    assertFailure(parser, "i", "wrong");
    assertFailure(parser, "o", "wrong");
    assertFailure(parser, "p", "wrong");
    assertFailure(parser, "r", "wrong");
    assertFailure(parser, "t", "wrong");
    assertFailure(parser, "y", "wrong");
  }

  @Test
  void testNoneOfEmpty() {
    var parser = noneOf("");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "", "none of '' expected");
  }

  @Test
  void testOf() {
    var parser = of('a');
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "", "'a' expected");
  }

  @Test
  void testOfSpecial() {
    var parser = of('\u0001');
    assertSuccess(parser, "\u0001", '\u0001');
    assertFailure(parser, "a", "'\\u0001' expected");
  }

  @Test
  void testOfWithMessage() {
    var parser = of('a', "wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testDigit() {
    var parser = digit();
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "digit expected");
    assertFailure(parser, "", "digit expected");
  }

  @Test
  void testDigitWithMessage() {
    var parser = digit("wrong");
    assertSuccess(parser, "1", '1');
    assertSuccess(parser, "9", '9');
    assertFailure(parser, "a", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testLetter() {
    var parser = letter();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "letter expected");
    assertFailure(parser, "", "letter expected");
  }

  @Test
  void testLetterWithMessage() {
    var parser = letter("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "X", 'X');
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testLowerCase() {
    var parser = lowerCase();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "lowercase letter expected");
    assertFailure(parser, "0", "lowercase letter expected");
    assertFailure(parser, "", "lowercase letter expected");
  }

  @Test
  void testLowerCaseWithMessage() {
    var parser = lowerCase("wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "A", "wrong");
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testPatternWithSingle() {
    var parser = pattern("abc");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d", "[abc] expected");
    assertFailure(parser, "", "[abc] expected");
  }

  @Test
  void testPatternWithMessage() {
    var parser = pattern("abc", "wrong");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "d", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testPatternWithRange() {
    var parser = pattern("a-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d", "[a-c] expected");
    assertFailure(parser, "", "[a-c] expected");
  }

  @Test
  void testPatternWithOverlappingRange() {
    var parser = pattern("b-da-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertFailure(parser, "e", "[b-da-c] expected");
    assertFailure(parser, "", "[b-da-c] expected");
  }

  @Test
  void testPatternWithAdjacentRange() {
    var parser = pattern("c-ea-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[c-ea-c] expected");
    assertFailure(parser, "", "[c-ea-c] expected");
  }

  @Test
  void testPatternWithPrefixRange() {
    var parser = pattern("a-ea-c");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ea-c] expected");
    assertFailure(parser, "", "[a-ea-c] expected");
  }

  @Test
  void testPatternWithPostfixRange() {
    var parser = pattern("a-ec-e");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ec-e] expected");
    assertFailure(parser, "", "[a-ec-e] expected");
  }

  @Test
  void testPatternWithRepeatedRange() {
    var parser = pattern("a-ea-e");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "e", 'e');
    assertFailure(parser, "f", "[a-ea-e] expected");
    assertFailure(parser, "", "[a-ea-e] expected");
  }

  @Test
  void testPatternWithComposed() {
    var parser = pattern("ac-df-");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "c", 'c');
    assertSuccess(parser, "d", 'd');
    assertSuccess(parser, "f", 'f');
    assertSuccess(parser, "-", '-');
    assertFailure(parser, "b", "[ac-df-] expected");
    assertFailure(parser, "e", "[ac-df-] expected");
    assertFailure(parser, "g", "[ac-df-] expected");
    assertFailure(parser, "", "[ac-df-] expected");
  }

  @Test
  void testPatternWithNegatedSingle() {
    var parser = pattern("^a");
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "a", "[^a] expected");
    assertFailure(parser, "", "[^a] expected");
  }

  @Test
  void testPatternWithNegatedRange() {
    var parser = pattern("^a-c");
    assertSuccess(parser, "d", 'd');
    assertFailure(parser, "a", "[^a-c] expected");
    assertFailure(parser, "b", "[^a-c] expected");
    assertFailure(parser, "c", "[^a-c] expected");
    assertFailure(parser, "", "[^a-c] expected");
  }

  @Test
  void testRange() {
    var parser = range('e', 'o');
    assertFailure(parser, "d", "e..o expected");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "e..o expected");
    assertFailure(parser, "", "e..o expected");
  }

  @Test
  void testRangeWithMessage() {
    var parser = range('e', 'o', "wrong");
    assertFailure(parser, "d", "wrong");
    assertSuccess(parser, "e", 'e');
    assertSuccess(parser, "i", 'i');
    assertSuccess(parser, "o", 'o');
    assertFailure(parser, "p", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testUpperCase() {
    var parser = upperCase();
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "uppercase letter expected");
    assertFailure(parser, "0", "uppercase letter expected");
    assertFailure(parser, "", "uppercase letter expected");
  }

  @Test
  void testUpperCaseWithMessage() {
    var parser = upperCase("wrong");
    assertSuccess(parser, "Z", 'Z');
    assertFailure(parser, "z", "wrong");
    assertFailure(parser, "0", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testWhitespace() {
    var parser = whitespace();
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "whitespace expected");
    assertFailure(parser, "-", "whitespace expected");
    assertFailure(parser, "", "whitespace expected");
  }

  @Test
  void testWhitespaceWithMessage() {
    var parser = whitespace("wrong");
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "z", "wrong");
    assertFailure(parser, "-", "wrong");
    assertFailure(parser, "", "wrong");
  }

  @Test
  void testWord() {
    var parser = word();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "letter or digit expected");
    assertFailure(parser, "", "letter or digit expected");
  }

  @Test
  void testWordWithMessage() {
    var parser = word("wrong");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "0", '0');
    assertFailure(parser, "-", "wrong");
    assertFailure(parser, "", "wrong");
  }

}
