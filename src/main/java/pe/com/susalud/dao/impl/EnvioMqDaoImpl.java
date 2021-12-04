package pe.com.susalud.dao.impl;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import pe.com.susalud.afiliacion.entidad.bean.Afiliacion;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionData;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionDataDet;
import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.RegistraTramaBean;
import pe.com.susalud.beans.ResponseDataBean;
import pe.com.susalud.beans.ResponseMqBean;
import pe.com.susalud.beans.TramaBean;
import pe.com.susalud.dao.EnvioMqDao;
import pe.com.susalud.mappers.AfiliacionMapper;

@Repository
public class EnvioMqDaoImpl implements EnvioMqDao {

	private static final Logger LOGGER = LogManager.getLogger(EnvioMqDaoImpl.class);

	@Resource(name = "sqlSession")
	@Qualifier(value = "sqlSession")
	private SqlSessionTemplate sqlSession;

	@Override
	public void consultaInformacionAfiliado() {
		try {

		} catch (Exception ex) {
			LOGGER.error(ex);
		}

	}

	@Override
	public ResponseDataBean insertarTbEnvioSuSalud(AfiliadoRequestPayloadBean afiliadoBean, AfiliacionData afiliacion) {
		// TODO FIX DAO

		// TODO Auto-generated method stub
		/*
		 * CallableStatement cstmt = this.Cn.prepareCall(
		 * "{call SP_INSERT_TB_ENVIO_SUSALUD(?,?,?,?)}"); cstmt.setInt(1,
		 * a.getC_tipo()); cstmt.setString(2, a.getC_c_usuario()); cstmt.setInt(3,
		 * a.getN_i_cantidad()); cstmt.registerOutParameter(4, java.sql.Types.VARCHAR);
		 * graba1 = cstmt.execute(); codEnvioSusalud = cstmt.getString(4);
		 * Constante.codigoEnvioSusalud = codEnvioSusalud;
		 */
		LOGGER.info("insertarTbEnvioSuSalud INI");
		ResponseDataBean response = null;
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("c_tipo", afiliacion.getC_tipo());
			map.put("c_c_usuario", afiliacion.getC_c_usuario());
			map.put("n_i_cantidad", afiliacion.getN_i_cantidad());
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.insertSuSalud(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("codigo " + response.getCodigo());
		LOGGER.info("insertarTbEnvioSuSalud FIN");
		return response;
	}

	@Override
	public ResponseDataBean insertarTbEnvioSuSaludDetalle(AfiliadoRequestPayloadBean afiliadoBean,
			String codEnvioSusalud) {
		// TODO FIX DAO
		// TODO Auto-generated method stub
		/*
		 * CallableStatement cstmt2 = this.Cn.prepareCall(
		 * "{call SP_INSERT_TB_ENVIO_SUSALUD_DET (?,?,?)}"); cstmt2.setString(1,
		 * codEnvioSusalud.trim()); cstmt2.setString(2, afi.getC_c_afiliado());
		 * cstmt2.setString(3, afi.getN_dni()); graba2 = cstmt2.execute();
		 */

		ResponseDataBean response = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("codEnvioSusalud", codEnvioSusalud);
			map.put("c_c_afiliado", afiliadoBean.getCodAfiliado());
			map.put("n_dni", afiliadoBean.getDni());

			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.insertSuSaludDetalle(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;

	}

	@Override
	public ArrayList<Afiliacion> listAfiliadosAlta(String codEnvioSalud) {
        //Prm = this.Cn.prepareCall("{CALL sp_afiliados_lineambatch_prm(" + cod + ")}");
        //System.out.print("SP->sp_afiliados_lineambatch_prm(" + cod + ")\n");

		ArrayList<Afiliacion> afiliados = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("codigo", codEnvioSalud);
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			afiliados = afiliacionMapper.procesaAltaAfiliacionSuSalud(map);
		} catch (SQLException ex) {
			System.out.println("*** ERROR:"+ex.getMessage());
			LOGGER.error(ex);
		} catch (Exception e) {
			System.out.println("*** ERROR:"+e.getMessage());
			LOGGER.error(e.getMessage());
		}
		return afiliados;
	}

	@Override
	public ArrayList<Afiliacion> listAfiliadosBaja(String codEnvioSalud) {
		
		//Prm = this.Cn.prepareCall("{CALL sp_afiliados_lineambatch_prm_bajas(" + cod + ")}");
        //System.out.print("SP->sp_afiliados_lineambatch_prm_bajas(" + cod + ")\n");
		
		ArrayList<Afiliacion> afiliados = null;
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("codigo", codEnvioSalud);
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			afiliados = afiliacionMapper.procesaBajaAfiliacionSuSalud(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return afiliados;
	}

	@Override
	public String Ins_tramaX12Param(String susaludTrama, String id, String codAfi) {

        //Prm = this.Cn.prepareCall("{CALL sp_ins_tramax12_susalud(?,?,?)} ");
        //Prm.setString(1,susaludTrama );
		ResponseDataBean response=null;
		String responseData="";
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("susaludTrama", susaludTrama);
			map.put("id", id);
			map.put("codAfi", codAfi);

			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.Ins_tramaX12Param(map);
			responseData = response.getCodigo();
			//afiliados = afiliacionMapper.procesaBajaAfiliacionSuSalud(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return responseData;
	}

	@Override
	public ArrayList<AfiliacionDataDet> ListarTrama(String codEnvioSalud) {
		ArrayList<AfiliacionDataDet> response=new ArrayList<>();
		try{
			
			//String sql = "select x_trama from TB_TRAMAX12_SUSALUD WHERE C_C_ID = '" + cod + "'";
	        //pstm = this.Cn.prepareStatement(sql);
			Map<String, Object> map = new HashMap<>();
			map.put("codEnvioSalud", codEnvioSalud);
			
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			ArrayList<AfiliacionDataDet> responseTramas= afiliacionMapper.selectListarTrama(map);
			/*
			for(String trama: responseTramas) {
				AfiliacionDataDet data=new AfiliacionDataDet();
				data.setX_trama(trama);
				response.add(data);
			}
			*/
			response = responseTramas;
		}catch(Exception ex) {
			LOGGER.error(ex);	
		}
		return response;
	}

	@Override
	public ResponseDataBean registrarRespuesta(ResponseMqBean respuestaMq) {
		ResponseDataBean response = null;
		try {
			/*
			String sDate1 = respuestaMq.getFeTransaccion();  
			Date date1=new SimpleDateFormat("yyyyMMdd").parse(sDate1);  

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			String strDate = dateFormat.format(date1);  	
			System.out.println("fecha = "+respuestaMq.getFeTransaccion());
			LOGGER.info("fecha "+strDate);
			System.out.println("fecha format = "+strDate);
			*/
			Map<String, Object> map = new HashMap<>();

			map.put("feTransaccion", respuestaMq.getFeTransaccion());
			map.put("hoTransaccion", respuestaMq.getHoTransaccion());
			if (respuestaMq.getIdCorrelativo()!=null) {
				map.put("idCorrelativo", respuestaMq.getIdCorrelativo());
			} else {
				map.put("idCorrelativo", "");	
			}
			
			map.put("idReceptor", respuestaMq.getIdReceptor());
			map.put("idRemitente", respuestaMq.getIdRemitente());
			if (respuestaMq.getIdTransaccion()!=null) {
				map.put("idTransaccion", respuestaMq.getIdTransaccion());
			} else {
				map.put("idTransaccion", "");
			}

			map.put("nuControl", respuestaMq.getNuControl());
			map.put("nuControlST", respuestaMq.getNuControlST());
			
			map.put("idSolicitud",respuestaMq.getIdSolicitud());
		    map.put("codAfiliado",respuestaMq.getCodAfiliado());
		    map.put("xmlEnvio",respuestaMq.getXmlEnvio());
		    map.put("dni",respuestaMq.getDni());
			
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.registrarRespuestaMq(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseDataBean registrarTramax12n(RegistraTramaBean tramaBean) {
		ResponseDataBean response = null;
		try {
			
			Map<String, Object> map = new HashMap<>();
			map.put("idSolicitud", tramaBean.getIdSolicitud());
			map.put("codAfiliado", tramaBean.getCodAfiliado());
			map.put("dni", tramaBean.getDni());
			map.put("x12n", tramaBean.getX12n());
		
			
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.registrarTramax12n(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	@Override
	public TramaBean selectTramax12n(String idSolicitud) {
		TramaBean response=null;
		try {
			
			Map<String, Object> map = new HashMap<>();
			map.put("idSolicitud", idSolicitud);
			
			AfiliacionMapper afiliacionMapper = sqlSession.getMapper(AfiliacionMapper.class);
			response = afiliacionMapper.selectTramax12n(map);
		} catch (SQLException ex) {
			LOGGER.error(ex);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;		
	}

}
