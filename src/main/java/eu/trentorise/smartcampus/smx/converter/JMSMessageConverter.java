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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import eu.trentorise.smartcampus.smx.converter.utils.UtilityBelt;

public class JMSMessageConverter implements MessageConverter {
	private static final transient Log logger = LogFactory.getLog(JMSMessageConverter.class);

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		Map<String, Object> toReturn = new HashMap<String, Object>(UtilityBelt.messageProperties);
		toReturn.put(UtilityBelt.SECURITY_TOKEN, message.getStringProperty(UtilityBelt.SECURITY_TOKEN));
		toReturn.put(UtilityBelt.RESPONSE_TYPE, message.getStringProperty(UtilityBelt.RESPONSE_TYPE));
		toReturn.put(UtilityBelt.RESPONSE_MESSAGE, message.getStringProperty(UtilityBelt.RESPONSE_MESSAGE));
		toReturn.put(UtilityBelt.SUBSCRIBER_ID, message.getStringProperty(UtilityBelt.SUBSCRIBER_ID));
		toReturn.put(UtilityBelt.SUBSCRIPTION_ID, message.getStringProperty(UtilityBelt.SUBSCRIPTION_ID));
		toReturn.put(UtilityBelt.CONVERSATION_ID, message.getStringProperty(UtilityBelt.CONVERSATION_ID));
		toReturn.put(UtilityBelt.CONTENT, UtilityBelt.extractByteArrayFromMessage(message));
		return toReturn;
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		Message message = null;
		String securityToken = null;
		String responseType = null;
		String responseMessage = null;
		String subscriberId = null;
		String subscriptionId = null;
		String conversationId = null;

		if (logger.isDebugEnabled()) {

			logger.debug("Called toMessage method for" + object.toString() + " in session " + session.toString() + "... ---");
		}

		if (object instanceof Map<?, ?>) {
			if (logger.isDebugEnabled()) {
				logger.debug("Object is a Map, going to get: " +
						UtilityBelt.SECURITY_TOKEN + ", " +
						UtilityBelt.RESPONSE_TYPE + ", " +
						UtilityBelt.RESPONSE_MESSAGE + ", " +
						UtilityBelt.SUBSCRIBER_ID + ", " +
						UtilityBelt.SUBSCRIPTION_ID + ", " +
						UtilityBelt.CONVERSATION_ID + " and " +
						UtilityBelt.CONTENT + "... ---");
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) object;

			StreamMessage streamMessage = session.createStreamMessage();
			securityToken = (String) map.get(UtilityBelt.SECURITY_TOKEN);
			responseType = (String) map.get(UtilityBelt.RESPONSE_TYPE);
			responseMessage = (String) map.get(UtilityBelt.RESPONSE_MESSAGE);
			subscriberId = (String) map.get(UtilityBelt.SUBSCRIBER_ID);
			subscriptionId = (String) map.get(UtilityBelt.SUBSCRIPTION_ID);
			conversationId = (String) map.get(UtilityBelt.CONVERSATION_ID);

			byte[] content = (byte[]) map.get(UtilityBelt.CONTENT);
			if (content != null) {
				streamMessage.writeBytes(content);
			}

			if (logger.isDebugEnabled()) {
				logger.debug(UtilityBelt.SECURITY_TOKEN + ", " +
						UtilityBelt.RESPONSE_TYPE + ", " +
						UtilityBelt.RESPONSE_MESSAGE + ", " +
						UtilityBelt.SUBSCRIBER_ID + ", " +
						UtilityBelt.SUBSCRIPTION_ID + ", " +
						UtilityBelt.CONVERSATION_ID + " and " +
						UtilityBelt.CONTENT +
						" got. Assigning to message... ---");
			}
			message = streamMessage;
		} else {

			throw new MessageConversionException("The object is not StreamMessage, BytesMessage or ObjectMessage.");
		}

		if (securityToken != null) {

			message.setStringProperty("securityToken", securityToken);
		}

		if (responseType != null) {

			message.setStringProperty("responseType", responseType);
		}

		if (responseMessage != null) {

			message.setStringProperty("responseMessage", responseMessage);
		}

		if (subscriberId != null) {

			message.setStringProperty("subscriberId", subscriberId);
		}

		if (subscriptionId != null) {

			message.setStringProperty("subscriptionId", subscriptionId);
		}

		if (conversationId != null) {

			message.setStringProperty("conversationId", conversationId);
		}

		if (logger.isDebugEnabled()) {

			logger.debug("Message " + message.toString() + " constructed... ---");
		}

		return message;
	}
}
