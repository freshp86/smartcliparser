/* Copyright 2011 Demetrios Papadopoulos

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package org.smartcliparser;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class FlagTest {

  @Test
  public void testConstructor() {
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 1, 5, null, false);
    assertEquals("getNumOfArgsMin", 1, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 5, flag.getNumOfArgsMax());
    assertTrue("hasName", flag.hasName("hello"));
    assertTrue("hasName", flag.hasName("world"));
    assertFalse("isSet", flag.isSet());
    assertTrue("args.isEmpty", flag.args.isEmpty());
    assertNull("pattern", flag.pattern);
  }


  @Test
  public void testHasName() {
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 1, 5, null, false);
    assertTrue("hasName", flag.hasName("hello"));
    assertTrue("hasName", flag.hasName("world"));
    assertFalse("hasName", flag.hasName("helloworld"));
    assertFalse("hasName", flag.hasName(""));
  }


  @Test
  public void testSetNumOfArgs() {
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 1, 5, null, false);

    flag.setNumOfArgs(0, 10);
    assertEquals("getNumOfArgsMin", 0, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 10, flag.getNumOfArgsMax());

    flag.setNumOfArgs(10, 0);
    assertEquals("getNumOfArgsMin", 0, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 10, flag.getNumOfArgsMax());

    flag.setNumOfArgs(4, Flag.UNLIMITED_NUM_OF_ARGS);
    assertEquals("getNumOfArgsMin", 4, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax",
        Flag.UNLIMITED_NUM_OF_ARGS, flag.getNumOfArgsMax());

    flag.setNumOfArgs(-10, 10);
    assertEquals("getNumOfArgsMin", 0, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 10, flag.getNumOfArgsMax());

    flag.setNumOfArgs(-10, -10);
    assertEquals("getNumOfArgsMin", 0, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 0, flag.getNumOfArgsMax());
  }


  @Test
  public void testIsValid() {
    // Test that an optional flag is valid even when not set.
    Flag flag1 = new Flag(
        new String[]{"hello", "world"}, false, 0, 5, null, false);
    assertTrue(flag1.isValid());
    assertEquals("errors.size", 0, flag1.getErrors().size());
    // Test that a required flag is not valid when it is not set.
    Flag flag2 = new Flag(
        new String[]{"hello", "world"}, true, 0, 5, null, false);
    assertFalse(flag2.isValid());
    assertEquals("errors.size", 1, flag2.getErrors().size());
  }


  @Test
  public void testConsume_MinArgViolation() {
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 2, 2, null, false);
    List<String> args = new ArrayList<String>(Arrays.asList(
          new String[]{"arg1"}));
    ListIterator<String> it = args.listIterator();
    flag.consume(args, it);
    assertFalse("isValid", flag.isValid());
    assertEquals("errors.size", 1, flag.getErrors().size());
  }


  @Test
  public void testConsume_NoPattern() {
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 1, 2, null, false);
    List<String> args = new ArrayList<String>(Arrays.asList(
        new String[]{"arg1", "arg2", "arg3"}));
    ListIterator<String> it = args.listIterator();
    flag.consume(args, it);
    // Test that the correct number of args is consumed.
    assertArrayEquals(new String[]{"arg1", "arg2"}, flag.args.toArray());
    // Test that the iterator has been left pointing at the correct position.
    assertTrue(it.hasNext());
    assertEquals("arg3", it.next());
    // Test that the flag parsing is valid.
    assertTrue("isValid", flag.isValid());
    assertEquals("errors.size", 0, flag.getErrors().size());
  }


  @Test
  public void testConsume_WithPattern() {
    Pattern pattern = Pattern.compile("^(abc|def)$");
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 2, 3, pattern, false);

    // Test that the flag parsing is valid when all args are following the
    // pattern.
    List<String> args = new ArrayList<String>(Arrays.asList(
          new String[]{"abc", "def"}));
    ListIterator<String> it = args.listIterator();
    flag.consume(args, it);
    assertTrue("isValid", flag.isValid());
    assertTrue("errors.isEmpty", flag.getErrors().isEmpty());

    // Test that an argument not following the pattern is detected.
    args = new ArrayList<String>(Arrays.asList(new String[]{"123"}));
    it = args.listIterator();
    flag.consume(args, it);
    assertFalse("isValid", flag.isValid());
    assertEquals("errors.size", 1, flag.getErrors().size());
    // TODO(dpapad): Assert the exact error type here, need to modify
    // ParsingError interface.
  }


  @Test
  public void testConsume_ForceConsume() {
    String[] argsArray = new String[]{"arg1", "arg2", "arg3"};
    Flag flag = new Flag(
        new String[]{"hello", "world"}, true, 1, 1, null, true);
    List<String> args = new ArrayList<String>(Arrays.asList(argsArray));
    ListIterator<String> it = args.listIterator();
    flag.consume(args, it);
    // Test that all arguments are consumed, regardless of the flag's specified
    // max number of arguments.
    assertArrayEquals(argsArray, flag.args.toArray());
    // Test that checking whether parsing was valid respects the flags
    // specifications.
    assertFalse("isValid", flag.isValid());
    assertEquals("errors.size", 1, flag.getErrors().size());
    // TODO(dpapad): Assert the exact error type here.
  }


  @Test
  public void testIsFlagLike() {
    assertFalse("isFlagLike", Flag.isFlagLike(""));
    assertFalse("isFlagLike", Flag.isFlagLike("--"));
    assertFalse("isFlagLike", Flag.isFlagLike("-"));
    assertFalse("isFlagLike", Flag.isFlagLike("-hello"));

    assertTrue("isFlagLike", Flag.isFlagLike("--hello"));
    assertTrue("isFlagLike", Flag.isFlagLike("--h"));
    assertTrue("isFlagLike", Flag.isFlagLike("-h"));
  }


  @Test
  public void testExtractName() {
    assertNull("extractName", Flag.extractName(""));
    assertNull("extractName", Flag.extractName("--"));
    assertNull("extractName", Flag.extractName("-"));
    assertNull("extractName", Flag.extractName("-hello"));

    assertEquals("extractName", "hello", Flag.extractName("--hello"));
    assertEquals("extractName", "h", Flag.extractName("--h"));
    assertEquals("extractName", "h", Flag.extractName("-h"));
  }


}  // class FlagTest
