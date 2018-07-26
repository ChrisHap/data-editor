package de.hhu.stups.plues.dataeditor.ui.entities;

import org.hibernate.annotations.NaturalId;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "modules")
public class Module extends ModelEntity implements Serializable {
  private static final long serialVersionUID = 7922130660874935173L;

  @Id
  @Column(name = "id")
  private int id;

  @NaturalId
  private String key;

  private String title;

  private Integer pordnr;

  @Column(name = "elective_units")
  private Integer electiveUnits;

  private Boolean bundled;

  @ManyToMany(mappedBy = "modules")
  private Set<AbstractUnit> abstractUnits;

  @ManyToMany(mappedBy = "modules")
  private Set<Course> courses;

  @OneToMany(mappedBy = "module")
  private Set<ModuleLevel> moduleLevels;

  @OneToMany(mappedBy = "module")
  private Set<ModuleAbstractUnitSemester> moduleAbstractUnitSemesters;

  @OneToMany(mappedBy = "module")
  private Set<ModuleAbstractUnitType> moduleAbstractUnitTypes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinTable(name = "module_levels",
        inverseJoinColumns = @JoinColumn(name = "level_id",
              referencedColumnName = "id"),
        joinColumns = @JoinColumn(name = "module_id",
              referencedColumnName = "id"))
  private Level level;

  public Module() {
    // Default constructor is required by hibernate
  }

  public Boolean getBundled() {
    return bundled;
  }

  public void setBundled(final Boolean bundled) {
    this.bundled = bundled;
  }

  public int getId() {
    return id;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public Integer getPordnr() {
    return pordnr;
  }

  public void setPordnr(final Integer pordnr) {
    this.pordnr = pordnr;
  }

  public Integer getElectiveUnits() {
    return electiveUnits;
  }

  public void setElectiveUnits(final Integer electiveUnits) {
    this.electiveUnits = electiveUnits;
  }

  public Set<ModuleLevel> getModuleLevels() {
    return this.moduleLevels;
  }

  public void setModuleLevels(Set<ModuleLevel> moduleLevels) {
    this.moduleLevels = moduleLevels;
  }

  public Set<AbstractUnit> getAbstractUnits() {
    return abstractUnits;
  }

  public void setAbstractUnits(final Set<AbstractUnit> abstractUnits) {
    this.abstractUnits = abstractUnits;
  }

  public Set<ModuleAbstractUnitSemester> getModuleAbstractUnitSemesters() {
    return moduleAbstractUnitSemesters;
  }

  public void setModuleAbstractUnitSemesters(
        final Set<ModuleAbstractUnitSemester> moduleAbstractUnitSemesters) {
    this.moduleAbstractUnitSemesters = moduleAbstractUnitSemesters;
  }

  /**
   * Returns the set of semesters in which the abstract unit au is associated to this module.
   *
   * @param au AbstractUnit
   * @return Set of semester numbers.
   */
  public Set<Integer> getSemestersForAbstractUnit(final AbstractUnit au) {
    return this.moduleAbstractUnitSemesters.stream()
          .filter(maus -> maus.getAbstractUnit().equals(au))
          .map(ModuleAbstractUnitSemester::getSemester)
          .collect(Collectors.toSet());
  }

  public Set<ModuleAbstractUnitType> getModuleAbstractUnitTypes() {
    return moduleAbstractUnitTypes;
  }

  public void setModuleAbstractUnitTypes(
        final Set<ModuleAbstractUnitType> moduleAbstractUnitTypes) {
    this.moduleAbstractUnitTypes = moduleAbstractUnitTypes;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    if (!super.equals(other)) {
      return false;
    }
    final Module module = (Module) other;
    return getId() == module.getId()
          && Objects.equals(key, module.key)
          && Objects.equals(getTitle(), module.getTitle())
          && Objects.equals(getPordnr(), module.getPordnr())
          && Objects.equals(getElectiveUnits(), module.getElectiveUnits())
          && Objects.equals(getBundled(), module.getBundled());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId(), key, getTitle(),
          getPordnr(), getElectiveUnits(), getBundled());
  }

  public Set<Course> getCourses() {
    return courses;
  }

  public void setCourses(Set<Course> courses) {
    this.courses = courses;
  }
}
