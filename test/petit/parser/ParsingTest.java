package petit.parser;

import org.junit.jupiter.api.Test;

import petit.parser.PetitParser;

import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.primitive.CharacterParser.*;

import java.util.Arrays;

/**
 * Tests {@link PetitParser} parsing.
 */
class ParsingTest {

  @Test
  void testParse() {
    var parser = of('a');
    assertTrue(parser.parse("a").isSuccess());
    assertFalse(parser.parse("b").isSuccess());
  }

  @Test
  void testAccepts() {
    var parser = of('a');
    assertTrue(parser.accept("a"));
    assertFalse(parser.accept("b"));
  }

  @Test
  void testMatches() {
    var parser = digit().seq(digit()).flatten();
    var expected = Arrays.asList("12", "23", "45");
    var actual = parser.matches("a123b45");
    assertEquals(expected, actual);
  }

  @Test
  void testMatchesSkipping() {
    var parser = digit().seq(digit()).flatten();
    var expected = Arrays.asList("12", "45");
    var actual = parser.matchesSkipping("a123b45");
    assertEquals(expected, actual);
  }

}
