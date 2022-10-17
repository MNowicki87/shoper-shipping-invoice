package eu.michalnowicki.shoperdemo.backend.domain.address;

import eu.michalnowicki.shoperdemo.backend.shoper.dto.AddressDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public final class AddressFactory {
   
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
