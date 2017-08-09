package x.mvmn.carpool.service.persistence;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import x.mvmn.carpool.model.LiftRequest;

public interface LiftRequestRepository extends JpaRepository<LiftRequest, Integer>, JpaSpecificationExecutor<LiftRequest> {
	public static Specification<LiftRequest> specUserId(int userId) {
		return new Specification<LiftRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("user").get("id"), userId);
			}
		};
	}

	public static Specification<LiftRequest> specValidAfter(long validAfter) {
		return new Specification<LiftRequest>() {
			@Override
			public Predicate toPredicate(Root<LiftRequest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.greaterThanOrEqualTo(root.get("timeValidTo"), validAfter);
			}
		};
	}

	@Modifying
	@Transactional
	public int deleteByTimeValidToLessThan(long timeValidToCap);

	@Query("SELECT lr FROM LiftRequest lr WHERE lr.lat > :latFrom AND lr.lat < :latTo AND lr.lon > :lonFrom AND lr.lon < :lonTo AND lr.timeValidTo > validAfter")
	public List<LiftRequest> findByLatLngInRangeAndValidAfter(@Param("latFrom") double latFrom, @Param("latTo") double latTo, @Param("lonFrom") double lonFrom,
			@Param("lonTo") double lonTo, @Param("lonTo") long validAfter);
}
