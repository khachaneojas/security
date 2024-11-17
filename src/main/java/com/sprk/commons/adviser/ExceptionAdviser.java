package com.sprk.commons.adviser;

import com.fasterxml.jackson.databind.JsonMappingException;

import com.sprk.commons.dto.APIResponse;
import com.sprk.commons.exception.BaseException;
import com.sprk.commons.lang.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.ConnectException;

import java.sql.SQLException;
import java.util.List;



@RestControllerAdvice
public class ExceptionAdviser {

	@ExceptionHandler(value = BaseException.class)
	public ResponseEntity<APIResponse<Object>> handleCustomExceptions(BaseException exception) {
		return ResponseEntity
				.status(exception.getStatus())
				.body(exception.getResponse());
	}



	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException exception) {
		return ResponseEntity
				.status(exception.getStatusCode())
				.contentType(MediaType.APPLICATION_JSON)
				.body(exception.getResponseBodyAsString());
	}



	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public APIResponse<Void> handleUnhandledExceptions(Exception exception) {
		Logger.log(exception.getClass(), ExceptionUtils.getStackTrace(exception), LogLevel.ERROR);
		return APIResponse.<Void>builder().error("Uh-oh! Something went wrong. :(").build();
	}



	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public APIResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		String errorMessage = exception.getFieldErrors().isEmpty()
				? exception.getMessage()
				: exception.getFieldErrors().get(0).getDefaultMessage();
		return APIResponse.<Void>builder().error(errorMessage).build();
	}



	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public APIResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
		StringBuilder errorMessageBuilder = new StringBuilder("Uh-oh! Request cannot be processed due to an invalid payload.");
		Throwable cause = exception.getCause();

		if (cause instanceof JsonMappingException jsonMappingException) {
            List<JsonMappingException.Reference> path = jsonMappingException.getPath();
			if (null == path || path.isEmpty() || null == path.get(0) || null == path.get(0).getFieldName()) {
				errorMessageBuilder
						.append(" There is an issue with parsing the payload.");
			} else {
				errorMessageBuilder
						.append(" There is an issue with parsing the parameter named `")
						.append(path.get(0).getFieldName())
						.append("`.");
			}
		} else {
			errorMessageBuilder
					.append(" Please check your request payload and try again.");
		}

		return APIResponse.<Void>builder().error(errorMessageBuilder.toString()).build();
	}



	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public APIResponse<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
		long maxUploadSize = exception.getMaxUploadSize();
		StringBuilder errorMessageBuilder = new StringBuilder("Uh-oh! The file is too big.");
		if (maxUploadSize > 0L) {
			String readableSIPrefixes = FileUtils.byteCountToDisplaySize(maxUploadSize);
			errorMessageBuilder
					.append(" (You can only upload files smaller than ")
					.append(readableSIPrefixes)
					.append(")");
		}

		return APIResponse.<Void>builder().error(errorMessageBuilder.toString()).build();
	}



	@ExceptionHandler(value = NoResourceFoundException.class)
	public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}



	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = JpaSystemException.class)
	public APIResponse<Void> handleJpaSystemException(JpaSystemException exception) {
		Throwable rootCause = exception.getRootCause();
		if (rootCause instanceof SQLException) {
			return defaultResponse(rootCause);
		} else {
			return defaultResponse(exception);
		}
	}
	


	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MultipartException.class)
	public APIResponse<Void> handleMultipartException(MultipartException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public APIResponse<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MissingPathVariableException.class)
	public APIResponse<Void> handleMissingPathVariableException(MissingPathVariableException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(MissingRequestHeaderException.class)
	public APIResponse<Void> handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.BAD_GATEWAY)
	@ExceptionHandler(ConnectException.class)
	public APIResponse<Void> handleCommunicationsException(ConnectException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public APIResponse<Void> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
		return defaultResponse(exception);
	}

	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public APIResponse<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		return defaultResponse(exception);
	}



	private APIResponse<Void> defaultResponse(Throwable throwable) {
		return APIResponse.<Void>builder().error(throwable.getMessage()).build();
	}
}
