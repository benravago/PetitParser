package pp.grammar.smalltalk;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link SmalltalkDefinition}.
 */
class SmalltalkDefinitionTest {

  final SmalltalkDefinition smalltalk = new SmalltalkDefinition();

  <T> T validate(String source, String production) {
    var parser = smalltalk.build(production).end();
    var result = parser.parse(source);
    return result.get();
  }

  @Test
  void testArray1() {
    validate("{}", "array");
  }

  @Test
  void testArray2() {
    validate("{self foo}", "array");
  }

  @Test
  void testArray3() {
    validate("{self foo. self bar}", "array");
  }

  @Test
  void testArray4() {
    validate("{self foo. self bar.}", "array");
  }

  @Test
  void testAssignment1() {
    validate("1", "expression");
  }

  @Test
  void testAssignment2() {
    validate("a := 1", "expression");
  }

  @Test
  void testAssignment3() {
    validate("a := b := 1", "expression");
  }

  @Test
  void testAssignment6() {
    validate("a := (b := c)", "expression");
  }

  @Test
  void testComment1() {
    validate("1\"one\"+2", "expression");
  }

  @Test
  void testComment2() {
    validate("1 \"one\" +2", "expression");
  }

  @Test
  void testComment3() {
    validate("1\"one\"+\"two\"2", "expression");
  }

  @Test
  void testComment4() {
    validate("1\"one\"\"two\"+2", "expression");
  }

  @Test
  void testComment5() {
    validate("1\"one\" \"two\"+2", "expression");
  }

  @Test
  void testMethod1() {
    validate("negated ^ 0 - self", "method");
  }

  @Test
  void testMethod2() {
    validate("   negated ^ 0 - self", "method");
  }

  @Test
  void testMethod3() {
    validate(" negated ^ 0 - self  ", "method");
  }

  @Test
  void testSequence1() {
    validate("| a | 1 . 2", "sequence");
  }

  @Test
  void testStatements1() {
    validate("1", "sequence");
  }

  @Test
  void testStatements2() {
    validate("1 . 2", "sequence");
  }

  @Test
  void testStatements3() {
    validate("1 . 2 . 3", "sequence");
  }

  @Test
  void testStatements4() {
    validate("1 . 2 . 3 .", "sequence");
  }

  @Test
  void testStatements5() {
    validate("1 . . 2", "sequence");
  }

  @Test
  void testStatements6() {
    validate("1. 2", "sequence");
  }

  @Test
  void testStatements7() {
    validate(". 1", "sequence");
  }

  @Test
  void testStatements8() {
    validate(".1", "sequence");
  }

  @Test
  void testTemporaries1() {
    validate("| a |", "sequence");
  }

  @Test
  void testTemporaries2() {
    validate("| a b |", "sequence");
  }

  @Test
  void testTemporaries3() {
    validate("| a b c |", "sequence");
  }

  @Test
  void testVariable1() {
    validate("trueBinding", "primary");
  }

  @Test
  void testVariable2() {
    validate("falseBinding", "primary");
  }

  @Test
  void testVariable3() {
    validate("nilly", "primary");
  }

  @Test
  void testVariable4() {
    validate("selfish", "primary");
  }

  @Test
  void testVariable5() {
    validate("supernanny", "primary");
  }

  @Test
  void testVariable6() {
    validate("super_nanny", "primary");
  }

  @Test
  void testVariable7() {
    validate("__gen_var_123__", "primary");
  }

  @Test
  void testArgumentsBlock1() {
    validate("[ :a | ]", "block");
  }

  @Test
  void testArgumentsBlock2() {
    validate("[ :a :b | ]", "block");
  }

  @Test
  void testArgumentsBlock3() {
    validate("[ :a :b :c | ]", "block");
  }

  @Test
  void testComplexBlock1() {
    validate("[ :a | | b | c ]", "block");
  }

  @Test
  void testComplexBlock2() {
    validate("[:a||b|c]", "block");
  }

  @Test
  void testSimpleBlock1() {
    validate("[ ]", "block");
  }

  @Test
  void testSimpleBlock2() {
    validate("[ nil ]", "block");
  }

  @Test
  void testSimpleBlock3() {
    validate("[ :a ]", "block");
  }

  @Test
  void testStatementBlock1() {
    validate("[ nil ]", "block");
  }

