package org.smartcliparser.demos;

import java.util.Iterator;

import org.smartcliparser.Flag;
import org.smartcliparser.CommandLineProgram;

public class Reverse extends CommandLineProgram {

  private Flag inputFlag;
  private Flag lowercaseFlag;
  private Flag uppercaseFlag;

  public Reverse(String[] args) {
    super(args);
  }

  @Override
  public void initialize() {
    // Specifying flags.
    inputFlag = new Flag(new String[]{"input", "i"}, true, 1,
      Flag.UNLIMITED_NUM_OF_ARGS);
    uppercaseFlag = Flag.createSwitch(new String[]{"uppercase", "u"});
    lowercaseFlag = Flag.createSwitch(new String[]{"lowercase", "l"});

    // Registering all flags so that the program knows how to parse them.
    registerFlag(inputFlag);
    registerFlag(uppercaseFlag);
    registerFlag(lowercaseFlag);
  }

  @Override
  public void run() {
    Iterator<String> it = inputFlag.args.iterator();
    while (it.hasNext()) {
      String reversed = this.reverse(it.next());
      if (lowercaseFlag.isSet()) {
        reversed = reversed.toLowerCase();
      } else if (uppercaseFlag.isSet()) {
        reversed = reversed.toUpperCase();
      }
      System.out.println(reversed);
    }
  }

  private String reverse(String string) {
    StringBuffer out = new StringBuffer();
    for (int i = string.length() - 1; i >= 0; i--) {
      out.append(string.charAt(i));
    }
    return out.toString();
  }

  public static void main(String[] args) {
    Reverse reverse = new Reverse(args);
  }

}  // class Reverse
