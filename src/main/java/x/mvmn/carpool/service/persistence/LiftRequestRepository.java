package x.mvmn.carpool.service.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import x.mvmn.carpool.model.LiftRequest;

public interface LiftRequestRepository extends JpaRepository<LiftRequest, Integer> {

}
