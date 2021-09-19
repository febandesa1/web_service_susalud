package pe.com.susalud.service;

import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.ResponseBean;

public interface EnvioMqService {
	ResponseBean sendMqInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean);

	ResponseBean cargarDatosAfiliado(AfiliadoRequestPayloadBean afiliadoBean);

	ResponseBean enviarDatosAfiliado(AfiliadoRequestPayloadBean afiliadoBean);

	Boolean validarTrama(String trama);
}
