package de.gedoplan.sample.jpa;

import de.gedoplan.sample.TestBase;
import de.gedoplan.sample.entity.CityIdentity;
import de.gedoplan.sample.entity.CityTable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

public class NativeBenchmark extends TestBase {

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
    EntityManager entityManager = benchmarkState.entityManagerFactory.createEntityManager();
    try {
      entityManager.getTransaction().begin();

      for (int i = 0; i < CHUNK_SIZE; ++i) {
        CityIdentity city = CitySource.createCityIdentity();
        entityManager
            .createNativeQuery("insert into " + CityIdentity.TABLE + "(name, population, area) values (?,?,?)")
            .setParameter(1, city.getName())
            .setParameter(2, city.getPopulation())
            .setParameter(3, city.getArea())
            .executeUpdate();
      }

      entityManager.getTransaction().commit();
    } finally {
      entityManager.close();
    }
  }

  @Benchmark
  public void fillTable(BenchmarkState benchmarkState) {
    EntityManager entityManager = benchmarkState.entityManagerFactory.createEntityManager();
    try {
      entityManager.getTransaction().begin();

      Number count = (Number) entityManager
          .createNativeQuery("select count(*) from " + CityTable.GENERATOR_TABLE + " where " + CityTable.GENERATOR_PK_COL + "=?")
          .setParameter(1, CityTable.GENERATOR_PK_VALUE)
          .getSingleResult();

      Number id;
      if (count.longValue() == 0) {
        id = 1;
        entityManager
            .createNativeQuery("insert into " + CityTable.GENERATOR_TABLE + "(" + CityTable.GENERATOR_PK_COL + "," + CityTable.GENERATOR_VALUE_COL + ") values (?,?)")
            .setParameter(1, CityTable.GENERATOR_PK_VALUE)
            .setParameter(2, id.intValue() + CHUNK_SIZE)
            .executeUpdate();
      } else {
        id = (Number) entityManager
            .createNativeQuery("select " + CityTable.GENERATOR_VALUE_COL + " from " + CityTable.GENERATOR_TABLE + " where " + CityTable.GENERATOR_PK_COL + "=?")
            .setParameter(1, CityTable.GENERATOR_PK_VALUE)
            .getSingleResult();

        entityManager
            .createNativeQuery("update " + CityTable.GENERATOR_TABLE + " set " + CityTable.GENERATOR_VALUE_COL + "=? where " + CityTable.GENERATOR_PK_COL + "=?")
            .setParameter(1, id.intValue() + CHUNK_SIZE)
            .setParameter(2, CityTable.GENERATOR_PK_VALUE)
            .executeUpdate();
      }

      for (int i = 0; i < CHUNK_SIZE; ++i) {
        CityTable city = CitySource.createCityTable();
        city.setId(id.intValue());
        entityManager
            .createNativeQuery("insert into " + CityTable.TABLE + "(id, name, population, area) values (?,?,?,?)")
            .setParameter(1, city.getId())
            .setParameter(2, city.getName())
            .setParameter(3, city.getPopulation())
            .setParameter(4, city.getArea())
            .executeUpdate();

        id = id.intValue() + 1;
      }

      entityManager.getTransaction().commit();
    } finally {
      entityManager.close();
    }
  }
}
