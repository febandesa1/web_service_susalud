package pe.com.susalud.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.ResponseBean;
import pe.com.susalud.service.EnvioMqService;

@RestController
@RequestMapping("/afiliado-test")
public class EnvioMqControllerTest {
	
	private static final Logger LOGGER = LogManager.getLogger(EnvioMqControllerTest.class);
	
	@Autowired
	private EnvioMqService envioMqService;
	
	@GetMapping(value = "/alta-test", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseBean sendMqInfoAfiliado(

			@RequestParam(defaultValue = "", name = "codAfiliado") String codAfiliado,
			@RequestParam(defaultValue = "", name = "descAfiliado") String descAfiliado,
			@RequestParam(defaultValue = "", name = "dni") String dni,
			@RequestParam(defaultValue = "", name = "tipoAfiliacion") String tipoAfiliacion,
			@RequestParam(defaultValue = "", name = "codMotivoAfiliacion") String codMotivoAfiliacion,
			@RequestParam(defaultValue = "", name = "descMotivoAfiliacion") String descMotivoAfiliacion,
			@RequestParam(defaultValue = "", name = "doc") String doc

	) {
		ResponseBean response = null;
		LOGGER.info("sendMqInfoAfiliado INI");
		try {
			AfiliadoRequestPayloadBean afiliadoBean = new AfiliadoRequestPayloadBean();
			afiliadoBean.setCodAfiliado(codAfiliado);
			afiliadoBean.setDescAfiliado(descAfiliado);
			afiliadoBean.setDni(dni);
			afiliadoBean.setTipoAfiliacion(tipoAfiliacion);
			afiliadoBean.setCodMotivoAfiliacion(codMotivoAfiliacion);
			afiliadoBean.setDescMotivoAfiliacion(descMotivoAfiliacion);
			afiliadoBean.setDoc(doc);
			response = envioMqService.sendMqInfoAfiliadoTest(afiliadoBean);
			LOGGER.info("sendMqInfoAfiliado FIN");
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}
}
