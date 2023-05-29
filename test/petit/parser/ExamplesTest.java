package petit.parser;

import org.junit.jupiter.api.Test;

import static petit.parser.Assertions.*;
import static petit.parser.primitive.CharacterParser.*;
import static petit.parser.primitive.StringParser.of;

import petit.parser.PetitParser;
import petit.parser.combinators.SettableParser;
import petit.parser.tools.ExpressionBuilder;

import java.util.List;
import java.util.function.Function;

/**
 * Tests some small but realistic parser examples.
 */
public class ExamplesTest {

  public static final PetitParser IDENTIFIER =
    letter().seq(word().star()).flatten();

  static final PetitParser NUMBER =
    of('-').optional().seq(digit().plus()).seq(of('.').seq(digit().plus()).optional()).flatten();

  static final PetitParser STRING =
    of('"').seq(any().starLazy(of('"'))).seq(of('"')).flatten();

  static final PetitParser RETURN =
    of("return").seq(whitespace().plus().flatten()).seq(IDENTIFIER.or(NUMBER).or(STRING)).pick(-1);

  static final PetitParser JAVADOC =
    of("/**").seq(any().starLazy(of("*/"))).seq(of("*/")).flatten();

  static final PetitParser DOUBLE =
    digit().plus().seq(of('.').seq(digit().plus()).optional()).flatten().trim().map(Double::parseDouble);

  @Test
  void testIdentifierSuccess() {
    assertSuccess(IDENTIFIER, "a", "a");
    assertSuccess(IDENTIFIER, "a1", "a1");
    assertSuccess(IDENTIFIER, "a12", "a12");
    assertSuccess(IDENTIFIER, "ab", "ab");
    assertSuccess(IDENTIFIER, "a1b", "a1b");
  }

  @Test
  void testIdentifierIncomplete() {
    assertSuccess(IDENTIFIER, "a_", "a", 1);
    assertSuccess(IDENTIFIER, "a1-", "a1", 2);
    assertSuccess(IDENTIFIER, "a12+", "a12", 3);
    assertSuccess(IDENTIFIER, "ab ", "ab", 2);
  }

  @Test
  void testIdentifierFailure() {
    assertFailure(IDENTIFIER, "", "letter expected");
    assertFailure(IDENTIFIER, "1", "letter expected");
    assertFailure(IDENTIFIER, "1a", "letter expected");
  }

  @Test
  void testNumberPositiveSuccess() {
    assertSuccess(NUMBER, "1", "1");
    assertSuccess(NUMBER, "12", "12");
    assertSuccess(NUMBER, "12.3", "12.3");
    assertSuccess(NUMBER, "12.34", "12.34");
  }

  @Test
  void testNumberNegativeSuccess() {
    assertSuccess(NUMBER, "-1", "-1");
    assertSuccess(NUMBER, "-12", "-12");
    assertSuccess(NUMBER, "-12.3", "-12.3");
    assertSuccess(NUMBER, "-12.34", "-12.34");
  }

  @Test
  void testNumberIncomplete() {
    assertSuccess(NUMBER, "1..", "1", 1);
    assertSuccess(NUMBER, "12-", "12", 2);
    assertSuccess(NUMBER, "12.3.", "12.3", 4);
    assertSuccess(NUMBER, "12.34.", "12.34", 5);
  }

  @Test
  void testNumberFailure() {
    assertFailure(NUMBER, "", "digit expected");
    assertFailure(NUMBER, "-", 1, "digit expected");
    assertFailure(NUMBER, "-x", 1, "digit expected");
    assertFailure(NUMBER, ".", "digit expected");
    assertFailure(NUMBER, ".1", "digit expected");
  }

  @Test
  void testStringSuccess() {
    assertSuccess(STRING, "\"\"", "\"\"");
    assertSuccess(STRING, "\"a\"", "\"a\"");
    assertSuccess(STRING, "\"ab\"", "\"ab\"");
    assertSuccess(STRING, "\"abc\"", "\"abc\"");
  }

  @Test
  void testStringIncomplete() {
    assertSuccess(STRING, "\"\"x", "\"\"", 2);
    assertSuccess(STRING, "\"a\"x", "\"a\"", 3);
    assertSuccess(STRING, "\"ab\"x", "\"ab\"", 4);
    assertSuccess(STRING, "\"abc\"x", "\"abc\"", 5);
  }

