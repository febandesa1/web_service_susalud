package pe.com.susalud.dao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import pe.com.susalud.dao.EnvioMqDao;

@Repository
public class EnvioMqDaoImpl implements EnvioMqDao {
	private static final Logger LOGGER = LogManager.getLogger(EnvioMqDaoImpl.class);

	@Override
	public void consultaInformacionAfiliado() {
		try {
			
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
		
	}

}
