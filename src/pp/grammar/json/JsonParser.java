package pp.grammar.json;

import petit.parser.tools.GrammarParser;

/**
 * JSON parser.
 */
public class JsonParser extends GrammarParser {
  public JsonParser() {
    super(new JsonParserDefinition());
  }
}
