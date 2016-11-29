package de.gedoplan.sample.entity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import lombok.Getter;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Table(name = CityTable.TABLE)
@Getter
@Setter
public class CityTable extends CityBase {
  public static final String TABLE = "PB_CITY_TABLE";

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "cityGeneratorTable")
  @TableGenerator(name = "cityGeneratorTable", table = GENERATOR_TABLE, pkColumnName = GENERATOR_PK_COL, pkColumnValue = GENERATOR_PK_VALUE, valueColumnName = GENERATOR_VALUE_COL, allocationSize = ALLOCATION_SIZE)
  private Integer id;

  protected CityTable() {
    super();
  }

  public CityTable(String title, int population, int area) {
    super(title, population, area);
  }

}
