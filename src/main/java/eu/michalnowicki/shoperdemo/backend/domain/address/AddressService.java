package eu.michalnowicki.shoperdemo.backend.domain.address;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
   
   private final AddressRepository addressRepo;
   
   public Address getOrSave(final Address newAddress) {
      final Optional<Address> optionalAddress = addressRepo.findById(newAddress.getId());
      if (optionalAddress.isPresent()) {
         final Address addressFromDb = optionalAddress.get();
         updateAddress(newAddress, addressFromDb);
         return addressRepo.save(addressFromDb);
      } else {
         return addressRepo.save(newAddress);
      }
   }
   
   private void updateAddress(final Address newAddress, final Address addressFromDb) {
      addressFromDb.setFirstName(newAddress.getFirstName());
      addressFromDb.setLastName(newAddress.getLastName());
      addressFromDb.setCompany(newAddress.getCompany());
      addressFromDb.setStreet1(newAddress.getStreet1());
      addressFromDb.setStreet2(newAddress.getStreet2());
      addressFromDb.setPostcode(newAddress.getPostcode());
      addressFromDb.setCity(newAddress.getCity());
      addressFromDb.setPesel(newAddress.getPesel());
      addressFromDb.setNip(newAddress.getNip());
      addressFromDb.setPhoneNumber(newAddress.getPhoneNumber());
   }
   
   public static Address map(AddressDto addressDto) {
      return Address.builder()
            .id(addressDto.addressId)
            .firstName(addressDto.firstname)
            .lastName(addressDto.lastname)
            .city(addressDto.city)
            .company(addressDto.company)
            .nip(addressDto.taxIdentificationNumber)
            .pesel(addressDto.pesel)
            .street1(addressDto.street1)
            .street2(addressDto.street2)
            .phoneNumber(addressDto.phone)
            .postcode(addressDto.postcode)
            .build();
   }
   
   
}
