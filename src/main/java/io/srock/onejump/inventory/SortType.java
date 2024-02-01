package io.srock.onejump.inventory;

import io.srock.onejump.data.Jump;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public enum SortType {
  ID(jumps -> jumps.stream().sorted(Comparator.comparingInt(Jump::id)).toList()),
  DIFFICULTY(jumps -> jumps.stream().sorted(Comparator.comparingInt(Jump::difficulty)).toList());

  private final Function<List<Jump>, List<Jump>> sortFunction;

  SortType(Function<List<Jump>, List<Jump>> sort) {
    this.sortFunction = sort;
  }

  public List<Jump> sort(List<Jump> jumps) {
    return sortFunction.apply(jumps);
  }

  public SortType next() {
    var values = SortType.values();
    var id = ordinal() + 1;

    if (id > values.length - 1) {
      id = 0;
    }

    return values[id];
  }
}
