/* Copyright 2013 Demetrios Papadopoulos

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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import rubikscube.io.FileIOUtilities;
import rubikscube.io.TextIOUtilities;


// A Flag subclass used for specifying files on the command line.
public class FileSetFlag extends Flag {

  public FileSetFlag(String[] names, boolean isRequired, int numOfArgsMin,
      int numOfArgsMax) {
    super(names, isRequired, numOfArgsMin, numOfArgsMax, null, false);
  }


  public Collection<File> getFileSet() throws FileNotFoundException {
    return FileIOUtilities.getFileSet(this.args);
  }

  public Collection<File> getTextFileSet() throws FileNotFoundException {
    Collection<File> inputFiles = this.getFileSet();
    return TextIOUtilities.getTextFiles(inputFiles);
  }


}  // class FileSetFlag
