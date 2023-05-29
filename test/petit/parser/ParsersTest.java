package petit.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.Assertions.*;
import static petit.parser.primitive.CharacterParser.*;

import petit.parser.PetitParser;
import petit.parser.combinators.ChoiceParser;
import petit.parser.context.Context;
import petit.parser.context.Failure;
import petit.parser.context.Result;
import petit.parser.context.Token;
import petit.parser.primitive.CharacterParser;
import petit.parser.primitive.StringParser;
import petit.parser.repeating.RepeatingParser;
import petit.parser.utils.FailureJoiner;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Tests {@link PetitParser} factory methods.
 */
class ParsersTest {

  @Test
  void testAnd() {
    var parser = of('a').and();
    assertSuccess(parser, "a", 'a', 0);
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "");
  }

  @Test
  void testChoice0() {
    assertThrows(IllegalArgumentException.class, () -> new ChoiceParser());
  }

  @Test
  void testChoice2() {
    var parser = of('a').or(of('b'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "c");
    assertFailure(parser, "");
  }

  @Test
  void testChoice3() {
    var parser = of('a').or(of('b')).or(of('c'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", 'b');
    assertSuccess(parser, "c", 'c');
    assertFailure(parser, "d");
    assertFailure(parser, "");
  }

  static final Failure failureA0 = new Failure("A0", 0, "A0");
  static final Failure failureA1 = new Failure("A1", 1, "A1");
  static final Failure failureB0 = new Failure("B0", 0, "B0");
  static final Failure failureB1 = new Failure("B1", 1, "B1");

  static final PetitParser[] choiceParsers = new PetitParser[]{
    anyOf("ab").plus().seq(anyOf("12").plus()).flatten(),
    anyOf("ac").plus().seq(anyOf("13").plus()).flatten(),
    anyOf("ad").plus().seq(anyOf("14").plus()).flatten(),
  };

  @Test
  void testChoice_FailureJoiner_SelectFirst() {
    var failureJoiner = new FailureJoiner.SelectFirst();
    assertEquals(failureJoiner.apply(failureA0, failureB0), failureA0);
    assertEquals(failureJoiner.apply(failureB0, failureA0), failureB0);
    var choiceParser = new ChoiceParser(failureJoiner, choiceParsers);
    assertSuccess(choiceParser, "ab12", "ab12");
    assertSuccess(choiceParser, "ac13", "ac13");
    assertSuccess(choiceParser, "ad14", "ad14");
    assertFailure(choiceParser, "", "any of 'ab' expected");
    assertFailure(choiceParser, "a", 1, "any of '12' expected");
    assertFailure(choiceParser, "ab", 2, "any of '12' expected");
    assertFailure(choiceParser, "ac", 1, "any of '12' expected");
    assertFailure(choiceParser, "ad", 1, "any of '12' expected");
  }

  @Test
  void testChoice_FailureJoiner_SelectLast() {
    var failureJoiner = new FailureJoiner.SelectLast();
    assertEquals(failureJoiner.apply(failureA0, failureB0), failureB0);
    assertEquals(failureJoiner.apply(failureB0, failureA0), failureA0);
    var choiceParser = new ChoiceParser(failureJoiner, choiceParsers);
    assertSuccess(choiceParser, "ab12", "ab12");
    assertSuccess(choiceParser, "ac13", "ac13");
    assertSuccess(choiceParser, "ad14", "ad14");
    assertFailure(choiceParser, "", "any of 'ad' expected");
    assertFailure(choiceParser, "a", 1, "any of '14' expected");
    assertFailure(choiceParser, "ab", 1, "any of '14' expected");
    assertFailure(choiceParser, "ac", 1, "any of '14' expected");
    assertFailure(choiceParser, "ad", 2, "any of '14' expected");
  }

  @Test
  void testChoice_FailureJoiner_SelectFarthest() {
    var failureJoiner = new FailureJoiner.SelectFarthest();
    assertEquals(failureJoiner.apply(failureA0, failureB0), failureB0);
    assertEquals(failureJoiner.apply(failureA0, failureB1), failureB1);
    assertEquals(failureJoiner.apply(failureB0, failureA0), failureA0);
    assertEquals(failureJoiner.apply(failureB1, failureA0), failureB1);
    var choiceParser = new ChoiceParser(failureJoiner, choiceParsers);
    assertSuccess(choiceParser, "ab12", "ab12");
    assertSuccess(choiceParser, "ac13", "ac13");
    assertSuccess(choiceParser, "ad14", "ad14");
    assertFailure(choiceParser, "", "any of 'ad' expected");
    assertFailure(choiceParser, "a", 1, "any of '14' expected");
    assertFailure(choiceParser, "ab", 2, "any of '12' expected");
    assertFailure(choiceParser, "ac", 2, "any of '13' expected");
    assertFailure(choiceParser, "ad", 2, "any of '14' expected");
  }

  @Test
  void testChoice_FailureJoiner_SelectFarthestJoined() {
    var failureJoiner = new FailureJoiner.SelectFarthestJoined();
    assertEquals(failureJoiner.apply(failureA0, failureB1), failureB1);
    assertEquals(failureJoiner.apply(failureB1, failureA0), failureB1);
    assertEquals(failureJoiner.apply(failureA0, failureB0).getMessage(), "A0 " + "OR B0");
    assertEquals(failureJoiner.apply(failureB0, failureA0).getMessage(), "B0 " + "OR A0");
    assertEquals(failureJoiner.apply(failureA1, failureB1).getMessage(), "A1 " + "OR B1");
    assertEquals(failureJoiner.apply(failureB1, failureA1).getMessage(), "B1 " + "OR A1");
    var choiceParser = new ChoiceParser(failureJoiner, choiceParsers);
    assertSuccess(choiceParser, "ab12", "ab12");
    assertSuccess(choiceParser, "ac13", "ac13");
    assertSuccess(choiceParser, "ad14", "ad14");
    assertFailure(choiceParser, "", "any of 'ab' expected OR any of 'ac' " + "expected OR any of 'ad' expected");
    assertFailure(choiceParser, "a", 1, "any of '12' expected OR any of '13' " + "expected OR any of '14' expected");
    assertFailure(choiceParser, "ab", 2, "any of '12' expected");
    assertFailure(choiceParser, "ac", 2, "any of '13' expected");
    assertFailure(choiceParser, "ad", 2, "any of '14' expected");
  }

  @Test
  void testChoice_FailureJoiner_SelectFarthestJoined_CustomMessage() {
    var failureJoiner = new FailureJoiner.SelectFarthestJoined("; ");
    assertEquals(failureJoiner.apply(failureA0, failureB1), failureB1);
    assertEquals(failureJoiner.apply(failureB1, failureA0), failureB1);
    assertEquals(failureJoiner.apply(failureA0, failureB0).getMessage(), "A0;" + " B0");
    assertEquals(failureJoiner.apply(failureB0, failureA0).getMessage(), "B0;" + " A0");
    assertEquals(failureJoiner.apply(failureA1, failureB1).getMessage(), "A1;" + " B1");
    assertEquals(failureJoiner.apply(failureB1, failureA1).getMessage(), "B1;" + " A1");
    var choiceParser = new ChoiceParser(failureJoiner, choiceParsers);
    assertSuccess(choiceParser, "ab12", "ab12");
    assertSuccess(choiceParser, "ac13", "ac13");
    assertSuccess(choiceParser, "ad14", "ad14");
    assertFailure(choiceParser, "", "any of 'ab' expected; any of 'ac' " + "expected; any of 'ad' expected");
    assertFailure(choiceParser, "a", 1, "any of '12' expected; any of '13' " + "expected; any of '14' expected");
    assertFailure(choiceParser, "ab", 2, "any of '12' expected");
    assertFailure(choiceParser, "ac", 2, "any of '13' expected");
    assertFailure(choiceParser, "ad", 2, "any of '14' expected");
  }

  @Test
  void testEndOfInput() {
    var parser = of('a').end();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "aa", 1, "end of input expected");
  }

  @Test
  void testSettable() {
    var parser = of('a').settable();
    assertSuccess(parser, "a", 'a');
    assertFailure(parser, "b", 0, "'a' expected");
    parser.set(of('b'));
    assertSuccess(parser, "b", 'b');
    assertFailure(parser, "a", 0, "'b' expected");
  }

  @Test
  void testFlatten1() {
    var parser = CharacterParser.digit().repeat(2, RepeatingParser.UNBOUNDED).flatten();
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "a", 0, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertFailure(parser, "1a", 1, "digit expected");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  void testFlatten2() {
    var parser = CharacterParser.digit().repeat(2, RepeatingParser.UNBOUNDED).flatten("gimme a number");
    assertFailure(parser, "", 0, "gimme a number");
    assertFailure(parser, "a", 0, "gimme a number");
    assertFailure(parser, "1", 0, "gimme a number");
    assertFailure(parser, "1a", 0, "gimme a number");
    assertSuccess(parser, "12", "12");
    assertSuccess(parser, "123", "123");
    assertSuccess(parser, "1234", "1234");
  }

  @Test
  void testMap() {
    var parser = CharacterParser.digit().map((Function<Character, Integer>) Character::getNumericValue);
    assertSuccess(parser, "1", 1);
    assertSuccess(parser, "4", 4);
    assertSuccess(parser, "9", 9);
    assertFailure(parser, "");
    assertFailure(parser, "a");
  }

  @Test
  void testPick() {
    var parser = CharacterParser.digit().seq(CharacterParser.letter()).pick(1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  void testPickLast() {
    var parser = CharacterParser.digit().seq(CharacterParser.letter()).pick(-1);
    assertSuccess(parser, "1a", 'a');
    assertSuccess(parser, "2b", 'b');
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  void testPermute() {
    var parser = CharacterParser.digit().seq(CharacterParser.letter()).permute(1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  void testPermuteLast() {
    var parser = CharacterParser.digit().seq(CharacterParser.letter()).permute(-1, 0);
    assertSuccess(parser, "1a", Arrays.asList('a', '1'));
    assertSuccess(parser, "2b", Arrays.asList('b', '2'));
    assertFailure(parser, "");
    assertFailure(parser, "1", 1, "letter expected");
    assertFailure(parser, "12", 1, "letter expected");
  }

  @Test
  void testNeg1() {
    var parser = CharacterParser.digit().neg();
    assertFailure(parser, "1", 0);
    assertFailure(parser, "9", 0);
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0);
  }

  @Test
  void testNeg2() {
    var parser = CharacterParser.digit().neg("no digit expected");
    assertFailure(parser, "1", 0, "no digit expected");
    assertFailure(parser, "9", 0, "no digit expected");
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " ", ' ');
    assertFailure(parser, "", 0, "no digit expected");
  }

  @Test
  void testNeg3() {
    var parser = StringParser.of("foo").neg("no foo expected");
    assertFailure(parser, "foo", 0, "no foo expected");
    assertFailure(parser, "foobar", 0, "no foo expected");
    assertSuccess(parser, "f", 'f');
    assertSuccess(parser, " ", ' ');
  }

  @Test
  void testNot() {
    var parser = of('a').not("not a expected");
    assertFailure(parser, "a", "not a expected");
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  void testOptional() {
    var parser = of('a').optional();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "b", null, 0);
    assertSuccess(parser, "", null);
  }

  @Test
  void testPlus() {
    var parser = of('a').plus();
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  void testPlusGreedy() {
    var parser = CharacterParser.word().plusGreedy(CharacterParser.digit());
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 1, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1', '2'), 5);
  }

  @Test
  void testPlusLazy() {
    var parser = CharacterParser.word().plusLazy(CharacterParser.digit());
    assertFailure(parser, "");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "1", 1, "digit expected");
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "123", Arrays.asList('1'), 1);
    assertSuccess(parser, "a123", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
  }

  @Test
  void testTimes() {
    var parser = of('a').times(2);
    assertFailure(parser, "", 0, "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a'), 2);
  }

  @Test
  void testRepeat() {
    var parser = of('a').repeat(2, 3);
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "a", 1, "'a' expected");
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
    assertSuccess(parser, "aaaa", Arrays.asList('a', 'a', 'a'), 3);
  }

  @Test
  void testRepeatMinError1() {
    assertThrows(IllegalArgumentException.class, () -> of('a').repeat(-2, 5));
  }

  @Test
  void testRepeatMinError2() {
    assertThrows(IllegalArgumentException.class, () -> of('a').repeat(3, 2));
  }

  @Test
  void testRepeatUnbounded() {
    var builder = new StringBuilder();
    var list = new ArrayList<Character>();
    for (var i = 0; i < 100000; i++) {
      builder.append('a');
      list.add('a');
    }
    var parser = of('a').repeat(2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builder.toString(), list);
  }

  @Test
  void testRepeatGreedy() {
    var parser = CharacterParser.word().repeatGreedy(CharacterParser.digit(), 2, 4);
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "letter or digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "abc", 2, "digit expected");
    assertFailure(parser, "abcd", 2, "digit expected");
    assertFailure(parser, "abcde", 2, "digit expected");
    assertFailure(parser, "1", 1, "letter or digit expected");
    assertFailure(parser, "a1", 2, "digit expected");
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd1", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde1", 2, "digit expected");
    assertFailure(parser, "12", 2, "digit expected");
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "abcd12", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde12", 2, "digit expected");
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "abcd123", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde123", 2, "digit expected");
  }

  @Test
  void testRepeatGreedyUnbounded() {
    var builderLetter = new StringBuilder();
    var builderDigit = new StringBuilder();
    var listLetter = new ArrayList<Character>();
    var listDigit = new ArrayList<Character>();
    for (var i = 0; i < 100000; i++) {
      builderLetter.append('a');
      listLetter.add('a');
      builderDigit.append('1');
      listDigit.add('1');
    }
    builderLetter.append('1');
    builderDigit.append('1');
    var parser = CharacterParser.word().repeatGreedy(CharacterParser.digit(), 2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builderLetter.toString(), listLetter, listLetter.size());
    assertSuccess(parser, builderDigit.toString(), listDigit, listDigit.size());
  }

  @Test
  void testRepeatLazy() {
    var parser = CharacterParser.word().repeatLazy(CharacterParser.digit(), 2, 4);
    assertFailure(parser, "", 0, "letter or digit expected");
    assertFailure(parser, "a", 1, "letter or digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertFailure(parser, "abc", 3, "digit expected");
    assertFailure(parser, "abcd", 4, "digit expected");
    assertFailure(parser, "abcde", 4, "digit expected");
    assertFailure(parser, "1", 1, "letter or digit expected");
    assertFailure(parser, "a1", 2, "digit expected");
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd1", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde1", 4, "digit expected");
    assertFailure(parser, "12", 2, "digit expected");
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd12", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde12", 4, "digit expected");
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "abcd123", Arrays.asList('a', 'b', 'c', 'd'), 4);
    assertFailure(parser, "abcde123", 4, "digit expected");
  }

  @Test
  void testRepeatLazyUnbounded() {
    var builder = new StringBuilder();
    var list = new ArrayList<Character>();
    for (var i = 0; i < 100000; i++) {
      builder.append('a');
      list.add('a');
    }
    builder.append("1111");
    var parser = CharacterParser.word().repeatLazy(CharacterParser.digit(), 2, RepeatingParser.UNBOUNDED);
    assertSuccess(parser, builder.toString(), list, list.size());
  }

  @Test
  void testSequence2() {
    var parser = of('a').seq(of('b'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
  }

  @Test
  void testSequence3() {
    var parser = of('a').seq(of('b')).seq(of('c'));
    assertSuccess(parser, "abc", Arrays.asList('a', 'b', 'c'));
    assertFailure(parser, "");
    assertFailure(parser, "x");
    assertFailure(parser, "a", 1);
    assertFailure(parser, "ax", 1);
    assertFailure(parser, "ab", 2);
    assertFailure(parser, "abx", 2);
  }

  @Test
  void testStar() {
    var parser = of('a').star();
    assertSuccess(parser, "", Arrays.asList());
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "aa", Arrays.asList('a', 'a'));
    assertSuccess(parser, "aaa", Arrays.asList('a', 'a', 'a'));
  }

  @Test
  void testStarGreedy() {
    var parser = CharacterParser.word().starGreedy(CharacterParser.digit());
    assertFailure(parser, "", 0, "digit expected");
    assertFailure(parser, "a", 0, "digit expected");
    assertFailure(parser, "ab", 0, "digit expected");
    assertSuccess(parser, "1", Arrays.asList(), 0);
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList('1'), 1);
    assertSuccess(parser, "a12", Arrays.asList('a', '1'), 2);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b', '1'), 3);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c', '1'), 4);
    assertSuccess(parser, "123", Arrays.asList('1', '2'), 2);
    assertSuccess(parser, "a123", Arrays.asList('a', '1', '2'), 3);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b', '1', '2'), 4);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c', '1', '2'), 5);
  }

  @Test
  void testStarLazy() {
    var parser = CharacterParser.word().starLazy(CharacterParser.digit());
    assertFailure(parser, "");
    assertFailure(parser, "a", 1, "digit expected");
    assertFailure(parser, "ab", 2, "digit expected");
    assertSuccess(parser, "1", Arrays.asList(), 0);
    assertSuccess(parser, "a1", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab1", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc1", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "12", Arrays.asList(), 0);
    assertSuccess(parser, "a12", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab12", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc12", Arrays.asList('a', 'b', 'c'), 3);
    assertSuccess(parser, "123", Arrays.asList(), 0);
    assertSuccess(parser, "a123", Arrays.asList('a'), 1);
    assertSuccess(parser, "ab123", Arrays.asList('a', 'b'), 2);
    assertSuccess(parser, "abc123", Arrays.asList('a', 'b', 'c'), 3);
  }

  @Test
  void testToken() {
    var parser = of('a').star().token().trim();
    Token token = parser.parse(" aa ").get();
    assertEquals(1, token.getStart());
    assertEquals(3, token.getStop());
    assertEquals(Arrays.asList('a', 'a'), token.<List<Character>>getValue());
  }

  @Test
  void testTrim() {
    var parser = of('a').trim();
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, " a", 'a');
    assertSuccess(parser, "a ", 'a');
    assertSuccess(parser, " a ", 'a');
    assertSuccess(parser, "  a", 'a');
    assertSuccess(parser, "a  ", 'a');
    assertSuccess(parser, "  a  ", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, " b", 1, "'a' expected");
    assertFailure(parser, "  b", 2, "'a' expected");
  }

  @Test
  void testTrimCustom() {
    var parser = of('a').trim(of('*'));
    assertSuccess(parser, "a", 'a');
    assertSuccess(parser, "*a", 'a');
    assertSuccess(parser, "a*", 'a');
    assertSuccess(parser, "*a*", 'a');
    assertSuccess(parser, "**a", 'a');
    assertSuccess(parser, "a**", 'a');
    assertSuccess(parser, "**a**", 'a');
    assertFailure(parser, "", "'a' expected");
    assertFailure(parser, "b", "'a' expected");
    assertFailure(parser, "*b", 1, "'a' expected");
    assertFailure(parser, "**b", 2, "'a' expected");
  }

  @Test
  void testSeparatedBy() {
    var parser = of('a').separatedBy(of('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a'), 1);
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a'), 3);
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a'), 5);
  }

  @Test
  void testDelimitedBy() {
    var parser = of('a').delimitedBy(of('b'));
    assertFailure(parser, "", "'a' expected");
    assertSuccess(parser, "a", Arrays.asList('a'));
    assertSuccess(parser, "ab", Arrays.asList('a', 'b'));
    assertSuccess(parser, "aba", Arrays.asList('a', 'b', 'a'));
    assertSuccess(parser, "abab", Arrays.asList('a', 'b', 'a', 'b'));
    assertSuccess(parser, "ababa", Arrays.asList('a', 'b', 'a', 'b', 'a'));
    assertSuccess(parser, "ababab", Arrays.asList('a', 'b', 'a', 'b', 'a', 'b'));
  }

  @Test
  void testContinuationDelegating() {
    var parser = CharacterParser.digit().callCC(Function::apply);
    assertTrue(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
  }

  @Test
  void testContinuationRedirecting() {
    var parser = CharacterParser.digit().callCC((continuation, context) -> CharacterParser.letter().parseOn(context));
    assertFalse(parser.parse("1").isSuccess());
    assertTrue(parser.parse("a").isSuccess());
  }

  @Test
  void testContinuationResuming() {
    var continuations = new ArrayList<Function<Context, Result>>();
    var contexts = new ArrayList<Context>();
    var parser = CharacterParser.digit().callCC((continuation, context) -> {
      continuations.add(continuation);
      contexts.add(context);
      // we have to return something for now
      return context.failure("Abort");
    });
    // execute the parser twice to collect the continuations
    assertFalse(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
    // later we can execute the captured continuations
    assertTrue(continuations.get(0).apply(contexts.get(0)).isSuccess());
    assertFalse(continuations.get(1).apply(contexts.get(1)).isSuccess());
    // of course the continuations can be resumed multiple times
    assertTrue(continuations.get(0).apply(contexts.get(0)).isSuccess());
    assertFalse(continuations.get(1).apply(contexts.get(1)).isSuccess());
  }

  @Test
  void testContinuationSuccessful() {
    var parser = CharacterParser.digit().callCC((continuation, context) -> context.success("Always succeed"));
    assertTrue(parser.parse("1").isSuccess());
    assertTrue(parser.parse("a").isSuccess());
  }

  @Test
  void testContinuationFailing() {
    var parser = CharacterParser.digit().callCC((continuation, context) -> context.failure("Always fail"));
    assertFalse(parser.parse("1").isSuccess());
    assertFalse(parser.parse("a").isSuccess());
  }

}
