package eu.michalnowicki.shoperdemo.backend.domain.address;

import org.springframework.data.jpa.repository.JpaRepository;

interface AddressRepository extends JpaRepository<Address, Integer> {
}
