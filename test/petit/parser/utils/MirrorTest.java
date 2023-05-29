package petit.parser.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.PetitParser;
import petit.parser.combinators.SettableParser;
import petit.parser.utils.Mirror;

import static petit.parser.combinators.SettableParser.undefined;
import static petit.parser.primitive.CharacterParser.lowerCase;
import static petit.parser.primitive.CharacterParser.upperCase;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Tests {@link Mirror}.
 */
class MirrorTest {

  @Test
  void testToString() {
    var parser = lowerCase();
    var mirror = Mirror.of(parser);
    assertEquals("CharacterParser[lowercase letter expected]", parser.toString());
    assertEquals("Mirror of CharacterParser[lowercase letter expected]", mirror.toString());
  }

  @Test
  void testSingleElementIteration() {
    var parser = lowerCase();
    var mirror = Mirror.of(parser);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(parser), parsers);
  }

  @Test
  void testNestedElementsIteration() {
    var parser3 = lowerCase();
    var parser2 = parser3.star();
    var parser1 = parser2.flatten();
    var mirror = Mirror.of(parser1);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(parser1, parser2, parser3), parsers);
  }

  @Test
  void testBranchedElementsIteration() {
    var parser3 = lowerCase();
    var parser2 = upperCase();
    var parser1 = parser2.seq(parser3);
    var mirror = Mirror.of(parser1);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(parser1, parser3, parser2), parsers);
  }

  @Test
  void testDuplicatedElementsIteration() {
    var parser2 = upperCase();
    var parser1 = parser2.seq(parser2);
    var mirror = Mirror.of(parser1);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(parser1, parser2), parsers);
  }

  @Test
  void testKnotParserIteration() {
    var parser1 = undefined();
    parser1.set(parser1);
    var mirror = Mirror.of(parser1);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.asList(parser1), parsers);
  }

  @Test
  void testLoopingParserIteration() {
    var parser1 = undefined();
    var parser2 = undefined();
    var parser3 = undefined();
    parser1.set(parser2);
    parser2.set(parser3);
    parser3.set(parser1);
    var mirror = Mirror.of(parser1);
    var parsers = mirror.stream().collect(Collectors.toList());
    assertEquals(Arrays.<PetitParser>asList(parser1, parser2, parser3), parsers);
  }

  @Test
  void testBasicIteration() {
    var parser = lowerCase();
    var iterator = Mirror.of(parser).iterator();
    assertTrue(iterator.hasNext());
    assertEquals(parser, iterator.next());
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, () -> iterator.next());
  }

  @Test
  void testIdentityTransformation() {
    var input = lowerCase().settable();
    var output = Mirror.of(input).transform(Function.identity());
    assertNotEquals(input, output);
    assertTrue(input.isEqualTo(output));
    assertNotEquals(input.getChildren().get(0), output.getChildren().get(0));
  }

  @Test
  void testReplaceRootTransformation() {
    var source = lowerCase();
    var target = upperCase();
    var output = Mirror.of(source).transform(parser -> source.isEqualTo(parser) ? target : parser);
    assertNotEquals(source, output);
    assertFalse(source.isEqualTo(output));
    assertEquals(output, target);
  }

  @Test
  void testSingleElementTransformation() {
    var source = lowerCase();
    var input = source.settable();
    var target = upperCase();
    var output = Mirror.of(input).transform(parser -> source.isEqualTo(parser) ? target : parser);
    assertNotEquals(input, output);
    assertFalse(input.isEqualTo(output));
    assertEquals(input.getChildren().get(0), source);
    assertEquals(output.getChildren().get(0), target);
  }

  @Test
  void testDoubleElementTransformation() {
    var source = lowerCase();
    var input = source.seq(source);
    var target = upperCase();
    var output = Mirror.of(input).transform(parser -> source.isEqualTo(parser) ? target : parser);
    assertNotEquals(input, output);
    assertFalse(input.isEqualTo(output));
    assertTrue(input.isEqualTo(source.seq(source)));
    assertEquals(input.getChildren().get(0), input.getChildren().get(1));
    assertTrue(output.isEqualTo(target.seq(target)));
    assertEquals(output.getChildren().get(0), output.getChildren().get(1));
  }

  @Test
  void testExistingLoopTransformation() {
    var input = undefined().settable().settable().settable();
    var settable = (SettableParser) input.getChildren().get(0).getChildren().get(0);
    settable.set(input);
    var output = Mirror.of(input).transform(Function.identity());
    assertNotEquals(input, output);
    assertTrue(input.isEqualTo(output));
    var inputs = Mirror.of(input).stream().collect(Collectors.toSet());
    var outputs = Mirror.of(output).stream().collect(Collectors.toSet());
    inputs.forEach(each -> assertFalse(outputs.contains(each)));
    outputs.forEach(each -> assertFalse(inputs.contains(each)));
  }

  @Test
  void testNewLoopTransformation() {
    var source = lowerCase();
    var target = undefined().settable().settable().settable();
    var settable = (SettableParser) target.getChildren().get(0).getChildren().get(0);
    settable.set(source);
    var output = Mirror.of(source).transform(parser -> source.isEqualTo(parser) ? target : parser);
    assertNotEquals(source, output);
    assertFalse(source.isEqualTo(output));
    assertTrue(output.isEqualTo(target));
  }

}
