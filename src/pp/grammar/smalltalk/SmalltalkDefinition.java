package pp.grammar.smalltalk;

import petit.parser.PetitParser;

import petit.parser.tools.GrammarDefinition;
import petit.parser.primitive.EpsilonParser;
import static petit.parser.primitive.CharacterParser.any;
import static petit.parser.primitive.CharacterParser.digit;
import static petit.parser.primitive.CharacterParser.of;
import static petit.parser.primitive.CharacterParser.pattern;
import static petit.parser.primitive.CharacterParser.whitespace;
import static petit.parser.primitive.CharacterParser.word;
import static petit.parser.primitive.StringParser.of;

/**
 * Smalltalk grammar definition.
 */
public class SmalltalkDefinition extends GrammarDefinition {

  public SmalltalkDefinition() {
    other(); /*
     *  whitespace            <- {s} / comment
     *  comment               <- '"' .*? '"'
     */
    number(); /*
     *  number                <- '-'? positiveNumber
     *  positiveNumber        <- scaledDecimal / float / integer
     *  integer               <- radixInteger / decimalInteger
     *  decimalInteger        <- digits
     *  digits                <- {d}+
     *  radixInteger          <- radixSpecifier 'r' radixDigits
     *  radixSpecifier        <- digits
     *  radixDigits           <- [0-9A-Z]+
     *  float                 <- mantissa ( exponentLetter exponent )?
     *  mantissa              <- digits '.' digits
     *  exponent              <- '-' decimalInteger
     *  exponentLetter        <- [edq]
     *  scaledDecimal         <- scaledMantissa 's' fractionalDigits?
     *  scaledMantissa        <- decimalInteger / mantissa
     *  fractionalDigits      <- decimalInteger
     */
    smalltalk(); /*
     *  start                 <- startMethod
     *  startMethod           <- method {end}
     *  method                <- methodDeclaration methodSequence
     *  methodDeclaration     <- keywordMethod / unaryMethod / binaryMethod
     *  methodSequence        <- periodToken* pragmas periodToken* temporaries periodToken* pragmas periodToken* statements
     *  keywordMethod         <- keywordToken variable+
     *  unaryMethod           <- identifierToken
     *  binaryMethod          <- binaryToken variable
     *  pragmas               <- pragma*
     *  pragma                <- '<' whitespace pragmaMessage '>' whitespace
     *  pragmaMessage         <- keywordPragma / unaryPragma / binaryPragma
     *  keywordPragma         <- keywordToken arrayItem+
     *  unaryPragma           <- identifierToken
     *  binaryPragma          <- binaryToken arrayItem
     *  temporaries           <- ( '|' whitespace variable* '|' whitespace )?
     *  statements            <- expression ( periodToken+ statements / periodToken* ) / return periodToken* / periodToken*
     *  return                <- '^' whitespace expression
     *  expression            <- assignment* cascadeExpression 
     *  assignment            <- variable assignmentToken
     *  assignmentToken       <- ':=' whitespace
     *  cascadeExpression     <- keywordExpression cascadeMessage*
     *  cascadeMessage        <- ';' whitespace message
     *  message               <- keywordMessage / binarymessage / unaryMessage
     *  keywordExpression     <- binaryExpression keywordMessage?
     *  keywordMessage        <- keywordToken binaryExpression+
     *  binaryMessage         <- binaryToken unaryExpression
     *  unaryMessage          <- unaryToken
     *  binaryExpression      <- unaryExpression binaryMessage*
     *  unaryExpression       <- primary unaryMessage*
     *  primary               <- literal / variable / block / parens / array 
     *  variable              <- identifierToken
     *  parens                <- '(' whitespace expression ')' whitespace
     *  array                 <- '{' whitespace ( expression ( periodToken expression )* periodToken? ) '}' whitespace
     *  block                 <- '[' whitespace blockBody ']' whitespace
     *  blockBody             <- blockArguments sequence
     *  blockArguments        <- blockArgumentsWith / blockArgumentsWithout
     *  blockArgumentsWith    <- blockArgument+ (( '|' whitespace ) / ( ']' whitespace ) )
     *  blockArgumentsWithout <- {epsilon}
     *  blockArgument         <- ':' whitespace variable
     *  sequence              <- temporaries periodToken* statements
     *  literal               <- numberLiteral / stringLiteral / charLiteral / arrayLiteral / byteLiteral / symbolLiteral / nilLiteral / trueLiteral / falseLiteral
     *  numberLiteral         <- numberToken
     *  numberToken           <- number whitespace
     *  stringLiteral         <- stringToken
     *  stringToken           <- string whitespace
     *  string                <- ['] ( [']['] / [^']* ) [']
     *  charLiteral           <- charToken
     *  charToken             <- char whitespace
     *  char                  <- '$' .
     *  byteLiteral           <- '#[' numberLiteral* ']'
     *  trueLiteral           <- trueToken
     *  trueToken             <- 'true' whitespace !{word}
     *  falseLiteral          <- falseToken
     *  falseToken            <- 'false' whitespace !{word}
     *  nilLiteral            <- nilToken
     *  nilToken              <- 'nil' whitespace !{word}
     *  symbolLiteral         <- ('#' whitespace)+ symbol whitespace
     *  symbol                <- unary / binary / multiword / string
     *  arrayLiteral          <- '#(' whitespace arrayItem* ')'
     *  arrayItem             <- literal / symbolLiteralArray / arrayLiteralArray / byteLiteralArray 
     *  byteLiteralArray      <- '[' whitespace numberLiteral* ']'
     *  symbolLiteralArray    <- symbol whitespace
     *  arrayLiteralArray     <- '(' whitespace arrayItem* ')'
     *  periodToken           <- period whitespace
     *  period                <- '.'
     *  unaryToken            <- unary whitespace
     *  unary                 <- identifier !':' 
     *  binaryToken           <- binary whitespace
     *  binary                <- [!%&*+,-/<=>?@\\|~]+
     *  keywordToken          <- keyword whitespace
     *  keyword               <- identifier ':'
     *  multiword             <- keyword+
     *  identifierToken       <- identifer whitespace
     *  identifier            <- [a-zA-Z_] [a-zA-Z0-9_]*
     */
  }
  
