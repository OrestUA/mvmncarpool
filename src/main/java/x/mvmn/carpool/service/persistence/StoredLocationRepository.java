package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.StoredLocation;

public interface StoredLocationRepository extends JpaRepository<StoredLocation, Integer> {

}
