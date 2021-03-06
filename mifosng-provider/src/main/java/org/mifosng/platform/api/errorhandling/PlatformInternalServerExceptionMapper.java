package org.mifosng.platform.api.errorhandling;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.mifosng.platform.api.data.ApiGlobalErrorResponse;
import org.mifosng.platform.exceptions.PlatformInternalServerException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * An {@link ExceptionMapper} to map {@link PlatformInternalServerException} thrown by platform into a HTTP API friendly format.
 * 
 * The {@link PlatformInternalServerException} is thrown when an api call results in unexpected server side exceptions.
 */
@Provider
@Component
@Scope("singleton")
public class PlatformInternalServerExceptionMapper implements ExceptionMapper<PlatformInternalServerException> {

	@Override
	public Response toResponse(PlatformInternalServerException exception) {
		
		ApiGlobalErrorResponse notFoundErrorResponse = ApiGlobalErrorResponse.notFound(exception.getGlobalisationMessageCode(), exception.getDefaultUserMessage(), exception.getDefaultUserMessageArgs());
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(notFoundErrorResponse).build();
	}
}