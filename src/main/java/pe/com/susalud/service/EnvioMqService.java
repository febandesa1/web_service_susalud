package pe.com.susalud.service;

import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.ResponseBean;
import pe.com.susalud.beans.ResponseMqBean;

public interface EnvioMqService {

	ResponseMqBean sendMqInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean);
	
	ResponseMqBean generaTramaInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean);
	
	ResponseMqBean enviaTramaInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean);
	
	ResponseBean sendMqInfoAfiliadoTest(AfiliadoRequestPayloadBean afiliadoBean);

}
