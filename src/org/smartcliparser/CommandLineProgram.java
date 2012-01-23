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
   * A special flag used for managing unconsumed arguments.
   */
  private Flag unconsumed;

  /**
   * A list of all arguments passed to this program.
   */
  public List<String> args;

  /**
   * A list of all the errors that occured while parsing.
   */
  public List<ParsingError> errors;

  /**
   * Creates an instance without parsing any args.
   */
  public CommandLineProgram() {
    this.flags = new HashSet<Flag>();
    this.flagsMap = new HashMap<String, Flag>();
    this.unconsumed = new Flag(new String[]{ "unconsumed" }, false, 0, 0, true);
    this.registerFlag(this.unconsumed);
  }

  /**
   * Creates an instance and parses |args|.
   */
  public CommandLineProgram(String[] args) {
    this();
    initialize();
    if (!parseArgs(args)) {
      System.out.println("Invalid use, see --help");
      detectErrors();
      printErrors();
      System.exit(1);
    } else {
      run();
    }
  }

  public void detectErrors() {
    errors = new LinkedList<ParsingError>();
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext())
      errors.addAll(it.next().getErrors());

    ListIterator<String> itArgs = this.args.listIterator();
    while (itArgs.hasNext()) {
      String arg = itArgs.next();
      if (Flag.isFlagLike(arg) && !hasFlag(Flag.extractName(arg))) {
      errors.add(new ParsingError(ParsingError.Type.UNKNOWN_FLAG, arg));
      }
    }
  }

  public void printErrors() {
    Iterator<ParsingError> errorsIterator = errors.iterator();
    while (errorsIterator.hasNext()) {
      System.out.println(errorsIterator.next().toString());
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
    if (!this.flags.add(flag))
      return;
    List<String> names = flag.getNames();
    Iterator<String> it = names.iterator();
    while (it.hasNext())
      flagsMap.put(it.next(), flag);
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
    if (itArgs.hasNext())
      this.unconsumed.consume(this.args, itArgs);
    // TODO: consume again here until only uknown flags exist in this.args.

    return isParsingValid();
  }

  /**
   * Checks if parsing of arguments was successful. It can fail if a required
   * flag was not present, if wrong number of args was passed to a flag.
   * @return True if parsing was valid.
   */
  public boolean isParsingValid() {
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext()) {
      if (!it.next().isValid())
        return false;
    }

    // Checking if the number of unconsumed flags is as expected. All
    // unconsumed args should have been consumed by |this.unconsumed| and
    // |this.args| should be empty.
    if (this.args.size() > 0 || !this.unconsumed.isValid())
      return false;

   // TODO: find how to return the exact error that caused parsing to fail.
    return true;
  }

  /**
   * Clears all parsed data.
   */
  public void clear() {
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext())
      it.next().args.clear();
    this.args.clear();
  }

}  // class CommandLineProgram 
