package pe.com.susalud.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.ResponseBean;
import pe.com.susalud.service.EnvioMqService;

@RestController
@RequestMapping("/afiliado")
public class EnvioMqController {

	private static final Logger LOGGER = LogManager.getLogger(EnvioMqController.class);

	@Autowired
	private EnvioMqService envioMqService;

	@GetMapping
	@RequestMapping("/info")
	public ResponseBean sendMqInfoAfiliado(

			@RequestParam(defaultValue = "", name = "IdRemitente") String IdRemitente,
			@RequestParam(defaultValue = "", name = "FeTransaccion") String FeTransaccion,
			@RequestParam(defaultValue = "", name = "HoTransaccion") String HoTransaccion

	) {
		ResponseBean response = null;
		try {
			AfiliadoRequestPayloadBean afiliadoBean = new AfiliadoRequestPayloadBean();
			response = envioMqService.sendMqInfoAfiliado(afiliadoBean);

		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}

}
