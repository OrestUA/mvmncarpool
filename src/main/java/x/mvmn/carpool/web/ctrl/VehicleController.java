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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import x.mvmn.carpool.model.Vehicle;
import x.mvmn.carpool.service.persistence.VehicleRepository;
import x.mvmn.carpool.web.dto.VehicleDTO;
import x.mvmn.util.web.auth.UserUtil;

@RestController
public class VehicleController {

	// private static final Logger LOGGER = LoggerFactory.getLogger(VehicleController.class);

	@Autowired
	VehicleRepository vehicleRepository;

	@RequestMapping(path = "/api/vehicles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody List<VehicleDTO> listMyVehicles(Authentication auth) {
		return vehicleRepository.findByOwner(UserUtil.getCurrentUser(auth)).stream().map(VehicleDTO::fromVehicleIgnoreUser).collect(Collectors.toList());
	}

	@RequestMapping(path = "/api/vehicles/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody void deleteVehicle(@PathVariable(name = "id", required = true) int id, HttpServletResponse response) {
		if (vehicleRepository.deleteById(id) > 0) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@RequestMapping(path = "/api/vehicles", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody void updateVehicle(@RequestBody VehicleDTO vehicleDto, HttpServletResponse response) {
		Vehicle vehicle = null;
		if (vehicleDto.getId() > 0) {
			vehicle = vehicleRepository.findOne(vehicleDto.getId());
		}
		if (vehicle != null) {
			vehicle.setName(vehicleDto.getName());
			vehicle.setPlateNumber(vehicleDto.getPlateNumber());
			vehicle.setDescription(vehicleDto.getDescription());
			vehicle.setPassengerSeats(vehicleDto.getPassengerSeats());
			vehicleRepository.save(vehicle);

			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@RequestMapping(path = "/api/vehicles", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_USER')")
	public @ResponseBody VehicleDTO addVehicle(Authentication auth, @RequestBody VehicleDTO vehicleDto, HttpServletResponse response) {
		Vehicle vehicle = new Vehicle();
		vehicle.setOwner(UserUtil.getCurrentUser(auth));
		vehicle.setName(vehicleDto.getName());
		vehicle.setPlateNumber(vehicleDto.getPlateNumber());
		vehicle.setDescription(vehicleDto.getDescription());
		vehicle.setPassengerSeats(vehicleDto.getPassengerSeats());
		return VehicleDTO.fromVehicleIgnoreUser(vehicleRepository.save(vehicle));
	}
}
