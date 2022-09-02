package net.danh.MIR.command.tcc;

import net.danh.MIR.command.TabCompleteComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListTCComponent extends TabCompleteComponent {
   final List<String> list = new ArrayList<>();

   public StringListTCComponent(String... strings) {
      this.list.addAll(Arrays.asList(strings));
   }

   public List<String> generate() {
      return this.list;
   }
}
