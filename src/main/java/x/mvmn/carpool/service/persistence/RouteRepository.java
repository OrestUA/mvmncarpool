package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.Route;

public interface RouteRepository extends JpaRepository<Route, Integer> {

}
