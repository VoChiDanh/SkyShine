package net.danh.MIR.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.util.StringUtil;

public class TabCompleteContainer {
   List<TabCompleteComponent> components = new ArrayList<>();

   Map<String, TabCompleteComponent> mappedComponents = new HashMap<>();

   public List<String> generate(int length, String arg, String lastArg) {
      List<String> completions = new ArrayList<>();
      List<String> component = new ArrayList<>();
      if (this.mappedComponents.containsKey(lastArg)) {
         component.addAll(this.mappedComponents.get(lastArg).generate());
      } else {
         if (this.components.size() <= length)
            return new ArrayList<>();
         component.addAll(this.components.get(length).generate());
      }
      if (component.isEmpty())
         return component;
      return StringUtil.copyPartialMatches(arg, component, completions);
   }

   public void add(TabCompleteComponent tcc) {
      this.components.add(tcc);
   }

   public void add(String arg, TabCompleteComponent tcc) {
      this.mappedComponents.put(arg, tcc);
   }
}
