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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiFlagParsingError implements ParsingError {

  public static enum Type {
    REQUIRED_FLAG_SET_VIOLATION
  }

  public List<Flag> flags;
  public Type type;


  public MultiFlagParsingError(
      MultiFlagParsingError.Type type, List<Flag> flags) {
    this.flags = flags;
    this.type = type;
  }


  public String toString() {
    return this.getFlagNames() + ": " + this.type.toString() + ": " +
        this.getDescription();
  }

  private List<String> getFlagNames() {
    List<String> flagNames = new ArrayList<String>();
    Iterator<Flag> it = this.flags.iterator();
    while (it.hasNext()) {
      flagNames.add(it.next().getNames().get(0));
    }
    return flagNames;
  }


  @Override
  public String getDescription() {
    if (this.type == Type.REQUIRED_FLAG_SET_VIOLATION) {
      return "At least one of these flags neeeds to be set.";
    }
    return "";
  }

}  // class MultiFlagParsingError
