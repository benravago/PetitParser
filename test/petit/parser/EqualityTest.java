package petit.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.primitive.CharacterParser.of;

import petit.parser.PetitParser;
import petit.parser.combinators.DelegateParser;
import petit.parser.primitive.CharacterParser;
import petit.parser.primitive.EpsilonParser;
import petit.parser.primitive.FailureParser;
import petit.parser.primitive.StringParser;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Tests {@link PetitParser#copy}, {@link PetitParser#equals(Object)}, and {@link PetitParser#replace(PetitParser, PetitParser)}.
 */
class EqualityTest {

  void verify(PetitParser parser) {
    var copy = parser.copy();
    // check copying
    assertNotSame(parser, copy);
    assertEquals(parser.getClass(), copy.getClass());
    assertEquals(parser.getChildren().size(), copy.getChildren().size());
    assertPairwiseSame(parser.getChildren(), copy.getChildren());
    assertEquals(parser.toString(), copy.toString());
    // check equality
    assertTrue(copy.isEqualTo(copy));
    assertTrue(parser.isEqualTo(copy));
    assertTrue(copy.isEqualTo(parser));
    assertTrue(parser.isEqualTo(parser));
    // check replacing
    var replaced = new ArrayList<PetitParser>();
    for (var i = 0; i < copy.getChildren().size(); i++) {
      var source = copy.getChildren().get(i);
      var target = CharacterParser.any();
      copy.replace(source, target);
      assertSame(target, copy.getChildren().get(i));
      replaced.add(target);
    }
    assertPairwiseSame(replaced, copy.getChildren());
  }

  void assertPairwiseSame(List<PetitParser> expected, List<PetitParser> actual) {
    assertEquals(expected.size(), actual.size());
    for (var i = 0; i < expected.size(); i++) {
      assertSame(expected.get(i), actual.get(i));
    }
  }

  @Test
  void differentChildren() {
    var first = of('a').or(of('b')).or(of('c'));
    var second = of('a').or(of('b')).or(of('d'));
    assertFalse(first.isEqualTo(second));
    assertFalse(second.isEqualTo(first));
  }

  @Test
  void differentSize() {
    var first = of('a').or(of('b')).or(of('c'));
    var second = of('a').or(of('b'));
    assertFalse(first.isEqualTo(second));
    assertFalse(second.isEqualTo(first));
  }

  @Test
  void any() {
    verify(CharacterParser.any());
  }

  @Test
  void and() {
    verify(CharacterParser.digit().and());
  }

  @Test
  void is() {
    verify(CharacterParser.of('a'));
  }

  @Test
  void digit() {
    verify(CharacterParser.digit());
  }

  @Test
  void delegate() {
    verify(new DelegateParser(CharacterParser.any()));
  }

  @Test
  void continuation() {
    verify(CharacterParser.digit().callCC((continuation, context) -> null));
  }

  @Test
  void end() {
    verify(CharacterParser.digit().end());
  }

  @Test
  void epsilon() {
    verify(new EpsilonParser());
  }

  @Test
  void failure() {
    verify(FailureParser.withMessage("failure"));
  }

  @Test
  void flatten1() {
    verify(CharacterParser.digit().flatten());
  }

  @Test
  void flatten2() {
    verify(CharacterParser.digit().flatten("digit"));
  }

  @Test
  void map() {
    verify(CharacterParser.digit().map(Function.identity()));
  }

  @Test
  void not() {
    verify(CharacterParser.digit().not());
  }

  @Test
  void optional() {
    verify(CharacterParser.digit().optional());
  }

  @Test
  void or() {
    verify(CharacterParser.digit().or(CharacterParser.word()));
  }

  @Test
  void plus() {
    verify(CharacterParser.digit().plus());
  }

  @Test
  void plusGreedy() {
    verify(CharacterParser.digit().plusGreedy(CharacterParser.word()));
  }

  @Test
  void plusLazy() {
    verify(CharacterParser.digit().plusLazy(CharacterParser.word()));
  }

  @Test
  void repeat() {
    verify(CharacterParser.digit().repeat(2, 3));
  }

  @Test
  void repeatGreedy() {
    verify(CharacterParser.digit().repeatGreedy(CharacterParser.word(), 2, 3));
  }

  @Test
  void repeatLazy() {
    verify(CharacterParser.digit().repeatLazy(CharacterParser.word(), 2, 3));
  }

  @Test
  void seq() {
    verify(CharacterParser.digit().seq(CharacterParser.word()));
  }

  @Test
  void settable() {
    verify(CharacterParser.digit().settable());
  }

  @Test
  void star() {
    verify(CharacterParser.digit().star());
  }

  @Test
  void starGreedy() {
    verify(CharacterParser.digit().starGreedy(CharacterParser.word()));
  }

  @Test
  void starLazy() {
    verify(CharacterParser.digit().starLazy(CharacterParser.word()));
  }

  @Test
  void string() {
    verify(StringParser.of("ab"));
  }

  @Test
  void stringIgnoringCase() {
    verify(StringParser.ofIgnoringCase("ab"));
  }

  @Test
  void times() {
    verify(CharacterParser.digit().times(2));
  }

  @Test
  void token() {
    verify(CharacterParser.digit().token());
  }

  @Test
  void trim() {
    verify(CharacterParser.digit().trim(CharacterParser.of('a'), CharacterParser.of('b')));
  }

}
