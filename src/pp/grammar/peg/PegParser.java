package pp.grammar.peg;

import petit.parser.tools.GrammarParser;

public class PegParser extends GrammarParser {

  public PegParser() {
    super(new ParsingExpressionGrammar() {
      // extend with actions() here
    });
  }
}
