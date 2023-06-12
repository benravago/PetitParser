package pp.grammar.peg;

import static petit.parser.primitive.CharacterParser.any;
import static petit.parser.primitive.CharacterParser.anyOf;
import static petit.parser.primitive.CharacterParser.of;
import static petit.parser.primitive.CharacterParser.pattern;
import static petit.parser.primitive.CharacterParser.whitespace;
import static petit.parser.primitive.StringParser.of;

import petit.parser.tools.GrammarDefinition;

public class ParsingExpressionGrammar extends GrammarDefinition {

  public ParsingExpressionGrammar()  {
    def("start", whitespace().star().seq(ref("grammar")).seq(whitespace().star()) );

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

    def("grammar",     ref("nonterminal").seq(of("<-")).seq(ref("sp")).seq(ref("pattern")).plus() );
    def("pattern",     ref("alternative").seq( of('/').seq(ref("sp")).seq(ref("alternative")).star() ) );
    def("alternative", anyOf("!&").optional().seq(ref("sp")).seq(ref("suffix")).plus() );
    def("suffix",      ref("primary").seq( anyOf("*+?").seq(ref("sp")).star() ) );

    def("primary",     of('(').seq(ref("sp")).seq(ref("pattern")).seq(of(')')).seq(ref("sp"))
                       .or(of('.').seq(ref("sp")))
                       .or(ref("literal"))
                       .or(ref("charclass"))
                       .or(ref("nonterminal").seq(of("<-").not())) );

    def("literal",     of("'").seq( of("'").not().seq( any() ).star() ).seq(of("'")).seq(ref("sp")) );
    def("charclass",   of('[').seq( of(']').not().seq( any().seq(of('-')).seq(any() ).or(any()) ).star() ).seq(of(']')).seq(ref("sp")) );
    def("nonterminal", pattern("a-zA-Z").plus().seq(ref("sp")) );
    def("sp",          anyOf(" \t\n").star() );
  }
}
