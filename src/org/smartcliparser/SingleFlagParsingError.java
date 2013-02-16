/* Copyright 2012 Demetrios Papadopoulos

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

public class SingleFlagParsingError implements ParsingError {

  /**
   * Types of errors that can be related to a single flag.
   */
  public static enum Type {
    MAX_NUMBER_OF_ARGS_VIOLATION,
    MIN_NUMBER_OF_ARGS_VIOLATION,
    PATTERN_VIOLATION,
    REQUIRED_FLAG_NOT_SET,
    UNKNOWN_FLAG
  }

  /**
   * The flag that caused the error.
   */
  public Flag flag;

  /**
   * The type of the error that occurred.
   */
  public Type type;


  public SingleFlagParsingError(SingleFlagParsingError.Type type, Flag flag) {
    this.flag = flag;
    this.type = type;
  }


  public SingleFlagParsingError(
      SingleFlagParsingError.Type type, String flagName) {
    this.flag = new Flag(flagName);
    this.type = type;
  }


  @Override
  public String toString() {
    return this.flag.getNames().get(0) + ": " + this.type.toString() + ": " +
        this.getDescription();
  }


  @Override
  public String getDescription() {
    if (this.type == Type.MIN_NUMBER_OF_ARGS_VIOLATION) {
      return "Expected at least " + flag.getNumOfArgsMin() +
          " arguments, but got " + flag.args.size();
    } else if (this.type == Type.MAX_NUMBER_OF_ARGS_VIOLATION) {
      return "Expected at most " + flag.getNumOfArgsMax() +
          " arguments, but got " + flag.args.size();
    } else if (this.type == Type.PATTERN_VIOLATION) {
      return "Arguments should follow the pattern " + flag.pattern + ".";
    } else if (this.type == Type.REQUIRED_FLAG_NOT_SET) {
      return "Required flag " + flag.getNames().get(0) + " was not set.";
    } else if (this.type == Type.UNKNOWN_FLAG) {
      return "Flag " + flag.getNames().get(0) + " does not exist.";
    }
    return "";
  }

}  // class SingleFlagParsingError
