package pe.com.susalud.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.ResponseBean;
import pe.com.susalud.core.beans.Trama271Bean;
import pe.com.susalud.service.Afiliacion217Services;
import pe.com.susalud.service.Afiliacion997Services;
import pe.com.susalud.service.EnvioMqService;
import pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate;

@Service
public class EnvioMqServiceImpl implements EnvioMqService {
	private static final Logger LOGGER = LogManager.getLogger(EnvioMqServiceImpl.class);

	@Autowired
	private Afiliacion217Services afiliacion217Services;

	@Autowired
	private Afiliacion997Services afiliacion997Services;

	@Override
	public ResponseBean sendMqInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseBean response = new ResponseBean();
		try {

		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}

	@Override
	public Boolean validarTrama(String trama) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseBean cargarDatosAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseBean response = new ResponseBean();
		try {

			Trama271Bean bean = new Trama271Bean();

			In271RegafiUpdate dato;

			// ACTION_CARGAR
			if (bean != null) {
				// 271
				dato = bean.getDato();

			} else {
				LOGGER.info("Ocurrió un error al parsear la trama 271.");
			}

		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}

	@Override
	public ResponseBean enviarDatosAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseBean response = new ResponseBean();
		try {

			// ACTION_ENVIAR

		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}

}
