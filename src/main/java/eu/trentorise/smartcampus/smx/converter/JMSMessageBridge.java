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

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.trentorise.smartcampus.smx.converter.utils.UtilityBelt;

public class JMSMessageBridge {
	private static final transient Log logger = LogFactory.getLog(JMSMessageBridge.class);

	@EndpointInject
	private ProducerTemplate producer;
	private String serviceEndpoint;

	public Map<String, Object> forwardMessage(Map<String, Object> message) {
		if (logger.isInfoEnabled()) {

			logger.info("Message " + message.toString() + " to forward... ---");
		}

		/*
		try {

			Random randomGenerator = new Random();
			int sleepingInterval = 250 + randomGenerator.nextInt(500);

			if (logger.isDebugEnabled()) {

				logger.debug("Sleeping for " + sleepingInterval + " milliseconds... ---");
			}
			Thread.sleep(sleepingInterval);

			if (logger.isDebugEnabled()) {

				logger.debug("Waking up after " + sleepingInterval + " milliseconds... ---");
			}
		} catch (InterruptedException e) {

			if (logger.isErrorEnabled()) {

				logger.error("Problems in putting in sleep ---", e);
			}
		}
		*/

		Object responseFromSent;
		try {
			responseFromSent = this.producer.sendBody(this.serviceEndpoint, ExchangePattern.InOut, message);
		} catch (CamelExecutionException e) {
			logger.error("Error forwarding: "+e.getExchange().getException().getMessage());
			e.printStackTrace();
			Map<String, Object> result = new HashMap<String, Object>();
			result.put(UtilityBelt.SECURITY_TOKEN, message.get(UtilityBelt.SECURITY_TOKEN));
			result.put(UtilityBelt.RESPONSE_TYPE, "ERROR");
			result.put(UtilityBelt.RESPONSE_MESSAGE, message.get(UtilityBelt.RESPONSE_MESSAGE));
			result.put(UtilityBelt.SUBSCRIBER_ID, message.get(UtilityBelt.SUBSCRIBER_ID));
			result.put(UtilityBelt.SUBSCRIPTION_ID, message.get(UtilityBelt.SUBSCRIPTION_ID));
			result.put(UtilityBelt.CONVERSATION_ID, message.get(UtilityBelt.CONVERSATION_ID));
			return result;
		}
		if (responseFromSent instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> response = (Map<String, Object>) responseFromSent;

			if (logger.isInfoEnabled()) {

				logger.info("Recieved " + response.toString() + ".");
			}
			return response;
		} else {

			if (logger.isErrorEnabled()) {

				logger.error("Response form body sent to producer isn't a Map...");
			}
		}
		return null;
	}

	public String getServiceEndpoint() {

		return this.serviceEndpoint;
	}

	public void setServiceEndpoint(String serviceEndpoint) {

		this.serviceEndpoint = serviceEndpoint;
	}
}