  @Test
  void testStatementBlock2() {
    validate("[ | a | nil ]", "block");
  }

  @Test
  void testStatementBlock3() {
    validate("[ | a b | nil ]", "block");
  }

  @Test
  void testArrayLiteral1() {
    validate("#()", "arrayLiteral");
  }

  @Test
  void testArrayLiteral10() {
    validate("#((1 2) #(1 2 3))", "arrayLiteral");
  }

  @Test
  void testArrayLiteral11() {
    validate("#([1 2] #[1 2 3])", "arrayLiteral");
  }

  @Test
  void testArrayLiteral2() {
    validate("#(1)", "arrayLiteral");
  }

  @Test
  void testArrayLiteral3() {
    validate("#(1 2)", "arrayLiteral");
  }

  @Test
  void testArrayLiteral4() {
    validate("#(true false nil)", "arrayLiteral");
  }

  @Test
  void testArrayLiteral5() {
    validate("#($a)", "arrayLiteral");
  }

  @Test
  void testArrayLiteral6() {
    validate("#(1.2)", "arrayLiteral");
  }

  @Test
  void testArrayLiteral7() {
    validate("#(size #at: at:put: #'==')", "arrayLiteral");
  }

  @Test
  void testArrayLiteral8() {
    validate("#('baz')", "arrayLiteral");
  }

  @Test
  void testArrayLiteral9() {
    validate("#((1) 2)", "arrayLiteral");
  }

  @Test
  void testByteLiteral1() {
    validate("#[]", "byteLiteral");
  }

  @Test
  void testByteLiteral2() {
    validate("#[0]", "byteLiteral");
  }

  @Test
  void testByteLiteral3() {
    validate("#[255]", "byteLiteral");
  }

  @Test
  void testByteLiteral4() {
    validate("#[ 1 2 ]", "byteLiteral");
  }

  @Test
  void testByteLiteral5() {
    validate("#[ 2r1010 8r77 16rFF ]", "byteLiteral");
  }

  @Test
  void testCharLiteral1() {
    validate("$a", "charLiteral");
  }

  @Test
  void testCharLiteral2() {
    validate("$ ", "charLiteral");
  }

  @Test
  void testCharLiteral3() {
    validate("$$", "charLiteral");
  }

  @Test
  void testNumberLiteral1() {
    validate("0", "numberLiteral");
  }

  @Test
  void testNumberLiteral10() {
    validate("10r10", "numberLiteral");
  }

  @Test
  void testNumberLiteral11() {
    validate("8r777", "numberLiteral");
  }

  @Test
  void testNumberLiteral12() {
    validate("16rAF", "numberLiteral");
  }

  @Test
  void testNumberLiteral2() {
    validate("0.1", "numberLiteral");
  }

  @Test
  void testNumberLiteral3() {
    validate("123", "numberLiteral");
  }

  @Test
  void testNumberLiteral4() {
    validate("123.456", "numberLiteral");
  }

  @Test
  void testNumberLiteral5() {
    validate("-0", "numberLiteral");
  }

  @Test
  void testNumberLiteral6() {
    validate("-0.1", "numberLiteral");
  }

  @Test
  void testNumberLiteral7() {
    validate("-123", "numberLiteral");
  }

  @Test
  void testNumberLiteral8() {
    validate("-123", "numberLiteral");
  }

  @Test
  void testNumberLiteral9() {
    validate("-123.456", "numberLiteral");
  }

  @Test
  void testSpecialLiteral1() {
    validate("true", "trueLiteral");
  }

  @Test
  void testSpecialLiteral2() {
    validate("false", "falseLiteral");
  }

  @Test
  void testSpecialLiteral3() {
    validate("nil", "nilLiteral");
  }

  @Test
  void testStringLiteral1() {
    validate("''", "stringLiteral");
  }

  @Test
  void testStringLiteral2() {
    validate("'ab'", "stringLiteral");
  }

  @Test
  void testStringLiteral3() {
    validate("'ab''cd'", "stringLiteral");
  }

