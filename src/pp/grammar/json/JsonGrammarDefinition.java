package pp.grammar.json;

import petit.parser.PetitParser;
import petit.parser.primitive.CharacterParser;
import petit.parser.tools.GrammarDefinition;

import static petit.parser.primitive.CharacterParser.anyOf;
import static petit.parser.primitive.CharacterParser.digit;
import static petit.parser.primitive.CharacterParser.of;
import static petit.parser.primitive.StringParser.of;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * JSON grammar definition.
 */
public class JsonGrammarDefinition extends GrammarDefinition {

  protected static String listToString(Collection<Character> characters) {
    var builder = new StringBuilder(characters.size());
    characters.forEach(builder::append);
    return builder.toString();
  }

  protected static final Map<Character, Character> ESCAPE_TABLE = Map.of(
    '\\','\\',  '/','/',  '"','"',  'b','\b',  'f','\f',  'n','\n',  'r','\r',  't','\t'
  );
  
  protected static final Function<Character, Character> ESCAPE_TABLE_FUNCTION = ESCAPE_TABLE::get;
  protected static final String ESCAPEE = listToString(ESCAPE_TABLE.keySet());

  protected static final PetitParser HEXADECIMAL = CharacterParser.pattern("0-9A-Fa-f");
  
  public JsonGrammarDefinition() {

    def("start", ref("value").end() );                                    // start <- value
                                                          
    def("value", ref("stringToken").                                      // value <- stringToken  
                 or(ref("numberToken")).                                  //          / numberToken
                 or(ref("trueToken")).                                    //          / trueToken  
                 or(ref("falseToken")).                                   //          / falseToken 
                 or(ref("nullToken")).                                    //          / nullToken  
                 or(ref("object")).                                       //          / object     
                 or(ref("array")) );                                      //          / array      


    def("stringToken", ref("stringPrimitive").                            // stringToken <- stringPrimitive
                         flatten("Expected string").
                         trim() );

    def("numberToken", ref("numberPrimitive").                            // numberToken <- numberPrimitive
                         flatten("Expected number").
                         trim() );

    def("trueToken", of("true").                                          // trueToken <- `true`
                       flatten("Expected 'true'").
                       trim() );

    def("falseToken", of("false").                                        // falseToken <- `false`
                        flatten("Expected 'false'").
                        trim() );
    
    def("nullToken", of("null").                                          // nullToken <- `null`
                       flatten("Expected 'null'").
                       trim() );
    
    def("object", of('{').trim().                                         // object <- `{` members* `}`
                  seq(ref("members").optional()).
                  seq(of('}').trim()) );

    def("array", of('[').trim().                                          // array <- `[` elements* `}`
                 seq(ref("elements").optional()).
                 seq(of(']').trim()) );
    
    def("elements", ref("value").                                         // elements <- value ( `,` value )*
                      separatedBy(of(',').trim()) );
    
    def("members", ref("pair").                                           // members <- pair ( `,` pair )*
                     separatedBy(of(',').trim()) );
    
    def("pair", ref("stringToken").                                       // pair <- stringToken `:` value
                seq(of(':').trim()).
                seq(ref("value")) );
                    
    def("characterPrimitive", ref("characterEscape").                     // characterPrimitive <- characterEscape
                              or(ref("characterOctal")).                  //                       / characterOctal
                              or(ref("characterNormal")) );               //                       / characterNormal
     
    def("characterEscape", of('\\').                                      // characterEscape <- `\` ( `\` / `/` / `"` / `b` / `f` / `n` / `r` / `t` )
                           seq(anyOf(ESCAPEE)) );                       
                           
                           
    def("characterOctal", of("\\u").                                      // characterOctal <- `\` `u` [0-9A-Fa-f]{4}
                          seq(HEXADECIMAL.times(4).flatten()) );     
                                                             
    def("characterNormal", anyOf("\"\\").neg() );                         // characterNormal <- ! ( `\` | `"` )
                                                             
    def("numberPrimitive", of('-').optional().                            // numberPrimitive <- [-]? ( ([0] / [1-9]) [0-9]* ) ( [.] [0-9]+ )? ( [e] / [E] ) ( [-] / [+] ) [0-9]+
                           seq(of('0').or(digit().plus())).
                           seq(of('.').seq(digit().plus()).optional()).
                           seq(anyOf("eE").seq(anyOf("-+").optional()).     
                           seq(digit().plus()).optional()) );                                                       

    def("stringPrimitive", of('"').                                       // stringPrimitive <- `"` characterPrimitive* `"`
                           seq(ref("characterPrimitive").star()).
                           seq(of('"'))                     
    );                                                       
  }                                                          
}                                                            
      // https://pegn.dev/                                 

      // Number  <-- MINUS? Integer (DOT DIGIT+)? ('e' / 'E') sign DIGIT+ 
      // Integer  <- '0' / [1-9] DIGIT*
                
