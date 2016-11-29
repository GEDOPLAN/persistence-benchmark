package de.gedoplan.sample.jpa;

import de.gedoplan.sample.TestBase;
import de.gedoplan.sample.entity.CityBase;
import de.gedoplan.sample.entity.CityIdentity;
import de.gedoplan.sample.entity.CityTable;

import java.util.function.Supplier;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class JpaBenchmark extends TestBase {

  static final Log LOG = LogFactory.getLog(JpaBenchmark.class);

  @State(Scope.Thread)
  public static class BenchmarkState {
    EntityManagerFactory entityManagerFactory;

    @Setup(Level.Trial)
    public void setupTrial() {
      this.entityManagerFactory = Persistence.createEntityManagerFactory("showcase-se");
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
      EntityManager entityManager = this.entityManagerFactory.createEntityManager();
      try {
        entityManager.getTransaction().begin();

        entityManager.createQuery("delete from " + CityIdentity.class.getSimpleName()).executeUpdate();
        entityManager.createQuery("delete from " + CityTable.class.getSimpleName()).executeUpdate();

        entityManager.getTransaction().commit();
      } finally {
        entityManager.close();
      }
    }

    @TearDown(Level.Trial)
    public void tearDownTrial() {
      this.entityManagerFactory.close();
    }
  }

  @Benchmark
  public void fillIdentity(BenchmarkState benchmarkState) {
    fill(benchmarkState, CitySource::createCityIdentity);
  }

  @Benchmark
  public void fillTable(BenchmarkState benchmarkState) {
    fill(benchmarkState, CitySource::createCityTable);
  }

  private void fill(BenchmarkState benchmarkState, Supplier<CityBase> citySupplier) {
    EntityManager entityManager = benchmarkState.entityManagerFactory.createEntityManager();
    try {
      entityManager.getTransaction().begin();

      for (int i = 0; i < CHUNK_SIZE; ++i) {
        entityManager.persist(citySupplier.get());
      }

      entityManager.getTransaction().commit();
    } finally {
      entityManager.close();
    }
  }
}
