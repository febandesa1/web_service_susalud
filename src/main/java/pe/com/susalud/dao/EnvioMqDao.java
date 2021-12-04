package pe.com.susalud.dao;

import java.util.ArrayList;

import pe.com.susalud.afiliacion.entidad.bean.Afiliacion;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionData;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionDataDet;
import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.RegistraTramaBean;
import pe.com.susalud.beans.ResponseDataBean;
import pe.com.susalud.beans.ResponseMqBean;
import pe.com.susalud.beans.TramaBean;

public interface EnvioMqDao {

	void consultaInformacionAfiliado();

	ResponseDataBean insertarTbEnvioSuSalud(AfiliadoRequestPayloadBean afiliadoBean, AfiliacionData afiliacion);

	ResponseDataBean insertarTbEnvioSuSaludDetalle(AfiliadoRequestPayloadBean afiliadoBean, String codEnvioSusalud);
	
	ArrayList<Afiliacion> listAfiliadosAlta(String codEnvioSalud);
	
	ArrayList<Afiliacion> listAfiliadosBaja(String codEnvioSalud);
	
	String Ins_tramaX12Param(String susaludTrama, String id, String codAfi);
	
	ArrayList<AfiliacionDataDet> ListarTrama(String codEnvioSalud);
	
	ResponseDataBean registrarRespuesta(ResponseMqBean respuestaMq);
	
	ResponseDataBean registrarTramax12n(RegistraTramaBean tramaBean);
	
	TramaBean selectTramax12n(String idSolicitud);

}
