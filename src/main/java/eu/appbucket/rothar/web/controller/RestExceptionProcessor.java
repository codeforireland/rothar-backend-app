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
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo smartphoneNotFound(HttpServletRequest req, ServiceException ex) {
        String errorURL = req.getRequestURL().toString();
        return new ErrorInfo(errorURL, ex.getMessage());
    }
}
