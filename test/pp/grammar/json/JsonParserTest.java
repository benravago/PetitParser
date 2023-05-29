package pp.grammar.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import petit.parser.PetitParser;
import petit.parser.context.Result;

import java.util.List;
import java.util.Map;

/**
 * Tests {@link JsonParser}.
 */
class JsonParserTest {

  final static PetitParser parser = new JsonParser();
  final static double EPSILON = 1.0e-6;

  // arrays

  @Test
  void testEmptyArray() {
    List<String> result = assertValid("[]").get();
    assertTrue(result.isEmpty());
  }

  @Test
  void testSmallArray() {
    List<String> result = assertValid("[\"a\"]").get();
    assertEquals(1, result.size());
    assertEquals("a", result.get(0));
  }

  @Test
  void testBigArray() {
    List<String> result = assertValid(" [ \"a\" , \"b\" ] ").get();
    assertEquals(2, result.size());
    assertEquals("a", result.get(0));
    assertEquals("b", result.get(1));
  }

  @Test
  void testNestedArray() {
    List<List<String>> result = assertValid("[[\"a\"]]").get();
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).size());
    assertEquals("a", result.get(0).get(0));
  }

  // objects

  @Test
  void testEmptyObject() {
    Map<String, Long> result = assertValid("{}").get();
    assertTrue(result.isEmpty());
  }

  @Test
  void testSmallObject() {
    Map<String, Long> result = assertValid("{\"a\":1}").get();
    assertEquals(1, result.size());
    assertEquals(Long.valueOf(1), result.get("a"));
  }

  @Test
  void testBigObject() {
    Map<String, Long> result = assertValid(" { \"a\" : 1 , \"b\" : 2 } ").get();
    assertEquals(2, result.size());
    assertEquals(Long.valueOf(1), result.get("a"));
    assertEquals(Long.valueOf(2), result.get("b"));
  }

  @Test
  void testNestedObject() {
    Map<String, Map<String, Long>> result = assertValid("{\"object\":{\"1\": " + "2}}").get();
    assertEquals(1, result.size());
    assertEquals(1, result.get("object").size());
    assertEquals(Long.valueOf(2), result.get("object").get("1"));
  }

  // literals

  @Test
  void testTrue() {
    Boolean result = assertValid("true").get();
    assertTrue(result);
  }

  @Test
  void testFalse() {
    Boolean result = assertValid("false").get();
    assertFalse(result);
  }

  @Test
  void testNull() {
    Object result = assertValid("null").get();
    assertNull(result);
  }

  @Test
  void testFloat() {
    assertEquals(0.0, assertValid("0.0").get(), EPSILON);
    assertEquals(0.12, assertValid("0.12").get(), EPSILON);
    assertEquals(-0.12, assertValid("-0.12").get(), EPSILON);
    assertEquals(12.34, assertValid("12.34").get(), EPSILON);
    assertEquals(-12.34, assertValid("-12.34").get(), EPSILON);
    assertEquals(1.2, assertValid("1.2e0").get(), EPSILON);
    assertEquals(1.2e-1, assertValid("1.2e-1").get(), EPSILON);
    assertEquals(1.2e-1, assertValid("1.2E-1").get(), EPSILON);
  }

  @Test
  void testInteger() {
    assertEquals(0L, (long) assertValid("0").get());
    assertEquals(1L, (long) assertValid("1").get());
    assertEquals(-1L, (long) assertValid("-1").get());
    assertEquals(12L, (long) assertValid("12").get());
    assertEquals(-12L, (long) assertValid("-12").get());
    assertEquals(100L, (long) assertValid("1e2").get());
    assertEquals(100L, (long) assertValid("1e+2").get());
  }

  @Test
  void testString() {
    assertEquals("", assertValid("\"\"").get());
    assertEquals("foo", assertValid("\"foo\"").get());
    assertEquals("foo bar", assertValid("\"foo bar\"").get());
  }

  @Test
  void testStringEscaped() {
    assertEquals("\"", assertValid("\"\\\"\"").get());
    assertEquals("\\", assertValid("\"\\\\\"").get());
    assertEquals("/", assertValid("\"\\/\"").get());
    assertEquals("\b", assertValid("\"\\b\"").get());
    assertEquals("\f", assertValid("\"\\f\"").get());
    assertEquals("\n", assertValid("\"\\n\"").get());
    assertEquals("\r", assertValid("\"\\r\"").get());
    assertEquals("\t", assertValid("\"\\t\"").get());
    assertEquals("\u20Ac", assertValid("\"\\u20Ac\"").get());
  }

  @Test
  void testExplorerEvent() {
    Result result = assertValid("{\"recordset\": null, \"type\": \"change\", \"fromElement\": null, "
        + "\"toElement\": null, " + "\"altLeft\": false, \"keyCode\": 0, \"repeat\": false, " + "\"reason\": 0, "
        + "\"behaviorCookie\": 0, \"contentOverflow\": false, " + "\"behaviorPart\": 0, "
        + "\"dataTransfer\": null, \"ctrlKey\": false, \"shiftLeft\": " + "false, \"dataFld\": \"\","
        + " \"qualifier\": \"\", \"wheelDelta\": 0, \"bookmarks\": null, " + "\"button\": 0, "
        + "\"srcFilter\": null, \"nextPage\": \"\", \"cancelBubble\": " + "false, \"x\": 89, \"y\": "
        + "502, \"screenX\": 231, \"screenY\": 1694, \"srcUrn\": \"\", " + "\"boundElements\": "
        + "{\"length\": 0}, \"clientX\": 89, \"clientY\": 502, " + "\"propertyName\": \"\", "
        + "\"shiftKey\": false, \"ctrlLeft\": false, \"offsetX\": 25, " + "\"offsetY\": 2, " + "\"altKey\": false}");
    assertTrue(result.isSuccess());
  }

  @Test
  void testFirefoxEvent() {
    Result result = assertValid("{\"type\": \"change\", \"eventPhase\": 2, \"bubbles\": true, "
        + "\"cancelable\": true, " + "\"timeStamp\": 0, \"CAPTURING_PHASE\": 1, \"AT_TARGET\": 2, "
        + "\"BUBBLING_PHASE\": 3, " + "\"isTrusted\": true, \"MOUSEDOWN\": 1, \"MOUSEUP\": 2, " + "\"MOUSEOVER\": 4, "
        + "\"MOUSEOUT\": 8, \"MOUSEMOVE\": 16, \"MOUSEDRAG\": 32, " + "\"CLICK\": 64, \"DBLCLICK\": "
        + "128, \"KEYDOWN\": 256, \"KEYUP\": 512, \"KEYPRESS\": 1024, " + "\"DRAGDROP\": 2048, "
        + "\"FOCUS\": 4096, \"BLUR\": 8192, \"SELECT\": 16384, \"CHANGE\": " + "32768, \"RESET\": "
        + "65536, \"SUBMIT\": 131072, \"SCROLL\": 262144, \"LOAD\": 524288," + " \"UNLOAD\": "
        + "1048576, \"XFER_DONE\": 2097152, \"ABORT\": 4194304, \"ERROR\": " + "8388608, \"LOCATE\":"
        + " 16777216, \"MOVE\": 33554432, \"RESIZE\": 67108864, " + "\"FORWARD\": 134217728, "
        + "\"HELP\": 268435456, \"BACK\": 536870912, \"TEXT\": 1073741824, " + "\"ALT_MASK\": 1, "
        + "\"CONTROL_MASK\": 2, \"SHIFT_MASK\": 4, \"META_MASK\": 8}");
    assertTrue(result.isSuccess());
  }

  @Test
  void testWebkitEvent() {
    Result result = assertValid("{\"returnValue\": true, \"timeStamp\": 1226697417289, "
        + "\"eventPhase\": 2, \"type\": " + "\"change\", \"cancelable\": false, \"bubbles\": true, "
        + "\"cancelBubble\": false, " + "\"MOUSEOUT\": 8, \"FOCUS\": 4096, \"CHANGE\": 32768, " + "\"MOUSEMOVE\": 16, "
        + "\"AT_TARGET\": 2, \"SELECT\": 16384, \"BLUR\": 8192, \"KEYUP\": " + "512, \"MOUSEDOWN\": "
        + "1, \"MOUSEDRAG\": 32, \"BUBBLING_PHASE\": 3, \"MOUSEUP\": 2, " + "\"CAPTURING_PHASE\": 1,"
        + " \"MOUSEOVER\": 4, \"CLICK\": 64, \"DBLCLICK\": 128, " + "\"KEYDOWN\": 256, \"KEYPRESS\":"
        + " 1024, \"DRAGDROP\": 2048}");
    assertTrue(result.isSuccess());
  }

  @Test
  void testInvalidArray() {
    assertInvalid("[");
    assertInvalid("[1");
    assertInvalid("[1,");
    assertInvalid("[1,]");
    assertInvalid("[1 2]");
    assertInvalid("[]]");
  }

  @Test
  void testInvalidObject() {
    assertInvalid("{");
    assertInvalid("{\"a\"");
    assertInvalid("{\"a\":");
    assertInvalid("{\"a\":\"b\"");
    assertInvalid("{\"a\":\"b\",");
    assertInvalid("{\"a\"}");
    assertInvalid("{\"a\":}");
    assertInvalid("{\"a\":\"b\",}");
    assertInvalid("{}}");
  }

  @Test
  void testInvalidString() {
    assertInvalid("\"");
    assertInvalid("\"a");
    assertInvalid("\"\\\"");
    assertInvalid("\"\\a\"");
    assertInvalid("\"\\u\"");
    assertInvalid("\"\\u1\"");
    assertInvalid("\"\\u12\"");
    assertInvalid("\"\\u123\"");
    assertInvalid("\"\\u123x\"");
  }

  @Test
  void testInvalidNumber() {
    assertInvalid("00");
    assertInvalid("01");
    assertInvalid("00.1");
  }

  @Test
  void testInvalidTrue() {
    assertInvalid("tr");
    assertInvalid("trace");
    assertInvalid("truest");
  }

  @Test
  void testInvalidFalse() {
    assertInvalid("fa");
    assertInvalid("falsely");
    assertInvalid("fabulous");
  }

  @Test
  void testInvalidNull() {
    assertInvalid("nu");
    assertInvalid("nuclear");
    assertInvalid("nullified");
  }

  Result assertValid(String input) {
    var result = parser.parse(input);
    assertTrue(result.isSuccess());
    return result;
  }

  Result assertInvalid(String input) {
    var result = parser.parse(input);
    assertTrue(result.isFailure());
    return result;
  }

}
