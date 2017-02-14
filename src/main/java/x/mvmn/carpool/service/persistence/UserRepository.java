package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

	public User findByEmailAddress(String emailAddress);

}
