package pe.com.susalud.mappers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import pe.com.susalud.afiliacion.entidad.bean.Afiliacion;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionDataDet;
import pe.com.susalud.beans.RegistraTramaBean;
import pe.com.susalud.beans.ResponseDataBean;
import pe.com.susalud.beans.TramaBean;

public interface AfiliacionMapper {

	ResponseDataBean insertSuSalud(Map<String, Object> params) throws SQLException;

	ResponseDataBean insertSuSaludDetalle(Map<String, Object> params) throws SQLException;

	ArrayList<Afiliacion> procesaAltaAfiliacionSuSalud(Map<String, Object> params) throws SQLException;

	ArrayList<Afiliacion> procesaBajaAfiliacionSuSalud(Map<String, Object> params) throws SQLException;

	ResponseDataBean Ins_tramaX12Param(Map<String, Object> params) throws SQLException;
	
	ArrayList<AfiliacionDataDet> selectListarTrama(Map<String, Object> params) throws SQLException;
	
	ResponseDataBean registrarRespuestaMq(Map<String, Object> params) throws SQLException;
	
	ResponseDataBean registrarTramax12n(Map<String, Object> params) throws SQLException;
	
	TramaBean selectTramax12n(Map<String, Object> params) throws SQLException;


}
