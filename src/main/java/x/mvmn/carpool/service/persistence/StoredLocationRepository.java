package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.StoredLocation;
import x.mvmn.carpool.model.User;

public interface StoredLocationRepository extends JpaRepository<StoredLocation, Integer> {

	public List<StoredLocation> findByUser(User user);
}
