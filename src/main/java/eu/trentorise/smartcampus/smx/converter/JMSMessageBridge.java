package eu.trentorise.smartcampus.smx.converter;

import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;

public class JMSMessageBridge {
	private String serviceEndpoint;
	
	@EndpointInject
  ProducerTemplate producer;
  
  @SuppressWarnings("unchecked")
	public Map<String, Object> forwardMessage(Map<String, Object> message) {
//		System.out.println("receiveMessage:");
//		try {
//			Random randomGenerator = new Random();
//			Thread.sleep(250 + randomGenerator.nextInt(500));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		Map<String, Object> response = (Map<String, Object>) producer.sendBody(serviceEndpoint, ExchangePattern.InOut, message);
		return response;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
	}
}
