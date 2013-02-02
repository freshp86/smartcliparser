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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

// A class representing a flag that of a command line program.
public class Flag {

  // Convenience alias for (seemingly) unlimited number of args.
  public static int UNLIMITED_NUM_OF_ARGS = Integer.MAX_VALUE;

  /**
   * A list of all names for this flag.
   */
  private List<String> names;

  /**
   * True if this flag is required for the program to run.
   */
  private boolean isRequired;

  /**
   * Minimum/Maximum number of arguments that this flag can consume.
   */
  private int numOfArgsMin;
  private int numOfArgsMax;

  /**
   * True if this flag was set.
   */
  private boolean isSet;

  /**
   * If true, |numOfArgsMax| is ignored while consuming args. It is still taken
   * into account when determining whether parsing succeeded.
   */
  private boolean forceConsume;

  /**
   * A list of all arguments that were consumed by this flag.
   */
  public List<String> args;

  public List<ParsingError> errors;


  public Flag(String name) {
    this(new String[]{name}, false, 0, 0, false);
  }


  public Flag(String[] names, boolean isRequired, int numOfArgsMin,
      int numOfArgsMax) {
    this(names, isRequired, numOfArgsMin, numOfArgsMax, false);
  }


  public Flag(String[] names, boolean isRequired, int numOfArgsMin,
      int numOfArgsMax, boolean forceConsume) {
    // TODO(dpapad); Make sure that the same name can not be registered twice.
    this.names = Arrays.asList(names);
    this.isRequired = isRequired;
    this.setNumOfArgs(numOfArgsMin, numOfArgsMax);
    this.forceConsume = forceConsume;
    this.isSet = false;
    this.args = new LinkedList<String>();
  }


  public static Flag createSwitch(String[] names) {
    return new Flag(names, false, 0, 0, false);
  }


  public boolean isSet() {
    return this.isSet;
  }


  public int getNumOfArgsMin() {
    return this.numOfArgsMin;
  }


  public int getNumOfArgsMax() {
    return this.numOfArgsMax;
  }


  /**
   * Sets the maximum/minumum allowable number of args for this flag.
   */
  public void setNumOfArgs(int min, int max) {
    this.numOfArgsMin = Math.max(0, Math.min(min, max));
    this.numOfArgsMax = Math.max(0, Math.max(min, max));
  }


  /**
   * @return A list of all names registered.
   */
  public List<String> getNames() {
    return this.names;
  }


  /**
   * Checks if the given name is registered.
   * @param name The name to check.
   * @return True if name is registered as a valid name.
   */
  public boolean hasName(String name) {
    Iterator<String> it = this.names.iterator();
    while (it.hasNext()) {
      if (it.next().equals(name))
        return true;
    }
    return false;
  }


  /**
   * Marks this flag as required or optional.
   * @param isRequired True if this flag is required for the program to execute.
   */
  public void setIsRequired(boolean isRequired) {
    this.isRequired = isRequired;
  }


  /**
   * Consumes all args that belong to this flag.
   * @param args The list of all passed args. All consumed args are removed from
   *     the list.
   * @param it The iterator that is used for traversing the list.
   */
  public void consume(List<String> args, ListIterator<String> it) {
    this.isSet = true;
    while (it.hasNext()) {
      String arg = it.next();
      if (!Flag.isFlagLike(arg) &&
         (this.forceConsume || this.args.size() < this.numOfArgsMax)) {
        it.remove();
        this.args.add(arg);
      } else {
        it.previous();
        break;
      }
    }
  }


  /**
   * Checks if flag is in a valid state.
   * @return True if this flag is in a valid state. Which means either of the
   *     following.
   *     1) The flag was invoked with valid arguments.
   *     2) The flag was not invoked and it is not required.
   */
  public boolean isValid() {
    return (this.isSet && this.args.size() >= this.numOfArgsMin &&
        this.args.size() <= this.numOfArgsMax) ||
        (!this.isSet && !this.isRequired);
  }


  public List<ParsingError> getErrors() {
    this.errors = new LinkedList<ParsingError>();
    if (this.isSet) {
      if (this.args.size() < this.numOfArgsMin) {
        this.errors.add(new ParsingError(
            ParsingError.Type.MIN_NUMBER_OF_ARGS_VIOLATION, this));
      } else if (args.size() > this.numOfArgsMax) {
        this.errors.add(new ParsingError(
            ParsingError.Type.MAX_NUMBER_OF_ARGS_VIOLATION, this));
      }
    } else if (this.isRequired) {
        this.errors.add(new ParsingError(
            ParsingError.Type.REQUIRED_FLAG_NOT_SET, this));
    }
    return this.errors;
  }


  /**
   * Checks if a string looks like a flag. A string is flag-like when either of
   * the following is true.
   * 1) It is exactly 2 characters long and the 1st character is a dash (-).
   * 2) It is longer than 2 characters and the first 2 characters are dashes
   *    (--).
   * @return True if the string looks like a flag.
   */
  public static boolean isFlagLike(String string) {
    // TODO(dpapad): User regular expressions instead.
    return (string.length() > 2 && string.substring(0, 2).equals("--")) ||
        (string.length() == 2 && !string.equals("--") &&
            string.substring(0, 1).equals("-"));
  }


  /**
   * Extracts the name of the flag by removing any preceding dashes.
   * @return The extracted name or null If |string| is not flag-like.
   */
  public static String extractName(String string) {
    if (!Flag.isFlagLike(string)) {
      return null;
    }

    if (string.length() > 2) {
      return string.substring(2);
    } else {
      return string.substring(1);
    }
  }


  public String toString() {
    StringBuffer out = new StringBuffer();
    out.append("names: " + this.names + "\n");
    out.append("required: " + this.isRequired + "\n");
    out.append("args: " + this.args);
    return out.toString();
  }

}  // class Flag
