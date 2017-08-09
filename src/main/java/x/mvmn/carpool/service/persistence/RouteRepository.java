package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import x.mvmn.carpool.model.Route;
import x.mvmn.carpool.model.User;

public interface RouteRepository extends JpaRepository<Route, Integer> {

	public List<Route> findByUser(User user);

	@Query("SELECT r FROM Route r WHERE r.startLat>:latFrom AND r.startLat<:latTo AND r.startLon>:lonFrom AND r.startLon<:lonTo OR r.endLat>:latFrom AND r.endLat<:latTo AND r.endLon>:lonFrom AND r.endLon<:lonTo OR "
			+ "EXISTS (SELECT w FROM RouteWaypoint w WHERE w.route.id = r.id AND w.lat>:latFrom AND w.lat<:latTo AND w.lon>:lonFrom AND w.lon<:lonTo)")
	public List<Route> findByLatLngInRange(@Param("latFrom") double latFrom, @Param("latTo") double latTo, @Param("lonFrom") double lonFrom,
			@Param("lonTo") double lonTo);

}
