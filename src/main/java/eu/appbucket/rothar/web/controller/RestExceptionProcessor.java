package eu.appbucket.rothar.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import eu.appbucket.rothar.core.service.exception.ServiceException;
import eu.appbucket.rothar.web.domain.exception.ErrorInfo;

@ControllerAdvice
public class RestExceptionProcessor {
	
	@ExceptionHandler(ServiceException.class)
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorInfo serviceException(HttpServletRequest req, ServiceException ex) {
		ErrorInfo error = new ErrorInfo();
		error.setUrl(req.getRequestURL().toString());
        error.setClientMessage(ex.getMessage());
        error.setDeveloperMessage(ex.getCause() != null ? ex.getCause().getMessage() : "");
        return error;
    }
}
