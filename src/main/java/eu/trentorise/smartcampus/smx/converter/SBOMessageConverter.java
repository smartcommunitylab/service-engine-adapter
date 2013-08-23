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

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import eu.trentorise.smartcampus.smx.converter.utils.UtilityBelt;

public class SBOMessageConverter implements MessageConverter {
	private static final transient Log logger = LogFactory.getLog(SBOMessageConverter.class);

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		Message toReturn = null;
		String securityToken = null;

		if (logger.isInfoEnabled()) {

			logger.info("Called toMessage method for" + object.toString() + " in session " + session.toString() + "... ---");
		}

		try {

			if (object instanceof StreamMessage) {

				StreamMessage aStreamMessage = (StreamMessage) object;
				StreamMessage streamMessage = session.createStreamMessage();
				securityToken = aStreamMessage.getStringProperty(UtilityBelt.SECURITY_TOKEN);
				byte[] bytes = extractBytesFromMessage(aStreamMessage);
				streamMessage.writeBytes(bytes);
				toReturn = streamMessage;
				Destination replyTo = aStreamMessage.getJMSReplyTo();

				if (replyTo != null &&
						logger.isInfoEnabled()) {

					logger.info("Reply to StreamMessage null... ---");
				}
			} else if (object instanceof BytesMessage) {

				BytesMessage aBytesMessage = (BytesMessage) object;
				BytesMessage bytesMessage = session.createBytesMessage();
				securityToken = aBytesMessage.getStringProperty(UtilityBelt.SECURITY_TOKEN);
				byte[] bytes = extractBytesFromMessage(aBytesMessage);
				bytesMessage.writeBytes(bytes);
				toReturn = bytesMessage;
			} else if ((object instanceof ObjectMessage)) {

				ObjectMessage anObjectMessage = (ObjectMessage) object;
				ObjectMessage objectMessage = session.createObjectMessage();
				securityToken = anObjectMessage.getStringProperty(UtilityBelt.SECURITY_TOKEN);
				byte[] bytes = (byte[]) anObjectMessage.getObject();
				if (bytes != null && logger.isInfoEnabled()) {

					logger.info("Bytes for message " + anObjectMessage.toString() + " equals to " + bytes.length + "... ---");
				} else if (logger.isInfoEnabled()) {

					logger.info("Bytes for message " + anObjectMessage.toString() + " are null... ---");
				}

				objectMessage.setObject(bytes);
				toReturn = objectMessage;
			} else {
				if (logger.isErrorEnabled()) {

					logger.error(object.toString() + " isn't a StreamMessage, BytesMessage or ObjectMessage... ---");
				}
				throw new MessageConversionException("The object " + object.toString() + " is not StreamMessage, BytesMessage or ObjectMessage... ---");
			}
		} catch (JMSException e) {

			if (logger.isErrorEnabled()) {

				logger.error("Problems in conversion of message... ---", e);
			}
			throw new MessageConversionException(e.getMessage());
		}

		toReturn.setStringProperty(UtilityBelt.SECURITY_TOKEN, securityToken);

		if (logger.isInfoEnabled()) {

			logger.info("Message " + toReturn.toString() + " constructed... ---");
		}
		return toReturn;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		Object contentObject = null;
		if (logger.isInfoEnabled()) {

			logger.info("Called fromMessage method for" + message.toString() + "... ---");
		}

		if (message instanceof StreamMessage ||
				message instanceof BytesMessage) {

			contentObject = UtilityBelt.extractByteArrayFromMessage(message);
		} else if (message instanceof ObjectMessage) {

			try {

				contentObject = ((ObjectMessage) message).getObject();
			} catch (Exception e) {

				if (logger.isErrorEnabled()) {

					logger.error("Problems in getting content from message " + message.toString() + "... ---", e);
				}
				throw new MessageConversionException(e.getMessage());
			}
		} else {
			if (logger.isErrorEnabled()) {

				logger.error(message.toString() + " isn't a StreamMessage, BytesMessage or ObjectMessage... ---");
			}
			throw new MessageConversionException("The message " + message.toString() + " is not StreamMessage, BytesMessage or ObjectMessage... ---");
		}

		if (logger.isInfoEnabled()) {

			logger.info("Content object " + contentObject.toString() + " created... ---");
		}
		return contentObject;
	}

	private byte[] extractBytesFromMessage(Message aMessage) {
		byte[] bytes = UtilityBelt.extractByteArrayFromMessage(aMessage);
		if (bytes != null && logger.isInfoEnabled()) {

			logger.info("Bytes for message " + aMessage.toString() + " equals to " + bytes.length + "... ---");
		} else if (logger.isInfoEnabled()) {

			logger.info("Bytes for message " + aMessage.toString() + " are null... ---");
		}

		return bytes;
	}
}
