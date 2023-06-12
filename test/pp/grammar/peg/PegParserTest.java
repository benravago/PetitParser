package pp.grammar.peg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.PetitParser;

class PegParserTest {
  
  final static PetitParser parser = new PegParser();
  
  final static String input = """
    grammar <- ( nonterminal '<-' sp pattern )+
    pattern <- alternative ( '/' sp alternative )*
    alternative <- ( [!&]? sp suffix )+
    suffix <- primary ( [*+?] sp )*
    primary <- '(' sp pattern ')' sp / '.' sp / literal / charclass / nonterminal !'<-'
    literal <- ['] ( !['] . )* ['] sp
    charclass <- '[' ( !']' ( . '-' . / . ) )* ']' sp
    nonterminal <- [a-zA-Z]+ sp
    sp <- [ \t\n]*
  """;

  @Test
  void test() throws Exception {
    var result = parser.parse(input);
    assertTrue(result.isSuccess());
    assertNotNull(result.get());
  }

}
