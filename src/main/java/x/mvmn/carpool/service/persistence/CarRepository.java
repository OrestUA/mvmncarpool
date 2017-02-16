package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.Car;
import x.mvmn.carpool.model.User;

public interface CarRepository extends CrudRepository<Car, Integer> {

	public List<Car> findByOwner(User owner);

}
