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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CommandLineProgramTest {

  // Sample command line program used for testing.
  public class SampleProgram extends CommandLineProgram {

    /* TODO(dpapad): find out why the following makes testRunIsCalledFail.
     * public boolean success = false;
     * { System.out.println("init"); }
     */
    public boolean success;

    public SampleProgram() { };

    public SampleProgram(String[] args) {
      super(args);
    }

    public void initialize() {
      Flag f1 = new Flag(new String[]{"output", "o"}, true, 1, 1);
      Flag f2 = new Flag(new String[]{"input", "i"}, true, 1, 2);
      Flag f3 = Flag.createSwitch(new String[]{"compress", "c"});
      Flag f4 = new Flag(new String[]{"filter", "f"}, false, 0,
                         Flag.UNLIMITED_NUM_OF_ARGS);
      Flag f5 = Flag.createSwitch(new String[]{"decompress", "d"});
      registerFlag(f1);
      registerFlag(f2);
      registerFlag(f3);
      registerFlag(f4);
      registerFlag(f5);
      this.setRequiredFlagSet(new Flag[]{f3, f5});
    }

    public void run() {
      this.success = true;
    }

  }  // class SampleProgram

  private SampleProgram program;

  @Before
  public void setUp() {
    program = new SampleProgram();
    program.initialize();
  }

  // TODO(dpapad): Add expectation for the args of each individual flag.
  @Test
  public void testHasFlag() {
    assertTrue(program.hasFlag("output"));
    assertTrue(program.hasFlag("o"));
    assertTrue(program.hasFlag("input"));
    assertTrue(program.hasFlag("i"));

    assertFalse(program.hasFlag("--output"));
    assertFalse(program.hasFlag("nosuchflag"));
  }

  @Test
  public void testParseArgs_Success() {
    String[] args = {"--output", "log.txt",
                      "-i", "input1.txt", "input2.txt",
                      "-c"};
    assertTrue(program.parseArgs(args));
    assertEquals(0, program.args.size());
  }


  @Test
  public void testParseArgs_RequiredFlagSetViolation() {
    String[] args = {"--output", "log.txt",
                      "-i", "input1.txt", "input2.txt"};
    assertFalse(program.parseArgs(args));
    assertEquals(0, program.args.size());
    assertEquals("errors.size", 1, program.getErrors().size());
  }

  @Test
  public void testParseArgs_MinArgsViolation() {
    String[] args = {"--output",
                      "-i", "input1.txt", "input2.txt",
                      "-c"};
    assertFalse(program.parseArgs(args));
  }

  @Test
  public void testParseArgs_UnconsumedMaxArgsViolation() {
    String[] args = new String[]{"--output", "log.txt", "log2.txt",
                                 "-i", "input1.txt", "input2.txt",
                                 "-c"};
    assertFalse(program.parseArgs(args));
    assertEquals(0, program.args.size());
  }

  @Test
  public void testParseArgs_UnknownFlagViolation() {
    String[] args = new String[]{"--output", "log.txt", "unconsumed1.txt",
                                 "-i", "input1.txt",
                                 "-d",
                                 "-u", "unconsumed2.txt"};
    // TODO: -u stops the unconsumed args to be consumed by this.unconsumed flag
    // decide if this is alright. Decision:No. Fix this.
    program.setUnconsumedFlags(0, 2);
    assertFalse("parseArgs", program.parseArgs(args));
    assertEquals("errors.size", 1, program.getErrors().size());
    assertEquals("args.size", 2, program.args.size());
  }

  @Test
  public void testParseArgs_UnconsumedSuccess() {
    String[] args = new String[]{"--output", "log.txt", "unconsumed1.txt",
                                 "-i", "input1.txt", "input2.txt",
                                 "-c", "unknown_arg"};
    program.setUnconsumedFlags(2, 2);
    assertTrue(program.parseArgs(args));
    assertEquals(0, program.args.size());
  }

  @Test
  public void testParseArgs_UnlimitedSuccess() {
    String[] args = new String[]{"--output", "log.txt",
                                 "-i", "input1.txt", "input2.txt",
                                 "-d",
                                 "--filter", "f1", "f2", "f3", "f4", "f5"};
    assertTrue("parseArgs", program.parseArgs(args));
    assertEquals("args.size", 0, program.args.size());
    Flag filterFlag = program.flagsMap.get("filter");
    assertEquals("filterFlag.args.size", 5, filterFlag.args.size());
  }

  @Test
  public void testRunIsCalled() {
    String[] args = {"--output", "log.txt",
                     "-i", "input1.txt", "input2.txt",
                     "-c" };
    SampleProgram program2 = new SampleProgram(args);
    assertTrue(program2.success);
  }

  @Test
  public void testRunIsNotCalled() {
    String[] args = {"--output", "log.txt", "log2.txt",
                     "-i", "input1.txt", "input2.txt",
                     "-c"};
    // TODO(dpapad) Find how to test a function that calls System.exit.
    //SampleProgram program3 = new SampleProgram(args);
    // JVM is shutting down and ant reports failure.
    //assertFalse(program3.success);
  }

}  // class CommandLineProgramTest
