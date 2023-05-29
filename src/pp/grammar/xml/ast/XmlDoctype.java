package pp.grammar.xml.ast;

import pp.grammar.xml.XmlDefinition;

/**
 * XML doctype node.
 */
public class XmlDoctype extends XmlData {

  public XmlDoctype(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlDefinition.OPEN_DOCTYPE);
    buffer.append(XmlDefinition.WHITESPACE);
    buffer.append(getData());
    buffer.append(XmlDefinition.CLOSE_DOCTYPE);
  }
}
