package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.FavoredRoute;

public interface FavoredRouteRepository extends CrudRepository<FavoredRoute, Integer> {

}