  @Test
  void testSymbolLiteral1() {
    validate("#foo", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral2() {
    validate("#+", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral3() {
    validate("#key:", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral4() {
    validate("#key:value:", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral5() {
    validate("#'testing-result'", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral6() {
    validate("#__gen__binding", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral7() {
    validate("# fucker", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral8() {
    validate("##fucker", "symbolLiteral");
  }

  @Test
  void testSymbolLiteral9() {
    validate("## fucker", "symbolLiteral");
  }

  @Test
  void testBinaryExpression1() {
    validate("1 + 2", "expression");
  }

  @Test
  void testBinaryExpression2() {
    validate("1 + 2 + 3", "expression");
  }

  @Test
  void testBinaryExpression3() {
    validate("1 // 2", "expression");
  }

  @Test
  void testBinaryExpression4() {
    validate("1 -- 2", "expression");
  }

  @Test
  void testBinaryExpression5() {
    validate("1 ==> 2", "expression");
  }

  @Test
  void testBinaryMethod1() {
    validate("+ a", "method");
  }

  @Test
  void testBinaryMethod2() {
    validate("+ a | b |", "method");
  }

  @Test
  void testBinaryMethod3() {
    validate("+ a b", "method");
  }

  @Test
  void testBinaryMethod4() {
    validate("+ a | b | c", "method");
  }

  @Test
  void testBinaryMethod5() {
    validate("-- a", "method");
  }

  @Test
  void testCascadeExpression1() {
    validate("1 abs; negated", "expression");
  }

  @Test
  void testCascadeExpression2() {
    validate("1 abs negated; raisedTo: 12; negated", "expression");
  }

  @Test
  void testCascadeExpression3() {
    validate("1 + 2; - 3", "expression");
  }

  @Test
  void testKeywordExpression1() {
    validate("1 to: 2", "expression");
  }

  @Test
  void testKeywordExpression2() {
    validate("1 to: 2 by: 3", "expression");
  }

  @Test
  void testKeywordExpression3() {
    validate("1 to: 2 by: 3 do: 4", "expression");
  }

  @Test
  void testKeywordMethod1() {
    validate("to: a", "method");
  }

  @Test
  void testKeywordMethod2() {
    validate("to: a do: b | c |", "method");
  }

  @Test
  void testKeywordMethod3() {
    validate("to: a do: b by: c d", "method");
  }

  @Test
  void testKeywordMethod4() {
    validate("to: a do: b by: c | d | e", "method");
  }

  @Test
  void testUnaryExpression1() {
    validate("1 abs", "expression");
  }

  @Test
  void testUnaryExpression2() {
    validate("1 abs negated", "expression");
  }

  @Test
  void testUnaryMethod1() {
    validate("abs", "method");
  }

  @Test
  void testUnaryMethod2() {
    validate("abs | a |", "method");
  }

  @Test
  void testUnaryMethod3() {
    validate("abs a", "method");
  }

  @Test
  void testUnaryMethod4() {
    validate("abs | a | b", "method");
  }

  @Test
  void testUnaryMethod5() {
    validate("abs | a |", "method");
  }

  @Test
  void testPragma1() {
    validate("method <foo>", "method");
  }

  @Test
  void testPragma10() {
    validate("method <foo: bar>", "method");
  }

  @Test
  void testPragma11() {
    validate("method <foo: true>", "method");
  }

  @Test
  void testPragma12() {
    validate("method <foo: false>", "method");
  }

  @Test
  void testPragma13() {
    validate("method <foo: nil>", "method");
  }

  @Test
  void testPragma14() {
    validate("method <foo: ()>", "method");
  }

  @Test
  void testPragma15() {
    validate("method <foo: #()>", "method");
  }

  @Test
  void testPragma16() {
    validate("method < + 1 >", "method");
  }

  @Test
  void testPragma2() {
    validate("method <foo> <bar>", "method");
  }

  @Test
  void testPragma3() {
    validate("method | a | <foo>", "method");
  }

  @Test
  void testPragma4() {
    validate("method <foo> | a |", "method");
  }

  @Test
  void testPragma5() {
    validate("method <foo> | a | <bar>", "method");
  }

  @Test
  void testPragma6() {
    validate("method <foo: 1>", "method");
  }

  @Test
  void testPragma7() {
    validate("method <foo: 1.2>", "method");
  }

  @Test
  void testPragma8() {
    validate("method <foo: 'bar'>", "method");
  }

  @Test
  void testPragma9() {
    validate("method <foo: #'bar'>", "method");
  }

}
