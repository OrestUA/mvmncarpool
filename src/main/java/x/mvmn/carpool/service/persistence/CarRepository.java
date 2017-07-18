package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.Car;
import x.mvmn.carpool.model.User;

public interface CarRepository extends JpaRepository<Car, Integer> {

	public List<Car> findByOwner(User owner);

}
