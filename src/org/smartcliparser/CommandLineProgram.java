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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * An abstract base class to be extended by coomand line programs.
 */
public abstract class CommandLineProgram {

  /**
   * A mapping of flag names to Flag objects.
   */
  public Map<String, Flag> flagsMap;

  public Set<Flag> flags;

  /**
   * A set of flags that is required. At least one of the flags in this set
   * needs to be specified for parsing to succeed.
   */
  private List<Flag> requiredFlagSet = null;

  /**
   * A special flag used for managing unconsumed arguments.
   */
  private Flag unconsumed;

  /**
   * A list of all arguments passed to this program.
   */
  public List<String> args;


  /**
   * Creates an instance without parsing any args.
   */
  public CommandLineProgram() {
    this.flags = new HashSet<Flag>();
    this.flagsMap = new HashMap<String, Flag>();
    this.unconsumed = new Flag(
        new String[]{"unconsumed"}, false, 0, 0, null, true);
    this.registerFlag(this.unconsumed);
  }


  /**
   * Creates an instance and parses |args|.
   */
  public CommandLineProgram(String[] args) {
    this();
    initialize();
    if (!this.parseArgs(args)) {
      System.err.println("Invalid use, see --help");
      CommandLineProgram.printErrors(this.getErrors());
      System.exit(1);
    } else {
      run();
    }
  }


  public List<ParsingError> getErrors() {
    List<ParsingError> errors = new LinkedList<ParsingError>();
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext()) {
      errors.addAll(it.next().getErrors());
    }

    // TODO(dpapad): Cache the result, so that this method is not called twice.
    if (!this.checkRequiredFlagSetSatisfied()) {
      errors.add(new MultiFlagParsingError(
          MultiFlagParsingError.Type.REQUIRED_FLAG_SET_VIOLATION,
          this.requiredFlagSet));
    }

    ListIterator<String> itArgs = this.args.listIterator();
    while (itArgs.hasNext()) {
      String arg = itArgs.next();
      if (Flag.isFlagLike(arg) && !hasFlag(Flag.extractName(arg))) {
        errors.add(new SingleFlagParsingError(
              SingleFlagParsingError.Type.UNKNOWN_FLAG, arg));
      }
    }
    return errors;
  }


  public static void printErrors(List<ParsingError> errors) {
    Iterator<ParsingError> it = errors.iterator();
    while (it.hasNext()) {
      System.err.println(it.next().toString());
    }
  }


  /**
   * Sets the maximum/minumum allowable number of unconsumed args.
   */
  public void setUnconsumedFlags(int min, int max) {
    this.unconsumed.setNumOfArgs(min, max);
  }


  /**
   * Subclasses should register all flags within this method.
   */
  public abstract void initialize();


  /**
   * This method will get called if all arguments are parsed correctly.
   */
  public abstract void run();


  /**
   * Registers a flag.
   * @param flag The flag to register.
   */
  public void registerFlag(Flag flag) {
    if (!this.flags.add(flag)) {
      return;
    }
    List<String> names = flag.getNames();
    Iterator<String> it = names.iterator();
    while (it.hasNext()) {
      this.flagsMap.put(it.next(), flag);
    }
  }


  /**
   * Registers a required flag set. Such a set indicates that at least one of
   * the listed flags has to be provided, otherwise parsing should fail.
   */
  public void setRequiredFlagSet(Flag[] flags) {
    this.requiredFlagSet = Arrays.asList(flags);
  }


  /**
   * Checks if |name| corresponds to a registered flag.
   * @param name The name of the flag to check.
   * @return True if a flag with that name is registered.
   */
  public boolean hasFlag(String name) {
    return this.flagsMap.get(name) != null;
  }


  /**
   * Parses arguments.
   * @param args The arguments to parse.
   */
  public boolean parseArgs(String[] args) {
    this.args = new LinkedList<String>(Arrays.asList(args));
    ListIterator<String> itArgs = this.args.listIterator();
    while (itArgs.hasNext()) {
      String arg = itArgs.next();
      if (Flag.isFlagLike(arg) && hasFlag(Flag.extractName(arg))) {
        itArgs.remove();
        this.flagsMap.get(Flag.extractName(arg)).consume(this.args, itArgs);
      }
    }

    // Placing remaining args to this.unconsumed as described by it.
    itArgs = this.args.listIterator();
    if (itArgs.hasNext()) {
      this.unconsumed.consume(this.args, itArgs);
    }
    // TODO: consume again here until only uknown flags exist in this.args.

    return this.isParsingValid();
  }


  /**
   * Checks if parsing of arguments was successful. It can fail if a required
   * flag was not present, if wrong number of args was passed to a flag.
   * @return True if parsing was valid.
   */
  public boolean isParsingValid() {
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext()) {
      if (!it.next().isValid()) {
        return false;
      }
    }

    if (!this.checkRequiredFlagSetSatisfied()) {
      return false;
    }

    // Checking if the number of unconsumed flags is as expected. All
    // unconsumed args should have been consumed by |this.unconsumed| and
    // |this.args| should be empty.
    if (this.args.size() > 0 || !this.unconsumed.isValid()) {
      return false;
    }

    return true;
  }


  /**
   * Checking that at least one of the flags in the required flags set is
   * actually set.
   * @return True if the required flag set constraint is met.
   */
  public boolean checkRequiredFlagSetSatisfied() {
    if (this.requiredFlagSet == null) {
      return true;
    }

    Iterator<Flag> it = this.requiredFlagSet.iterator();
    boolean requiredFlagSetSatisfied = false;
    while (it.hasNext()) {
      if (it.next().isSet()) {
        requiredFlagSetSatisfied = true;
        break;
      }
    }

    return requiredFlagSetSatisfied;
  }


  /**
   * Clears all parsed data.
   */
  public void clear() {
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext()) {
      it.next().args.clear();
    }
    this.args.clear();
  }

}  // class CommandLineProgram
