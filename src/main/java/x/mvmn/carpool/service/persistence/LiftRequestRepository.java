package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.LiftRequest;

public interface LiftRequestRepository extends CrudRepository<LiftRequest, Integer> {

}
