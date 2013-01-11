/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
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
