package ai.aitia.rfid_employee.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "history")
public class History {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @NotNull
  private String tagId;

  @NotNull
  @Size(max = 255, message = "Employee name can not be longer than 255 character")
  private String name;

  @Column(name = "is_inside")
  @Type(type = "yes_no")
  private boolean inside;

  @PastOrPresent(message = "RFID timestamp can not be in the future")
  private ZonedDateTime timestamp;

  public History() {
  }

  public History(Employee employee) {
    this.tagId = employee.getTagId();
    this.name = employee.getName();
    this.inside = employee.isInside();
    this.timestamp = employee.getLastSwipe();
  }

  public History(@NotNull String tagId, @NotNull @Size(max = 255, message = "Employee name can not be longer than 255 character") String name) {
    this.tagId = tagId;
    this.name = name;
  }

  public History(@NotNull String tagId, @NotNull @Size(max = 255, message = "Employee name can not be longer than 255 character") String name,
                 boolean inside, @PastOrPresent(message = "RFID timestamp can not be in the future") ZonedDateTime timestamp) {
    this.tagId = tagId;
    this.name = name;
    this.inside = inside;
    this.timestamp = timestamp;
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
    return inside;
  }

  public void setInside(boolean inside) {
    this.inside = inside;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }
}
