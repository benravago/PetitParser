package petit.parser;

import org.junit.jupiter.api.Test;

import static petit.parser.Assertions.*;
import static petit.parser.primitive.CharacterParser.of;

/**
 * Various regressions amd tricky examples.
 */
class RegressionsTest {

  @Test
  void testFlattenTrim() {
    var parser = of('a').flatten().trim();
    assertSuccess(parser, "a", "a");
    assertSuccess(parser, " a ", "a");
    assertSuccess(parser, "  a  ", "a");
  }

  @Test
  void testTrimFlatten() {
    var parser = of('a').trim().flatten();
    assertSuccess(parser, "a", "a");
    assertSuccess(parser, " a ", " a ");
    assertSuccess(parser, "  a  ", "  a  ");
  }

}
