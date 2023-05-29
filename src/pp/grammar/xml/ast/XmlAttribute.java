package pp.grammar.xml.ast;

import java.util.Objects;

import pp.grammar.xml.XmlCharacterParser;
import pp.grammar.xml.XmlDefinition;

/**
 * XML attribute node.
 */
public class XmlAttribute extends XmlNode {

  private final XmlName name;
  private final String value;

  public XmlAttribute(XmlName name, String value) {
    this.name = name;
    this.value = value;
  }

  public XmlName getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  @Override
  public void writeTo(StringBuilder buffer) {
    name.writeTo(buffer);
    buffer.append(XmlDefinition.EQUALS);
    buffer.append(XmlDefinition.DOUBLE_QUOTE);
    buffer.append(XmlCharacterParser.encodeXmlAttributeValue(value));
    buffer.append(XmlDefinition.DOUBLE_QUOTE);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    XmlAttribute other = (XmlAttribute) obj;
    return Objects.equals(name, other.name) &&
        Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }
}
