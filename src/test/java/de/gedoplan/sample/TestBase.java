package de.gedoplan.sample;

import de.gedoplan.sample.entity.CityBase;

import org.junit.Test;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class TestBase {

  public static final int CHUNK_SIZE = CityBase.ALLOCATION_SIZE;

  @Test
  public void runBenchmark() throws RunnerException {
    Options options = new OptionsBuilder()
        .include(getClass().getName())
        .forks(1)
        .warmupIterations(2)
        .measurementIterations(10)
        .build();

    new Runner(options).run();
  }

}
