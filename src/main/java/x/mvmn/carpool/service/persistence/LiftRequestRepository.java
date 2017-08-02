package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.LiftRequest;
import x.mvmn.carpool.model.User;

public interface LiftRequestRepository extends JpaRepository<LiftRequest, Integer> {
	public List<LiftRequest> findByUserAndTimeValidToGreaterThanEqual(User user, long timeValidTo);

	public List<LiftRequest> findByTimeValidToGreaterThanEqual(long timeValidTo);
}
