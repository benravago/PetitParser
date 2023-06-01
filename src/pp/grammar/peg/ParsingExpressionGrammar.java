package pp.grammar.peg;

import petit.parser.tools.GrammarDefinition;

import static petit.parser.primitive.CharacterParser.whitespace;
import static petit.parser.primitive.CharacterParser.pattern;
import static petit.parser.primitive.CharacterParser.noneOf;
import static petit.parser.primitive.CharacterParser.anyOf;
import static petit.parser.primitive.CharacterParser.any;
import static petit.parser.primitive.CharacterParser.of;
import static petit.parser.primitive.StringParser.of;

public class ParsingExpressionGrammar extends GrammarDefinition {
  public ParsingExpressionGrammar()  {
    def("start", whitespace().star().seq(ref("grammar")) );
    
    /**
     *  from: A Text Pattern-Matching Tool based on Parsing Expression Grammars
     *    by: Roberto Ierusalimschy
     * 
     *  grammar     <- ( nonterminal '<-' sp pattern )+
     *  pattern     <- alternative ( '/' sp alternative )*
     *  alternative <- ( [!&]? sp suffix )+
     *  suffix      <- primary ( [*+?] sp )*
     *  primary     <- '(' sp pattern ')' sp
     *               / '.' sp
     *               / literal
     *               / charclass
     *               / nonterminal !'<-'
     *  literal     <- ['] ( !['] . )* ['] sp
     *  charclass   <- '[' ( !']' ( . '-' . / . ) )* ']' sp
     *  nonterminal <- [a-zA-Z]+ sp
     *  sp          <- [ \t\n]*
     */
    def("grammar", ref("nonterminal").seq(of("<-").trim()).seq(ref("pattern")).plus() );
    def("pattern", ref("alternative").seq(of('/').trim().seq(ref("alternative")).star()) );
    def("alternative", anyOf("!&").optional().trim().seq(ref("suffix")).plus() );
    def("suffix", ref("primary").seq(anyOf("*+?").trim().star()) );
    def("primary", of('(').trim().seq(ref("pattern")).seq(of(')')).trim()
                   .or(of('.').trim())
                   .or(ref("literal"))
                   .or(ref("charclass"))
                   .or(ref("nonterminal").seq(of("<-").not())) );
    def("literal", of("'").seq(noneOf("'").star()).seq(of("'")).trim() );
    def("charclass", of('[').seq(noneOf("]").seq(any().seq(of('-')).seq(any()).or(any()))).star().seq(of(']')).trim() );
    def("nonterminal", pattern("a-zA-Z").plus().trim() );
    def("sp", anyOf(" \t\n").star() );
  }

}
