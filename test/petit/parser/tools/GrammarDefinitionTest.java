package petit.parser.tools;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.PetitParser;
import petit.parser.primitive.EpsilonParser;
import petit.parser.tools.GrammarDefinition;
import petit.parser.tools.GrammarParser;
import static petit.parser.primitive.CharacterParser.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Tests {@link GrammarDefinition}.
 */
class GrammarDefinitionTest {

  class ListGrammarDefinition extends GrammarDefinition {
    ListGrammarDefinition() {
      def("start", ref("list").end());
      def("list", ref("element").seq(of(',').flatten()).seq(ref("list")).or(ref("element")));
      def("element", digit().plus().flatten());
    }
  }

  class ListParserDefinition extends ListGrammarDefinition {
    ListParserDefinition() {
      action("element", (Function<String, Integer>) Integer::parseInt);
    }
  }

  class BuggedGrammarDefinition extends GrammarDefinition {
    BuggedGrammarDefinition() {
      def("start", new EpsilonParser());
      def("directRecursion1", ref("directRecursion1"));
      def("indirectRecursion1", ref("indirectRecursion2"));
      def("indirectRecursion2", ref("indirectRecursion3"));
      def("indirectRecursion3", ref("indirectRecursion1"));
      def("delegation1", ref("delegation2"));
      def("delegation2", ref("delegation3"));
      def("delegation3", new EpsilonParser());
      def("unknownReference", ref("unknown"));
    }
  }

  class LambdaGrammarDefinition extends GrammarDefinition {
    LambdaGrammarDefinition() {
      def("start", ref("expression").end());
      def("expression", ref("variable").or(ref("abstraction")).or(ref("application")));
      def("variable", letter().seq(word().star()).flatten().trim());
      def("abstraction", of('\\').trim().seq(ref("variable")).seq(of('.').trim()).seq(ref("expression")));
      def("application", of('(').trim().seq(ref("expression")).seq(ref("expression")).seq(of(')').trim()));
    }
  }

  class ExpressionGrammarDefinition extends GrammarDefinition {
    ExpressionGrammarDefinition() {
      def("start", ref("terms").end());
      def("terms", ref("addition").or(ref("factors")));
      def("addition", ref("factors").separatedBy(pattern("+-").flatten().trim()));
      def("factors", ref("multiplication").or(ref("power")));
      def("multiplication", ref("power").separatedBy(pattern("*/").flatten().trim()));
      def("power", ref("primary").separatedBy(of('^').flatten().trim()));
      def("primary", ref("number").or(ref("parentheses")));
      def("number", of('-').flatten().trim().optional().seq(digit().plus()).seq(of('.').seq(digit().plus()).optional()));
      def("parentheses", of('(').flatten().trim().seq(ref("terms")).seq(of(')').flatten().trim()));
    }
  }

  final GrammarDefinition grammarDefinition = new ListGrammarDefinition();
  final GrammarDefinition parserDefinition = new ListParserDefinition();
  final GrammarDefinition buggedDefinition = new BuggedGrammarDefinition();
  final GrammarDefinition lambdaDefinition = new LambdaGrammarDefinition();
  final GrammarDefinition expressionDefinition = new ExpressionGrammarDefinition();

  @Test
  void testGrammar() {
    var parser = grammarDefinition.build();
    assertEquals(Arrays.asList("1", ",", "2"), parser.parse("1,2").get());
    assertEquals(Arrays.asList("1", ",", Arrays.asList("2", ",", "3")), parser.parse("1,2,3").get());
  }

  @Test
  void testParser() {
    var parser = parserDefinition.build();
    assertEquals(Arrays.asList(1, ",", 2), parser.parse("1,2").get());
    assertEquals(Arrays.asList(1, ",", Arrays.asList(2, ",", 3)), parser.parse("1,2,3").get());
  }

  @Test
  void testDirectRecursion1() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("directRecursion1"));
  }

  @Test
  void testIndirectRecursion1() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("indirectRecursion1"));
  }

  @Test
  void testIndirectRecursion2() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("indirectRecursion2"));
  }

  @Test
  void testIndirectRecursion3() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("indirectRecursion3"));
  }

  @Test
  void testUnknownReferenceOutside() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("unknown"));
  }

  @Test
  void testUnknownReferenceInside() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.build("unknownReference"));
  }

  @Test
  void testDuplicateDefinition() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.def("start", new EpsilonParser()));
  }

  @Test
  void testUnknownRedefinition() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.redef("unknown", new EpsilonParser()));
  }

  @Test
  void testUnknownRedefinitionFunction() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.redef("unknown", (start) -> new EpsilonParser()));
  }

  @Test
  void testUnknownAction() {
    assertThrows(IllegalStateException.class, () -> buggedDefinition.action("unknown", (object) -> null));
  }

  @Test
  void testReferenceParse() {
    assertThrows(UnsupportedOperationException.class, () -> buggedDefinition.ref("start").parse("abc"));
  }

  @Test
  void testReferenceCopy() {
    assertThrows(UnsupportedOperationException.class, () -> buggedDefinition.ref("start").copy());
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void testReferenceEquals() {
    var reference = buggedDefinition.ref("start");
    assertFalse(Objects.equals(reference, null));
    assertFalse(Objects.equals(reference, "start"));
  }

  @Test
  void testDelegation1() {
    assertTrue(buggedDefinition.build("delegation1") instanceof EpsilonParser);
  }

  @Test
  void testDelegation2() {
    assertTrue(buggedDefinition.build("delegation2") instanceof EpsilonParser);
  }

  @Test
  void testDelegation3() {
    assertTrue(buggedDefinition.build("delegation3") instanceof EpsilonParser);
  }

  @Test
  void testLambdaGrammar() {
    var parser = new GrammarParser(lambdaDefinition);
    assertTrue(parser.accept("x"));
    assertTrue(parser.accept("xy"));
    assertTrue(parser.accept("x12"));
    assertTrue(parser.accept("\\x.y"));
    assertTrue(parser.accept("\\x.\\y.z"));
    assertTrue(parser.accept("(x x)"));
    assertTrue(parser.accept("(x y)"));
    assertTrue(parser.accept("(x (y z))"));
    assertTrue(parser.accept("((x y) z)"));
  }

  @Test
  void testExpressionGrammar() {
    var parser = new GrammarParser(expressionDefinition, "start");
    assertTrue(parser.accept("1"));
    assertTrue(parser.accept("12"));
    assertTrue(parser.accept("1.23"));
    assertTrue(parser.accept("-12.3"));
    assertTrue(parser.accept("1 + 2"));
    assertTrue(parser.accept("1 + 2 + 3"));
    assertTrue(parser.accept("1 - 2"));
    assertTrue(parser.accept("1 - 2 - 3"));
    assertTrue(parser.accept("1 * 2"));
    assertTrue(parser.accept("1 * 2 * 3"));
    assertTrue(parser.accept("1 / 2"));
    assertTrue(parser.accept("1 / 2 / 3"));
    assertTrue(parser.accept("1 ^ 2"));
    assertTrue(parser.accept("1 ^ 2 ^ 3"));
    assertTrue(parser.accept("1 + (2 * 3)"));
    assertTrue(parser.accept("(1 + 2) * 3"));
  }

}
