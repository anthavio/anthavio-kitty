package net.anthavio.wsvc;

import javax.inject.Inject;

import net.anthavio.aspect.Logged;
import net.anthavio.example.messages.PingRequest;
import net.anthavio.example.messages.PingResponse;
import net.anthavio.example.types.ResponseCode;
import net.anthavio.example.types.ResponseHeader;
import net.anthavio.tap.svc.TapService;

import org.springframework.remoting.jaxws.LocalJaxWsServiceFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;


/**
 * @author vanek
 * 
 * TODO prozkoumat {@link LocalJaxWsServiceFactory}
 */
@Endpoint
public class ExampleEndpoint {

	public static final String NS = "http://example.komix.cz/messages";

	@Inject
	private TapService service;

	@Logged
	@PayloadRoot(localPart = "PingRequest", namespace = NS)
	public PingResponse ping(PingRequest ping) {

		//service.findByText("xxx");

		ResponseHeader header = new ResponseHeader(ResponseCode.SUCCESS, "Charaso");
		PingResponse.Data data = new PingResponse.Data(ping.getPingData(), ping.getPingData());
		return new PingResponse(header, data);
	}
}
