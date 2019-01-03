package ai.aitia.rfid_employee;

import java.time.ZonedDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "employee")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  @NaturalId
  private String tagId;

  @NotNull
  @Size(max = 255, message = "Employee name can not be longer than 255 character")
  private String name;

  @Type(type = "yes_no")
  private boolean isInside;

  @PastOrPresent(message = "Last RFID timestamp can not be in the future")
  private ZonedDateTime lastSwipe;

  public Employee() {
  }

  public Employee(@NotNull String tagId, @NotNull @Size(max = 255, message = "Employee name can not be longer than 255 character") String name) {
    this.tagId = tagId;
    this.name = name;
  }

  public Employee(String tagId, String name, boolean isInside, ZonedDateTime lastSwipe) {
    this.tagId = tagId;
    this.name = name;
    this.isInside = isInside;
    this.lastSwipe = lastSwipe;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTagId() {
    return tagId;
  }

  public void setTagId(String tagId) {
    this.tagId = tagId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isInside() {
    return isInside;
  }

  public void setInside(boolean inside) {
    isInside = inside;
  }

  public ZonedDateTime getLastSwipe() {
    return lastSwipe;
  }

  public void setLastSwipe(ZonedDateTime lastSwipe) {
    this.lastSwipe = lastSwipe;
  }
}
