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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FileSetFlagTest {

  public FileSetFlag flag = null;


  @Before
  public void initialize() {
    flag = new FileSetFlag(new String[]{"input", "i"}, true, 1, 1);

    List<String> args = new ArrayList<String>(Arrays.asList(
          new String[]{"bin/testsClasses/resources/test_folder"}));
    ListIterator<String> it = args.listIterator();
    flag.consume(args, it);
  }


  @Test
  public void testGetFileSet() {
    try {
      Collection<File> allFiles = flag.getFileSet();
      assertEquals(4, allFiles.size());
    } catch (FileNotFoundException e) {
      fail();
    }
  }


  @Test
  public void testGetTextFileSet() {
    try {
      Collection<File> textFiles = flag.getTextFileSet();
      assertEquals(2, textFiles.size());
    } catch (FileNotFoundException e) {
      fail();
    }
  }


}  // class FileSetFlagTest
