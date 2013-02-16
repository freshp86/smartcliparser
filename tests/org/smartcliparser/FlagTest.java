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

public class FlagTest {

  @Test
  public void testConstructor() {
    Flag flag = new Flag(new String[]{"hello", "world"}, true, 1, 5, false);
    assertEquals("getNumOfArgsMin", 1, flag.getNumOfArgsMin());
    assertEquals("getNumOfArgsMax", 5, flag.getNumOfArgsMax());
    assertTrue("hasName", flag.hasName("hello"));
    assertTrue("hasName", flag.hasName("world"));
    assertFalse("isSet", flag.isSet());
    assertTrue("args.isEmpty", flag.args.isEmpty());
  }


  @Test
  public void testSetNumOfArgs() {
    Flag flag = new Flag(new String[]{"hello", "world"}, true, 1, 5, false);

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
    Flag flag1 = new Flag(new String[]{"hello", "world"}, false, 0, 5, false);
    assertTrue(flag1.isValid());
    // Test that a required flag is not valid when it is not set.
    Flag flag2 = new Flag(new String[]{"hello", "world"}, true, 0, 5, false);
    assertFalse(flag2.isValid());
  }


  @Test
  public void testConsume() {
    // TODO(dpapad): Add extensive tests.
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
