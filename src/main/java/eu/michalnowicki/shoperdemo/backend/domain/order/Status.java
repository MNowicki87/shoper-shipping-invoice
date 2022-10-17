package eu.michalnowicki.shoperdemo.backend.domain.order;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "statuses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Status {
   
   @Id
   @EqualsAndHashCode.Include
   private Integer id;
   
   @Enumerated(EnumType.STRING)
   private StatusType type;
   
   @NotBlank
   @Length(min = 3)
   private String name;
   
   
}
