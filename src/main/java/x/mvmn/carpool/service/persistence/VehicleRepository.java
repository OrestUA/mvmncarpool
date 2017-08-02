package x.mvmn.carpool.service.persistence;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import x.mvmn.carpool.model.User;
import x.mvmn.carpool.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

	public List<Vehicle> findByOwner(User owner);

	@Modifying
	@Transactional
	public int deleteById(int id);

}
