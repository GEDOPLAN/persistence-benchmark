package de.gedoplan.sample.jpa;

import de.gedoplan.sample.entity.CityIdentity;
import de.gedoplan.sample.entity.CityTable;

import java.util.Random;

public class CitySource {
  private static Random random = new Random();

  public static CityIdentity createCityIdentity() {
    return new CityIdentity("DummyCity" + random.nextInt(100000), 250000 + random.nextInt(2500000), 500 + random.nextInt(500));
  }

  public static CityTable createCityTable() {
    return new CityTable("DummyCity" + random.nextInt(100000), 250000 + random.nextInt(2500000), 500 + random.nextInt(500));
  }
}