  @Test
  void testStringFailure() {
    assertFailure(STRING, "\"", 1, "'\"' expected");
    assertFailure(STRING, "\"a", 2, "'\"' expected");
    assertFailure(STRING, "\"ab", 3, "'\"' expected");
    assertFailure(STRING, "a\"", "'\"' expected");
    assertFailure(STRING, "ab\"", "'\"' expected");
  }

  @Test
  void testReturnSuccess() {
    assertSuccess(RETURN, "return f", "f");
    assertSuccess(RETURN, "return  f", "f");
    assertSuccess(RETURN, "return foo", "foo");
    assertSuccess(RETURN, "return    foo", "foo");
    assertSuccess(RETURN, "return 1", "1");
    assertSuccess(RETURN, "return  1", "1");
    assertSuccess(RETURN, "return -2.3", "-2.3");
    assertSuccess(RETURN, "return    -2.3", "-2.3");
    assertSuccess(RETURN, "return \"a\"", "\"a\"");
    assertSuccess(RETURN, "return  \"a\"", "\"a\"");
  }

  @Test
  void testReturnFailure() {
    assertFailure(RETURN, "retur f", 0, "return expected");
    assertFailure(RETURN, "return1", 6, "whitespace expected");
    assertFailure(RETURN, "return  $", 8, "'\"' expected");
  }

  @Test
  void testJavaDoc() {
    assertSuccess(JAVADOC, "/** foo */", "/** foo */");
    assertSuccess(JAVADOC, "/** * * */", "/** * * */");
  }

  @Test
  void testExpression() {
    var number = digit().plus().flatten().trim().map((Function<String, Integer>) Integer::parseInt);

    var term = SettableParser.undefined();
    var prod = SettableParser.undefined();
    var prim = SettableParser.undefined();

    term.set(prod.seq(of('+').trim()).seq(term).map((List<Integer> values) -> values.get(0) + values.get(2)).or(prod));
    prod.set(prim.seq(of('*').trim()).seq(prod).map((List<Integer> values) -> values.get(0) * values.get(2)).or(prim));
    prim.set((of('(').trim().seq(term).seq(of(')').trim())).map((List<Integer> values) -> values.get(1)).or(number));

    var start = term.end();

    assertSuccess(start, "1 + 2 * 3", 7);
    assertSuccess(start, "(1 + 2) * 3", 9);
  }

  @Test
  void testExpressionBuilderWithSettableExample() throws Exception {
    var recursion = SettableParser.undefined();
    var bracket = of('(').seq(recursion).seq(of(')')).map((List<Double> values) -> values.get(1));

    var builder = new ExpressionBuilder();
    builder.group().primitive(bracket.or(DOUBLE));

    initOperators(builder);

    recursion.set(builder.build());

    var parser = recursion.end();
    assertCalculatorExample(parser);
  }

  @Test
  void testExpressionBuilderWithWrapperExample() throws Exception {
    var builder = new ExpressionBuilder();
    builder.group().primitive(DOUBLE).wrapper(of('(').trim(), of(')').trim(),(List<Double> values) -> values.get(1));

    initOperators(builder);

    var parser = builder.build().end();
    assertCalculatorExample(parser);
  }

  void initOperators(ExpressionBuilder builder) {

    // negation is a prefix operator
    builder.group()
      .prefix(of('-').trim(),(List<Double> values) -> -values.get(1));

    // power is right-associative
    builder.group()
      .right(of('^').trim(),(List<Double> values) -> Math.pow(values.get(0), values.get(2)));

    // multiplication is left-associative
    builder.group()
      .left(of('*').trim(),(List<Double> values) -> values.get(0) * values.get(2))
      .left(of('/').trim(),(List<Double> values) -> values.get(0) / values.get(2));

    // addition is left-associative
    builder.group()
      .left(of('+').trim(),(List<Double> values) -> values.get(0) + values.get(2))
      .left(of('-').trim(),(List<Double> values) -> values.get(0) - values.get(2));
  }

  void assertCalculatorExample(PetitParser parser) {
    var intCalculator = parser.map(Double::intValue);
    assertSuccess(intCalculator, "-8", -8);
    assertSuccess(intCalculator, "1+2*3", 7);
    assertSuccess(intCalculator, "1*2+3", 5);
    assertSuccess(intCalculator, "8/4/2", 1);
    assertSuccess(intCalculator, "2^2^3", 256);
  }

}