  // the original implementation uses a handwritten parser to
  // efficiently consume whitespace and comments 
  void other() {
    def("whitespace", whitespace() .or(ref("comment")) );
    def("comment", of('"') .seq(any().starLazy(of('"'))) .seq(of('"')) );
  }

  // the original implementation uses the hand written number 
  // parser of the system, this is the spec of the ANSI standard
  void number() {
    def("number", of('-').optional() .seq(ref("positiveNumber")) );
    def("positiveNumber", ref("scaledDecimal") .or(ref("float")) .or(ref("integer")) );
    def("integer", ref("radixInteger") .or(ref("decimalInteger")) );
    def("decimalInteger", ref("digits") );
    def("digits", digit().plus() );
    def("radixInteger", ref("radixSpecifier") .seq(of('r')) .seq(ref("radixDigits")) );
    def("radixSpecifier", ref("digits") );
    def("radixDigits", pattern("0-9A-Z").plus() );
    def("float", ref("mantissa") .seq(ref("exponentLetter") .seq(ref("exponent")) .optional()) );
    def("mantissa", ref("digits") .seq(of('.')) .seq(ref("digits")) );
    def("exponent", of('-') .seq(ref("decimalInteger")) );
    def("exponentLetter", pattern("edq") );
    def("scaledDecimal", ref("scaledMantissa") .seq(of('s')) .seq(ref("fractionalDigits").optional()) );
    def("scaledMantissa", ref("decimalInteger") .or(ref("mantissa")) );
    def("fractionalDigits", ref("decimalInteger") );
  }

