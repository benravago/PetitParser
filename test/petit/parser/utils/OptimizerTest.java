package petit.parser.utils;

import org.junit.jupiter.api.Test;

import petit.parser.utils.Optimizer;

import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.primitive.CharacterParser.lowerCase;

/**
 * Tests {@link Optimizer}.
 */
class OptimizerTest {

  @Test
  void testNoOptimization() {
    var input = lowerCase().settable().star();
    var output = new Optimizer().transform(input);
    assertTrue(output.isEqualTo(input));
  }

  @Test
  void testRemoveBasicDelegates() {
    var input = lowerCase().settable();
    var output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  void testRemoveNestedDelegates() {
    var input = lowerCase().settable().star();
    var output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase().star()));
  }

  @Test
  void testRemoveDoubleDelegates() {
    var input = lowerCase().settable().settable();
    var output = new Optimizer().removeDelegates().transform(input);
    assertTrue(output.isEqualTo(lowerCase()));
  }

  @Test
  void testRemoveDuplicates() {
    var input = lowerCase().seq(lowerCase());
    var output = new Optimizer().removeDuplicates().transform(input);
    assertTrue(input.isEqualTo(output));
    assertNotEquals(input.getChildren().get(0), input.getChildren().get(1));
    assertEquals(output.getChildren().get(0), output.getChildren().get(1));
  }

}
