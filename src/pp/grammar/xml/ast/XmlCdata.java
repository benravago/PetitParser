package pp.grammar.xml.ast;

import pp.grammar.xml.XmlDefinition;

/**
 * XML text node.
 */
public class XmlCdata extends XmlData {

  public XmlCdata(String data) {
    super(data);
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    buffer.append(XmlDefinition.OPEN_CDATA);
    buffer.append(getData());
    buffer.append(XmlDefinition.CLOSE_CDATA);
  }
}
