package petit.parser;

import static petit.parser.Assertions.*;

import org.junit.jupiter.api.Test;

import petit.parser.primitive.EpsilonParser;
import petit.parser.primitive.FailureParser;
import petit.parser.primitive.StringParser;

/**
 * Tests {@link EpsilonParser}, {@link FailureParser} and {@link StringParser}.
 */
class PrimitiveTest {

  @Test
  void testEpsilon() {
    var parser = new EpsilonParser();
    assertSuccess(parser, "", null);
    assertSuccess(parser, "a", null, 0);
  }

  @Test
  void testFailure() {
    var parser = FailureParser.withMessage("wrong");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "a", "wrong");
  }

  @Test
  void testString() {
    var parser = StringParser.of("foo");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "", "foo expected");
    assertFailure(parser, "f", "foo expected");
    assertFailure(parser, "fo", "foo expected");
    assertFailure(parser, "Foo", "foo expected");
  }

  @Test
  void testStringWithMessage() {
    var parser = StringParser.of("foo", "wrong");
    assertSuccess(parser, "foo", "foo");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "f", "wrong");
    assertFailure(parser, "fo", "wrong");
    assertFailure(parser, "Foo", "wrong");
  }

  @Test
  void testStringIgnoreCase() {
    var parser = StringParser.ofIgnoringCase("foo");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "", "foo expected");
    assertFailure(parser, "f", "foo expected");
    assertFailure(parser, "Fo", "foo expected");
  }

  @Test
  void testStringIgnoreCaseWithMessage() {
    var parser = StringParser.ofIgnoringCase("foo", "wrong");
    assertSuccess(parser, "foo", "foo");
    assertSuccess(parser, "FOO", "FOO");
    assertSuccess(parser, "fOo", "fOo");
    assertFailure(parser, "", "wrong");
    assertFailure(parser, "f", "wrong");
    assertFailure(parser, "Fo", "wrong");
  }

}
