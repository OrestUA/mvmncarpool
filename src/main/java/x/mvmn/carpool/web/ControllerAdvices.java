package x.mvmn.carpool.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import x.mvmn.carpool.web.service.WebHelperService;

@ControllerAdvice
public class ControllerAdvices {

	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAdvices.class);

	@Autowired
	WebHelperService webHelperService;

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleResourceNotFoundException(Model model, Authentication auth) {
		webHelperService.populateCommonModelData(model, auth);
		return "notfound";
	}

	@ExceptionHandler(AccessDeniedException.class)
	public void handleAccessDenied(HttpServletRequest req, HttpServletResponse resp) {
		if (req.getRequestURI().startsWith("/api") || req.getRequestURI().startsWith(webHelperService.getBaseUrl() + "/api")) {
			resp.setContentType("application/json");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			resp.setHeader("Location", webHelperService.getBaseUrl() + "/signin");
			resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		}
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public void handleMethodNotSupported(HttpServletRequest req, HttpRequestMethodNotSupportedException exception) {
		LOGGER.debug(req.getRequestURI() + " " + exception.getMessage());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleGenericError(Exception e) {
		ModelAndView mav = new ModelAndView("internalerror");
		LOGGER.error("Unhandled exception occurred", e);
		mav.addObject("error", e);
		return mav;
	}
}
