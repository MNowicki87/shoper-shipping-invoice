package eu.michalnowicki.shoperdemo.backend.domain.order;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.StatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StatusFacade {
   
   private final StatusRepository statusRepo;
   
   public static Status map(final StatusDto statusDto) {
      return Status.builder()
            .id(statusDto.statusId)
            .name(statusDto.name)
            .type(StatusType.valueOf(statusDto.type))
            .build();
   }
   
   public Status getById(final Integer id) {
      return statusRepo.getOne(id);
   }
   
   public Status getOrSave(final Status status) {
      final Optional<Status> optionalStatus = statusRepo.findById(status.getId());
      return optionalStatus.orElse(statusRepo.save(status));
   }
   
   public void saveOrUpdate(final Status newStatus) {
      final Integer id = newStatus.getId();
      final Optional<Status> optionalStatus = statusRepo.findById(id);
      if (optionalStatus.isPresent()) {
         final Status status = optionalStatus.get();
         status.setName(newStatus.getName());
         status.setType(newStatus.getType());
         statusRepo.save(status);
      } else {
         statusRepo.save(newStatus);
      }
   }
   
}
