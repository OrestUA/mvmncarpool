package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.Route;
import x.mvmn.carpool.model.User;

public interface RouteRepository extends JpaRepository<Route, Integer> {

	public List<Route> findByUser(User user);

}
