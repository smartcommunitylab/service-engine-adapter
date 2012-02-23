package eu.trentorise.smartcampus.smx.converter;

import java.io.ByteArrayOutputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class SBOMessageConverter implements MessageConverter {

	@Override
	public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
		Message message = null;
		String securityToken = null;

		try {
			if (object instanceof StreamMessage) {
				StreamMessage msg = (StreamMessage) object;
				StreamMessage streamMessage = session.createStreamMessage();
				securityToken = msg.getStringProperty("securityToken");
				byte[] bytes = extractByteArrayFromStreamMessage(msg);
				streamMessage.writeBytes(bytes);
				message = streamMessage;
			} else if (object instanceof BytesMessage) {
				BytesMessage msg = (BytesMessage) object;
				BytesMessage bytesMessage = session.createBytesMessage();
				securityToken = msg.getStringProperty("securityToken");
				byte[] bytes = extractByteArrayFromBytesMessage(msg);
				bytesMessage.writeBytes(bytes);
				message = bytesMessage;
			} else if (object instanceof ObjectMessage) {
				ObjectMessage msg = (ObjectMessage) object;
				ObjectMessage objectMessage = session.createObjectMessage();
				securityToken = msg.getStringProperty("securityToken");
				byte[] bytes = (byte[]) msg.getObject();
				objectMessage.setObject(bytes);
				message = objectMessage;
			} else {
				throw new MessageConversionException("The object is not StreamMessage, BytesMessage or ObjectMessage.");
			}
		} catch (Exception e) {
			throw new MessageConversionException(e.getMessage());
		}

		message.setStringProperty("securityToken", securityToken);
		return message;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException, MessageConversionException {
		Object contentObject = null;
		try {
			if (message instanceof StreamMessage) {
				contentObject = extractByteArrayFromStreamMessage((StreamMessage) message);
			} else if (message instanceof BytesMessage) {
				contentObject = extractByteArrayFromBytesMessage((BytesMessage) message);
			} else if (message instanceof ObjectMessage) {
				contentObject = ((ObjectMessage) message).getObject();
			} else {
				throw new MessageConversionException("The object is not StreamMessage, BytesMessage or ObjectMessage.");
			}
		} catch (Exception e) {
			throw new MessageConversionException(e.getMessage());
		}
		return contentObject;
	}

	public byte[] extractByteArrayFromStreamMessage(StreamMessage message) throws JMSException {
		int BUFFER_SIZE = 4096; // (int)message.getBodyLength() + 1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		int bufferCount = -1;
		while ((bufferCount = message.readBytes(buffer)) >= 0) {
			baos.write(buffer, 0, bufferCount);
			if (bufferCount < BUFFER_SIZE) {
				break;
			}
		}
		return baos.toByteArray();
	}

	public byte[] extractByteArrayFromBytesMessage(BytesMessage message) throws JMSException {
		int BUFFER_SIZE = 4096; // (int)message.getBodyLength() + 1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
		byte[] buffer = new byte[BUFFER_SIZE];
		int bufferCount = -1;
		while ((bufferCount = message.readBytes(buffer)) >= 0) {
			baos.write(buffer, 0, bufferCount);
			if (bufferCount < BUFFER_SIZE) {
				break;
			}
		}
		return baos.toByteArray();
	}

}
