package de.gedoplan.sample.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Access(AccessType.FIELD)
@Getter
@Setter
public abstract class CityBase {

  public static final String GENERATOR_TABLE = "PB_CITY_GEN";
  public static final String GENERATOR_PK_COL = "GENERATOR";
  public static final String GENERATOR_PK_VALUE = "CITY_GENERATOR";
  public static final String GENERATOR_VALUE_COL = "NEXT_ID";

  public static final int ALLOCATION_SIZE = 1000;

  private String name;

  private int population;

  private int area;

  protected CityBase() {
  }

  public CityBase(String title, int population, int area) {
    this.name = title;
    this.population = population;
    this.area = area;
  }

  public abstract Integer getId();

  @Override
  public int hashCode() {
    return getId() != null ? getId().hashCode() : 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (getClass() != obj.getClass()) {
      return false;
    }

    final CityBase other = (CityBase) obj;

    return getId() != null && getId().equals(other.getId());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " [id=" + getId() + ", name=" + this.name + ", population=" + this.population + ", area=" + this.area + "]";
  }
}
