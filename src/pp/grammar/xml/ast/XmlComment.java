package pp.grammar.xml.ast;

import pp.grammar.xml.XmlDefinition;

/**
 * An XML comment node.
 */
public class XmlComment extends XmlData {

  public XmlComment(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlDefinition.OPEN_COMMENT);
    buffer.append(getData());
    buffer.append(XmlDefinition.CLOSE_COMMENT);
  }
}
