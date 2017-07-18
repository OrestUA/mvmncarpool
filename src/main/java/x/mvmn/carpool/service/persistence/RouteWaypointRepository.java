package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.RouteWaypoint;

public interface RouteWaypointRepository extends JpaRepository<RouteWaypoint, Integer> {

}
