package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmailAddress(String emailAddress);

}