  void smalltalk() {
    def("start", ref("startMethod") );

    def("startMethod", ref("method").end() );
    def("method", ref("methodDeclaration") .seq(ref("methodSequence")) );
    def("methodDeclaration", ref("keywordMethod") .or(ref("unaryMethod")) .or(ref("binaryMethod")) );
    def("methodSequence", ref("periodToken").star() .seq(ref("pragmas")) .seq(ref("periodToken").star()) .seq(ref("temporaries")) .seq(ref("periodToken").star()) .seq(ref("pragmas")) .seq(ref("periodToken").star()) .seq(ref("statements")) );

    def("keywordMethod", ref("keywordToken") .seq(ref("variable")).plus() );
    def("unaryMethod", ref("identifierToken") );
    def("binaryMethod", ref("binaryToken") .seq(ref("variable")) );
    
    def("pragmas", ref("pragma").star() );
    def("pragma", token("<") .seq(ref("pragmaMessage")) .seq(token(">")) );
    def("pragmaMessage", ref("keywordPragma") .or(ref("unaryPragma")) .or(ref("binaryPragma")) );
    def("keywordPragma", ref("keywordToken") .seq(ref("arrayItem")).plus() );
    def("unaryPragma", ref("identifierToken") );
    def("binaryPragma", ref("binaryToken") .seq(ref("arrayItem")) );
    
    def("temporaries", token("|") .seq(ref("variable").star()) .seq(token("|")) .optional() );
    def("statements", ref("expression").seq( ref("periodToken").plus().seq(ref("statements")) .or(ref("periodToken").star()) )
                      .or(ref("return").seq(ref("periodToken").star()))
                      .or(ref("periodToken").star()) );

    def("return", token("^") .seq(ref("expression")) );   
    def("expression", ref("assignment").star() .seq(ref("cascadeExpression")) );
    def("assignment", ref("variable") .seq(ref("assignmentToken")) );
    def("assignmentToken", token(":=") );

    def("cascadeExpression", ref("keywordExpression") .seq(ref("cascadeMessage").star()) );
    def("cascadeMessage", token(";") .seq(ref("message")) );
    def("message", ref("keywordMessage") .or(ref("binaryMessage")) .or(ref("unaryMessage")) );
 
    def("keywordExpression", ref("binaryExpression") .seq(ref("keywordMessage").optional()) );

    def("keywordMessage", ref("keywordToken") .seq(ref("binaryExpression")).plus() );
    def("binaryMessage", ref("binaryToken") .seq(ref("unaryExpression")) );
    def("unaryMessage", ref("unaryToken") );
    
    def("binaryExpression", ref("unaryExpression") .seq(ref("binaryMessage").star()) );
    def("unaryExpression", ref("primary") .seq(ref("unaryMessage").star()) );
   
    def("primary", ref("literal") .or(ref("variable")) .or(ref("block")) .or(ref("parens")) .or(ref("array")) );
    
    def("variable", ref("identifierToken") );
    def("parens", token("(") .seq(ref("expression")) .seq(token(")")) );

    def("array", token("{") .seq( ref("expression").separatedBy(ref("periodToken")) .seq( ref("periodToken").optional() ).optional() ) .seq(token("}")) );
                                                                                          // TODO: review 2x optional()
    def("block", token("[") .seq(ref("blockBody")) .seq(token("]")) );
    def("blockBody", ref("blockArguments") .seq(ref("sequence")) );
    def("blockArguments", ref("blockArgumentsWith") .or(ref("blockArgumentsWithout")) );
    def("blockArgumentsWith", ref("blockArgument").plus() .seq( token("|").or( token("]").and() ) ) ); // TODO: review and()
    def("blockArgumentsWithout", new EpsilonParser() ); // TODO: review epsilon() 
    def("blockArgument", token(":") .seq(ref("variable")) );
    def("sequence", ref("temporaries") .seq(ref("periodToken").star()) .seq(ref("statements")) );
    
    def("literal", ref("numberLiteral") .or(ref("stringLiteral")) .or(ref("charLiteral")) .or(ref("arrayLiteral")) .or(ref("byteLiteral")) .or(ref("symbolLiteral")) .or(ref("nilLiteral")) .or(ref("trueLiteral")) .or(ref("falseLiteral")) );

    def("numberLiteral", ref("numberToken") );
    def("numberToken", token(ref("number")) );

    def("stringLiteral", ref("stringToken") );
    def("stringToken", token(ref("string")) );
    def("string", of('\'') .seq(of("''").or(pattern("^'")).star()) .seq(of('\'')) ); // TODO: review quote escape
    
    def("charLiteral", ref("charToken") );
    def("charToken", token(ref("char")) );
    def("char", of('$').seq(any()) );
    
    def("byteLiteral", token("#[") .seq(ref("numberLiteral").star()) .seq(token("]")) );
    
    def("trueLiteral", ref("trueToken") );
    def("trueToken", token("true") .seq(word().not()) ); // TODO: review word()
    
    def("falseLiteral", ref("falseToken") );
    def("falseToken", token("false") .seq(word().not()) );
    
    def("nilLiteral", ref("nilToken") );
    def("nilToken", token("nil") .seq(word().not()) );

    def("symbolLiteral", token("#").plus() .seq(token(ref("symbol"))) );
    def("symbol", ref("unary") .or(ref("binary")) .or(ref("multiword")) .or(ref("string")) );
    
    def("arrayLiteral", token("#(") .seq(ref("arrayItem").star()) .seq(token(")")) );
    def("arrayItem", ref("literal") .or(ref("symbolLiteralArray")) .or(ref("arrayLiteralArray")) .or(ref("byteLiteralArray")) );
    
    def("byteLiteralArray", token("[") .seq(ref("numberLiteral").star()) .seq(token("]")) );
    def("symbolLiteralArray", token(ref("symbol")) );
    def("arrayLiteralArray", token("(") .seq(ref("arrayItem").star()) .seq(token(")")) );
       
    def("periodToken", token(ref("period")) );
    def("period", of('.') );
    def("unaryToken", token(ref("unary")) );
    def("unary", ref("identifier") .seq(of(':').not()) ); // TODO: review not()
    def("binaryToken", token(ref("binary")) );
    def("binary", pattern("!%&*+,-/<=>?@\\|~").plus() );
    def("keywordToken", token(ref("keyword")) );
    def("keyword", ref("identifier") .seq(of(':')) );
    def("multiword", ref("keyword").plus() );    
    def("identifierToken", token(ref("identifier")) ); 
    def("identifier", pattern("a-zA-Z_") .seq(pattern("a-zA-Z0-9_").star()) );
  }

  PetitParser token(Object input) {
    return switch(input) {
      case PetitParser p -> trim(p);
      case Character c -> trim(of(c));
      case String s -> trim(of(s));
      default -> throw new IllegalStateException("Object not parsable: " + input);
    };
  }
  
  PetitParser trim(PetitParser parser) {
    return parser.token().trim(ref("whitespace"));
  }

}
 
