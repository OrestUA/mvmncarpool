package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.LiftJoinRequest;

public interface LiftJoinRequestRepository extends JpaRepository<LiftJoinRequest, Integer> {

}
