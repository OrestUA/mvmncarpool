package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.RouteWaypoint;

public interface RouteWaypointRepository extends CrudRepository<RouteWaypoint, Integer> {

}
