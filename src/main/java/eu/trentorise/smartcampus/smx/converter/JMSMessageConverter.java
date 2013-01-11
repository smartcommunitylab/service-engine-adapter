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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class JMSMessageConverter implements MessageConverter {
	public static final String SECURITY_TOKEN = "securityToken";
	public static final String RESPONSE_TYPE = "responseType";
	public static final String RESPONSE_MESSAGE = "responseMessage";
	public static final String SUBSCRIBER_ID = "subscriberId";
	public static final String SUBSCRIPTION_ID = "subscriptionId";
	public static final String CONVERSATION_ID = "conversationId";
	public static final String CONTENT = "content";
	
	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
//		System.out.println("fromMessage:" + message.getClass().getCanonicalName());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SECURITY_TOKEN, message.getStringProperty(SECURITY_TOKEN));
		map.put(RESPONSE_TYPE, message.getStringProperty(RESPONSE_TYPE));
		map.put(RESPONSE_MESSAGE, message.getStringProperty(RESPONSE_MESSAGE));
		map.put(SUBSCRIBER_ID, message.getStringProperty(SUBSCRIBER_ID));
		map.put(SUBSCRIPTION_ID, message.getStringProperty(SUBSCRIPTION_ID));
		map.put(CONVERSATION_ID, message.getStringProperty(CONVERSATION_ID));
		map.put(CONTENT, extractByteArrayFromMessage(message));
		return map;
	}

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
//		System.out.println("toMessage:" + object.getClass().getCanonicalName());
		Message message = null;
		String securityToken = null;
		String responseType = null;
		String responseMessage = null;
		String subscriberId = null;
		String subscriptionId = null;
		String conversationId = null;
		
		if (object instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) object;
			StreamMessage streamMessage = session.createStreamMessage();
			securityToken = (String) map.get(SECURITY_TOKEN);
			responseType = (String) map.get(RESPONSE_TYPE);
			responseMessage = (String) map.get(RESPONSE_MESSAGE);
			subscriberId = (String) map.get(SUBSCRIBER_ID);
			subscriptionId = (String) map.get(SUBSCRIPTION_ID);
			conversationId = (String) map.get(CONVERSATION_ID);
			
			byte[] bytes = (byte[]) map.get(CONTENT);
			streamMessage.writeBytes(bytes);
			message = streamMessage;
		} else {
			throw new MessageConversionException("The object is not StreamMessage, BytesMessage or ObjectMessage.");
		}
		
		if(securityToken != null)
			message.setStringProperty(SECURITY_TOKEN, securityToken);
		if(responseType != null)
			message.setStringProperty(RESPONSE_TYPE, responseType);
		if(responseMessage != null)
			message.setStringProperty(RESPONSE_MESSAGE, responseMessage);
		if(subscriberId != null)
			message.setStringProperty(SUBSCRIBER_ID, subscriberId);
		if(subscriptionId != null)
			message.setStringProperty(SUBSCRIPTION_ID, subscriptionId);
		if(conversationId != null)
			message.setStringProperty(CONVERSATION_ID, conversationId);
		
		return message;
	}
	
	public byte[] extractByteArrayFromMessage(Message message) throws JMSException {
		int BUFFER_SIZE = 4096; // (int)message.getBodyLength() + 1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		int bufferCount = -1;
		while ((bufferCount = readBytes(buffer, message)) >= 0) {
			baos.write(buffer, 0, bufferCount);
			if (bufferCount < BUFFER_SIZE) {
				break;
			}
		}
		return baos.toByteArray();
	}
	
	private static int readBytes(byte[] buffer, Message message)  {
		try {
			if (message instanceof BytesMessage) {
				return ((BytesMessage) message).readBytes(buffer);
			}
			if (message instanceof StreamMessage) {
				return ((StreamMessage) message).readBytes(buffer);
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}

}
