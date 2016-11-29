package de.gedoplan.sample.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Table(name = CityIdentity.TABLE)
@Getter
@Setter
public class CityIdentity extends CityBase {
  public static final String TABLE = "PB_CITY_IDENTITY";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  protected CityIdentity() {
    super();
  }

  public CityIdentity(String title, int population, int area) {
    super(title, population, area);
  }

}
