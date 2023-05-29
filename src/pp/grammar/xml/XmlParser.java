package pp.grammar.xml;

import petit.parser.tools.GrammarParser;

/**
 * XmlParser Builder
 */
public class XmlParser extends GrammarParser {

  public XmlParser() {
    super(new XmlDefinition<>(new XmlBuilder()));
  }
}
