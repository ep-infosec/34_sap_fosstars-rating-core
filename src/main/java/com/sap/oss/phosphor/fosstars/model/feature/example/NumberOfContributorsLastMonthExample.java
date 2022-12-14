package com.sap.oss.phosphor.fosstars.model.feature.example;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.oss.phosphor.fosstars.model.Value;
import com.sap.oss.phosphor.fosstars.model.feature.AbstractFeature;
import com.sap.oss.phosphor.fosstars.model.value.IntegerValue;

/**
 * This is a sample feature which represents a number of contributors last month. The feature is
 * stateless and therefore immutable. This feature is only for demo purposes.
 */
@JsonSerialize
public class NumberOfContributorsLastMonthExample extends AbstractFeature<Integer> {

  /**
   * Initializes a new feature.
   */
  NumberOfContributorsLastMonthExample() {
    super("Number of contributors last month (example)");
  }

  @Override
  public IntegerValue value(Integer object) {
    return new IntegerValue(this, check(object));
  }

  @Override
  public Value<Integer> parse(String string) {
    return value(Integer.valueOf(string));
  }

  private static Integer check(Integer n) {
    if (n < 0) {
      throw new IllegalArgumentException(String.format(
          "Number of contributors (%d) can't be negative!", n));
    }

    return n;
  }

}
