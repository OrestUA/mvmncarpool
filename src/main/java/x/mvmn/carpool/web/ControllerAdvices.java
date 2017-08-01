package x.mvmn.carpool.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.RedirectView;

import com.savoirtech.logging.slf4j.json.LoggerFactory;
import com.savoirtech.logging.slf4j.json.logger.Logger;

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
	public RedirectView handleAccessDenied() {
		return new RedirectView(webHelperService.getBaseUrl() + "/signin");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGenericError(Exception e, Model model) {
		LOGGER.error().exception("Unhandled exception occurred", e);
		model.addAttribute("error", e);
		return "internalerror";
	}
}
