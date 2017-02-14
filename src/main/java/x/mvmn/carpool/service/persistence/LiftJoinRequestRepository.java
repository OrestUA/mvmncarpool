package x.mvmn.carpool.service.persistence;

import org.springframework.data.repository.CrudRepository;

import x.mvmn.carpool.model.LiftJoinRequest;

public interface LiftJoinRequestRepository extends CrudRepository<LiftJoinRequest, Integer> {

}
