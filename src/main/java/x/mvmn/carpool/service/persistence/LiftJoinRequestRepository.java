package x.mvmn.carpool.service.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import x.mvmn.carpool.model.LiftJoinRequest;
import x.mvmn.carpool.model.User;

public interface LiftJoinRequestRepository extends JpaRepository<LiftJoinRequest, Integer> {

	public List<LiftJoinRequest> findByUser(User user);

	@Query("SELECT ljr FROM LiftJoinRequest ljr WHERE ljr.user.id = :userId AND ljr.offer.timeValidTo > :validAfter")
	public List<LiftJoinRequest> findByUserAndTimeValidToGreaterThanEqual(@Param("userId") int userId, @Param("validAfter") long validAfter);

}
