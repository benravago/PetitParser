package petit.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static petit.parser.primitive.CharacterParser.any;

import petit.parser.PetitParser;
import petit.parser.context.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests {@link Token}.
 */
class TokenTest {

  final PetitParser parser = any().map(Object::hashCode).token().star();
  final String buffer = "1\r12\r\n123\n1234";
  final List<Token> result = parser.parse(buffer).get();

  @Test
  void testBuffer() {
    var expected = new Object[]{ buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer, buffer };
    var actual = result.stream().map(Token::getBuffer).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testInput() {
    var expected = new Object[]{ "1", "\r", "1", "2", "\r", "\n", "1", "2", "3", "\n", "1", "2", "3", "4" };
    var actual = result.stream().map(Token::getInput).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testLength() {
    var expected = new Object[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    var actual = result.stream().map(Token::getLength).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testStart() {
    var expected = new Object[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };
    var actual = result.stream().map(Token::getStart).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testStop() {
    var expected = new Object[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
    var actual = result.stream().map(Token::getStop).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testValue() {
    var expected = new Object[]{ 49, 13, 49, 50, 13, 10, 49, 50, 51, 10, 49, 50, 51, 52 };
    var actual = result.stream().map(Token::getValue).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testLine() {
    var expected = new Object[]{ 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4 };
    var actual = result.stream().map(Token::getLine).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testColumn() {
    var expected = new Object[]{ 1, 2, 1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4 };
    var actual = result.stream().map(Token::getColumn).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testString() {
    var expected = new Object[]{ "Token[1:1]: 49", "Token[1:2]: 13", "Token[2:1]: 49", "Token[2:2]: 50","Token[2:3]: 13", "Token[2:4]: 10", "Token[3:1]: 49", "Token[3:2]: 50", "Token[3:3]: 51", "Token[3:4]: 10", "Token[4:1]: 49", "Token[4:2]: 50", "Token[4:3]: 51", "Token[4:4]: 52" };
    var actual = result.stream().map(Token::toString).toArray();
    assertArrayEquals(expected, actual);
  }

  @Test
  void testHashCode() {
    var uniques = result.stream().map(Token::hashCode).collect(Collectors.toSet());
    assertEquals(result.size(), uniques.size());
  }

  @Test
  void testEquals() {
    for (var i = 0; i < result.size(); i++) {
      var first = result.get(i);
      for (var j = 0; j < result.size(); j++) {
        var second = result.get(j);
        if (i == j) {
          assertEquals(first, second);
        } else {
          assertNotEquals(first, second);
        }
      }
      assertEquals(first, new Token(first.getBuffer(), first.getStart(), first.getStop(), first.getValue()));
      assertNotEquals(first, null);
      assertNotEquals(first, "Some random string");
      assertNotEquals(first, new Token("", first.getStart(), first.getStop(), first.getValue()));
      assertNotEquals(first, new Token(first.getBuffer(), first.getStart() + 1, first.getStop(), first.getValue()));
      assertNotEquals(first, new Token(first.getBuffer(), first.getStart(), first.getStop() + 1, first.getValue()));
      assertNotEquals(first, new Token(first.getBuffer(), first.getStart(), first.getStop(), null));
    }
  }

}
