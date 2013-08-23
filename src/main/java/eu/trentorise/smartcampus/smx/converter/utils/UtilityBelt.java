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
package eu.trentorise.smartcampus.smx.converter.utils;

import java.io.ByteArrayOutputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.StreamMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class UtilityBelt {
	public static final int messageProperties = 7;
	public static final String SECURITY_TOKEN = "securityToken",
			RESPONSE_TYPE = "responseType",
			RESPONSE_MESSAGE = "responseMessage",
			SUBSCRIBER_ID = "subscriberId",
			SUBSCRIPTION_ID = "subscriptionId",
			CONVERSATION_ID = "conversationId",
			CONTENT = "content";

	private static final int BUFFER_SIZE = 4096;
	private static final transient Log logger = LogFactory.getLog(UtilityBelt.class);

	public static byte[] extractByteArrayFromMessage(Message message) {
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

	private static int readBytes(byte[] buffer, Message message) {
		try {

			if ((message instanceof BytesMessage)) {

				return ((BytesMessage) message).readBytes(buffer);
			} else if ((message instanceof StreamMessage)) {

				return ((StreamMessage) message).readBytes(buffer);
			}
		} catch (JMSException e) {

			if (logger.isErrorEnabled()) {

				logger.error("Error in reading bytes from message... ---", e);
			}
		}
		return -1;
	}
}
