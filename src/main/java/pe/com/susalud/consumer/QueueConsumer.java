package pe.com.susalud.consumer;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueConsumer implements MessageListener{
	 
	private static final Logger LOGGER = LogManager.getLogger(QueueConsumer.class);
	
	@Override
	public void onMessage(Message message) {
		LOGGER.info("Inside On Message...");
		 
		
	}

}
