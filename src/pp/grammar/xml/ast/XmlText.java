package pp.grammar.xml.ast;

import pp.grammar.xml.XmlCharacterParser;

/**
 * XML text node.
 */
public class XmlText extends XmlData {

  public XmlText(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlCharacterParser.encodeXmlText(getData()));
  }
}
