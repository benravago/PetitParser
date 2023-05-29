package petit.parser.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.ExamplesTest;
import petit.parser.utils.Profiler;

import java.util.ArrayList;

/**
 * Tests {@link Profiler}.
 */
class ProfilerTest {

  @Test
  void testSuccessfulProfile() {
    var actual = new ArrayList<Profiler.Profile>();
    var result = Profiler.on(ExamplesTest.IDENTIFIER, actual::add).parse("ab123");
    assertTrue(result.isSuccess());
    assertEquals(5, actual.size());
    assertEquals("FlattenParser", actual.get(0).parser.toString());
    assertEquals(1, actual.get(0).activationCount);
    assertTrue(actual.get(0).elapsedNanoseconds > 0);
    assertEquals("SequenceParser", actual.get(1).parser.toString());
    assertEquals(1, actual.get(1).activationCount);
    assertTrue(actual.get(1).elapsedNanoseconds > 0);
    assertEquals("PossessiveRepeatingParser[0..*]", actual.get(2).parser.toString());
    assertEquals(1, actual.get(2).activationCount);
    assertTrue(actual.get(2).elapsedNanoseconds > 0);
    assertEquals("CharacterParser[letter or digit expected]", actual.get(3).parser.toString());
    assertEquals(5, actual.get(3).activationCount);
    assertTrue(actual.get(3).elapsedNanoseconds > 0);
    assertEquals("CharacterParser[letter expected]", actual.get(4).parser.toString());
    assertEquals(1, actual.get(4).activationCount);
    assertTrue(actual.get(4).elapsedNanoseconds > 0);
  }

  @Test
  void testFailingProfile() {
    var actual = new ArrayList<Profiler.Profile>();
    var result = Profiler.on(ExamplesTest.IDENTIFIER, actual::add).parse("1");
    assertFalse(result.isSuccess());
    assertEquals(5, actual.size());
    assertEquals("FlattenParser", actual.get(0).parser.toString());
    assertEquals(1, actual.get(0).activationCount);
    assertTrue(actual.get(0).elapsedNanoseconds > 0);
    assertEquals("SequenceParser", actual.get(1).parser.toString());
    assertEquals(1, actual.get(1).activationCount);
    assertTrue(actual.get(1).elapsedNanoseconds > 0);
    assertEquals("PossessiveRepeatingParser[0..*]", actual.get(2).parser.toString());
    assertEquals(0, actual.get(2).activationCount);
    assertEquals(0, actual.get(2).elapsedNanoseconds);
    assertEquals("CharacterParser[letter or digit expected]", actual.get(3).parser.toString());
    assertEquals(0, actual.get(3).activationCount);
    assertEquals(0, actual.get(3).elapsedNanoseconds);
    assertEquals("CharacterParser[letter expected]", actual.get(4).parser.toString());
    assertEquals(1, actual.get(4).activationCount);
    assertTrue(actual.get(4).elapsedNanoseconds > 0);
  }

  @Test
  void testProfileToString() {
    var profiles = new ArrayList<Profiler.Profile>();
    Profiler.on(ExamplesTest.IDENTIFIER, profiles::add).parse("ab123");
    for (var profile : profiles) {
      var token = profile.toString().split("\t");
      assertEquals(4, token.length);
      assertEquals(token[0], Integer.toString(profile.activationCount));
      assertEquals(token[1], Integer.toString(profile.totalActivationCount));
      assertEquals(token[2], Long.toString(profile.elapsedNanoseconds));
      assertEquals(token[3], profile.parser.toString());
    }
  }

}
