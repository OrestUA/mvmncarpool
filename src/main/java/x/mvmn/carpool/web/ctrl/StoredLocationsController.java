package x.mvmn.carpool.web.ctrl;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.StoredLocation;
import x.mvmn.carpool.model.User;
import x.mvmn.carpool.service.persistence.StoredLocationRepository;
import x.mvmn.carpool.web.dto.GenericResultDTO;
import x.mvmn.carpool.web.dto.StoredLocationDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class StoredLocationsController {

	@Autowired
	StoredLocationRepository storedLocationRepository;

	@RequestMapping(path = "/api/storedLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<StoredLocationDTO> listStoredLocations(Authentication auth) {
		return storedLocationRepository.findByUser(UserUtil.getCurrentUser(auth)).stream().map(StoredLocationDTO::fromStoredLocation)
				.collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/storedLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO upsertStoredLocation(Authentication auth, @RequestBody StoredLocationDTO storedLocationDto, HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		User currentUser = UserUtil.getCurrentUser(auth);

		StoredLocation storedLocation;
		if (storedLocationDto.getId() > 0) {
			storedLocation = storedLocationRepository.findOne(storedLocationDto.getId());
		} else {
			storedLocation = new StoredLocation();
			storedLocation.setUser(currentUser);
		}
		if (storedLocation != null) {
			if (storedLocation.getUser().getId() == currentUser.getId()) {
				storedLocation.setName(storedLocationDto.getName());
				storedLocation.setDescription(storedLocationDto.getDescription());
				storedLocation.setLat(storedLocationDto.getLat());
				storedLocation.setLon(storedLocationDto.getLon());
				storedLocationRepository.save(storedLocation);

				result.success = true;
				result.message = "ok";
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "Can not update stored location that belongs to other user";
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Stored location not found by given ID";
		}

		return result;
	}

	@RequestMapping(path = "/api/storedLocation/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public GenericResultDTO updateStoredLocation(Authentication auth, @PathVariable(name = "id", required = true) int id, HttpServletResponse response) {
		GenericResultDTO result = new GenericResultDTO();
		result.success = false;

		int currentUserId = UserUtil.getCurrentUser(auth).getId();

		StoredLocation storedLocation = storedLocationRepository.findOne(id);
		if (storedLocation != null) {
			if (storedLocation.getUser().getId() == currentUserId) {
				storedLocationRepository.delete(storedLocation);

				result.success = true;
				result.message = "ok";
			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				result.message = "Can not delete stored location that belongs to other user";
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			result.message = "Stored location not found by given ID";
		}

		return result;
	}

}
