package pp.grammar.peg;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import petit.parser.ExamplesTest;
import petit.parser.PetitParser;
import petit.parser.utils.Tracer;

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
