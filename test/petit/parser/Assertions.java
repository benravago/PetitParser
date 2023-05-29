package petit.parser;

import petit.parser.PetitParser;
import petit.parser.context.ParseError;

import static org.junit.jupiter.api.Assertions.*;

public interface Assertions {

  static <T> void assertSuccess(PetitParser parser, String input, T result) {
    assertSuccess(parser, input, result, input.length());
  }

  static <T> void assertSuccess(PetitParser parser, String input, T expected, int position) {
    var result = parser.parse(input);
    assertNotNull(result.toString());
    assertTrue(result.isSuccess(), "Expected parse success");
    assertFalse(result.isFailure(), "Expected parse success");
    assertEquals(position, result.getPosition(), "Position");
    assertEquals(expected, result.get(), "Result");
    assertNull(result.getMessage(), "No message expected");
    assertEquals(position, parser.fastParseOn(input, 0), "Fast parse");
    assertTrue(parser.accept(input), "Accept");
  }

  static <T> void assertFailure(PetitParser parser, String input) {
    assertFailure(parser, input, 0);
  }

  static <T> void assertFailure(PetitParser parser, String input, int position) {
    assertFailure(parser, input, position, null);
  }

  static <T> void assertFailure(PetitParser parser, String input, String message) {
    assertFailure(parser, input, 0, message);
  }

  static <T> void assertFailure(PetitParser parser, String input, int position, String message) {
    var result = parser.parse(input);
    assertNotNull(result.toString());
    assertFalse(result.isSuccess(), "Expected parse failure");
    assertTrue(result.isFailure(), "Expected parse failure");
    assertEquals(position, result.getPosition(), "Position");
    if (message != null) {
      assertEquals(message, result.getMessage(), "Message expected");
    }
    assertEquals(-1, parser.fastParseOn(input, 0), "Expected fast parse failure");
    assertFalse(parser.accept(input), "Accept");
    try {
      result.get();
    } catch (ParseError error) {
      assertEquals(result, error.getFailure());
      assertEquals(result.getMessage(), error.getMessage());
      return;
    }
    fail("Result#get() did not throw a ParseError");
  }

}
