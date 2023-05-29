package petit.parser.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.ExamplesTest;
import petit.parser.utils.Tracer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Tests {@link petit.parser.utils.Tracer}.
 */
class TracerTest {

  @Test
  void testSuccessfulTrace() {
    var expected = Arrays.asList(
      "FlattenParser",
      "  SequenceParser",
      "    CharacterParser[letter expected]",
      "    Success[1:2]: a",
      "    PossessiveRepeatingParser[0..*]",
      "      CharacterParser[letter or digit expected]",
      "      Failure[1:2]: letter or digit expected",
      "    Success[1:2]: []",
      "  Success[1:2]: [a, []]",
      "Success[1:2]: a"
    );
    var actual = new ArrayList<Tracer.TraceEvent>();
    var result = Tracer.on(ExamplesTest.IDENTIFIER, actual::add).parse("a");
    assertTrue(result.isSuccess());
    assertEquals(expected, actual.stream().map(Tracer.TraceEvent::toString).collect(Collectors.toList()));
  }

  @Test
  void testFailingTrace() {
    var expected = Arrays.asList(
      "FlattenParser",
      "  SequenceParser",
      "    CharacterParser[letter expected]",
      "    Failure[1:1]: letter expected",
      "  Failure[1:1]: letter expected",
      "Failure[1:1]: letter expected"
    );
    var actual = new ArrayList<Tracer.TraceEvent>();
    var result = Tracer.on(ExamplesTest.IDENTIFIER, actual::add).parse("1");
    assertFalse(result.isSuccess());
    assertEquals(expected, actual.stream().map(Tracer.TraceEvent::toString).collect(Collectors.toList()));
  }

}
