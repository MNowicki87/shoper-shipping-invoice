package eu.michalnowicki.shoperdemo.backend.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;

interface StatusRepository extends JpaRepository<Status, Integer> {

}
