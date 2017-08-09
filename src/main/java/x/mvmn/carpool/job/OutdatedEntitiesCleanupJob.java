package x.mvmn.carpool.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import x.mvmn.carpool.service.persistence.LiftJoinRequestRepository;
import x.mvmn.carpool.service.persistence.LiftOfferRepository;
import x.mvmn.carpool.service.persistence.LiftRequestRepository;

@Component
public class OutdatedEntitiesCleanupJob {

	// TODO: make job disablable, have configurable history storing period and run interval.

	private static final Logger LOGGER = LoggerFactory.getLogger(OutdatedEntitiesCleanupJob.class);

	@Autowired
	LiftRequestRepository liftRequestRepository;

	@Autowired
	LiftJoinRequestRepository liftJoinRequestRepository;

	@Autowired
	LiftOfferRepository liftOfferRepository;

	@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
	public void doCleanup() {
		LOGGER.info("Starting old lift requests cleanup.");

		long unixTime24HoursAgo = System.currentTimeMillis() / 1000 - 60 * 60 * 24;
		try {
			LOGGER.info(" - Outdated Lift Join Requests removed: {}",
					liftJoinRequestRepository.deleteByLiftRequestOrLiftOfferTimeValidToLessThan(unixTime24HoursAgo));
		} catch (Exception e) {
			LOGGER.error("Error occurred during old lift join requests cleanup.", e);
		}

		try {
			LOGGER.info(" - Outdated Lift Requests removed: {}", liftRequestRepository.deleteByTimeValidToLessThan(unixTime24HoursAgo));
		} catch (Exception e) {
			LOGGER.error("Error occurred during old lift requests cleanup.", e);
		}

		try {
			LOGGER.info(" - Outdated Lift Offers removed: {}", liftOfferRepository.deleteWhereTimeValidToBefore(unixTime24HoursAgo));
		} catch (Exception e) {
			LOGGER.error("Error occurred during old lift offers cleanup.", e);
		}
		LOGGER.info("Finished old lift requests cleanup.");
	}
}
