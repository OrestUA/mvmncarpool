package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.Route;

public interface RouteRepository extends CrudRepository<Route, Integer> {

}
