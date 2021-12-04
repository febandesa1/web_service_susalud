package pe.com.susalud.service.impl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.com.susalud.afiliacion.entidad.bean.Afiliacion;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionData;
import pe.com.susalud.afiliacion.entidad.bean.AfiliacionDataDet;
import pe.com.susalud.afiliacion.util.FeTransaccion;
//import pe.com.susalud.afiliacion.trama.Afiliacion271Worker;
//import pe.com.susalud.afiliacion.trama.Afiliacion997Worker;
import pe.com.susalud.beans.AfiliadoRequestPayloadBean;
import pe.com.susalud.beans.RegistraTramaBean;
import pe.com.susalud.beans.ReponseTramax12Parm;
import pe.com.susalud.beans.ResponseBean;
import pe.com.susalud.beans.ResponseDataBean;
import pe.com.susalud.beans.ResponseMqBean;
import pe.com.susalud.beans.TramaBean;
import pe.com.susalud.dao.EnvioMqDao;
import pe.com.susalud.demoafl.Afiliacion271Worker;
import pe.com.susalud.demoafl.Afiliacion997Worker;
import pe.com.susalud.demoafl.Trama271Bean;
import pe.com.susalud.demoafl.Trama997Bean;
import pe.com.susalud.jr.transaccion.afiliacion.bean.In271RegafiUpdate;
import pe.com.susalud.jr.transaccion.afiliacion.bean.In271RegafiUpdateAfiliacion;
import pe.com.susalud.jr.transaccion.afiliacion.bean.In271RegafiUpdateAfiliado;
import pe.com.susalud.jr.transaccion.afiliacion.service.impl.RegafiUpdate271ServiceImpl;
import pe.com.susalud.service.EnvioMqService;

@Service
public class EnvioMqServiceImpl implements EnvioMqService {

	private static final Logger LOGGER = LogManager.getLogger(EnvioMqServiceImpl.class);
	private final int TIEMPO_ESPERA_MILISEC_271 = 3000;
	//private final int TIEMPO_ESPERA_MILISEC_997 = 10000; // tiempo original
	private final int TIEMPO_ESPERA_MILISEC_997 = 30000; // tiempo sugerido por susalud de espera

	@Autowired
	private EnvioMqDao envioMqDao;
	
	// Flujo 1
	@Override
	public ResponseMqBean sendMqInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseMqBean response = new ResponseMqBean();
		try {
			// Fase 1
			// Insercion de afiliacion
			String codigoEnvioSuSalud = insertarAfiliacion(afiliadoBean);

			// Fase 2
			// Generacion de Trama
			// tipo afiliacion 0= Baja,1 =Alta
			String tipoAfiliacion = afiliadoBean.getTipoAfiliacion();
			System.out.println("*****COD:" + codigoEnvioSuSalud);
			//String trama = AfiliadosTramaX12Parm(codigoEnvioSuSalud, tipoAfiliacion);
			ReponseTramax12Parm responseX12n = AfiliadosTramaX12Parm(codigoEnvioSuSalud, tipoAfiliacion);
			String trama = responseX12n.getTrama();
			// Fase 3
			// Carga Trama
			//pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate cargaBean = cargarTramaMq(trama);
			System.out.println("TRAMA 0: "+trama);	
			// Envio de trama
			/*
			trama="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<sus:Online271RegafiUpdateRequest xmlns:sus=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd ../MsgSetProjOnline271RegafiUpdateRequest/importFiles/Online271RegafiUpdateRequest.xsd \">\r\n"
					+ "  <txNombre>271_REGAFI_UPDATE</txNombre>\r\n"
					+ "  <txPeticion>ISA*00*          *00*          *ZZ*30004          *ZZ*SUSALUD        *211011*2140*|*00501*100000001*0*T*:~GS*HS*30004          *SUSALUD        *20211011*214042  *059520516*X *00501       ~ST*271*11886769 *1#1                                ~BHT*0022*13~HL*1           *            *20*1~NM1*PR *2*                                                            *                                   *                         *          *          *PI*30004               *  *   *                                                            ~REF*8X *00                                                *                                                                                ~HL*2           *1           *21*1~NM1*IL *1*VERA                                                        *YSAIAS FERNANDO                    *                         *          *          *MI*                    *  *   *ZAMALLOA                                                    ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~REF*ACC*1                                                 *                                                                                ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~N2*                                                            *                                                            ~N4*                              *  *               *   *W *070101              *   ~DMG*D8 *19550706                           *1* *                    *  *PER~NM1*IL *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~DMG*D8 *                                   * * *                    *  *PER~DTP*ZZZ*D8 *20211011                           ~EB*W *   *                                   *   *                                                  *                                   *                  *          *  *   *   *                                   ~REF*ZZ *1                                                 *                                                                                ~NM1*IL *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~REF*ZZ *17                                                *                                                                                ~DMG*D8 *                                   * * *                    *  *PER~HL*3           *2           *22*0~NM1*07 *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~DMG*D8 *19550706                           * * *                    *  *PER~NM1*C9 *2*                                                            *                                   *                         *          *          *MI*                    *  *   *                                                            ~REF*DD *8                                                 *                                                                                *4A :20122794424         :   :                    :   :                    ~N2*                                                            *                                                            ~DMG*D8 *                                   * * *                    *  *PER~NM1*ACV*2*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*18 *84033020004179                                    *                                                                                ~REF*CT *0264148006                                        *                                                                                ~REF*OF *                                                  *                                                                                ~REF*BLT*1                                                 *                                                                                ~REF*ACC*1                                                 *                                                                                ~REF*6P *7                                                 *PROGRAMA DE ATENCION MEDICA                                                     ~REF*OZ *01                                                *                                                                                ~REF*ACD*1                                                 *                                                                                ~REF*AEC*                                                  *                                                                                ~PRV*SU *   *PER123987374                                      ~DTP*W02*D8 *                                   ~DTP*276*D8 *19841031                           ~DTP*093*D8 *                                   ~DTP*150*D8 *                                   ~DTP*151*D8 *                                   ~DTP*636*D8 *20160622                           ~DTP*636*TS *000000                             ~EB*1 *   *2                                  *   *                                                  *                                   *                  *          *  *   *   *                                   ~REF*ZZ *000                                               *                                                                                ~SE*52        *11886769 ~GE*1     *059520516~IEA*1    *100000001~</txPeticion>\r\n"
					+ "</sus:Online271RegafiUpdateRequest>";
			*/
			
			//Convertir Trama 
			String xmlTrama = xmlTrama2(trama);
			ResponseMqBean responseMq = enviarTramaMq(xmlTrama);
			
			//Adicion de campos adicionales
			responseMq.setDni(responseX12n.getDocumentoIdentidad());
			responseMq.setCodAfiliado(responseX12n.getCodAfiliacion());
			responseMq.setXmlEnvio(xmlTrama);
			responseMq.setIdSolicitud(afiliadoBean.getIdSolicitud());
			
			//Registro de Trama xml obtenida de mq
			ResponseDataBean responseReg= registrarRespuestaMq(responseMq);
			
			// Generacion de Trama contingencia
			generarTrama(codigoEnvioSuSalud);

			//response.setCodigo(codigoEnvioSuSalud);
			//response.setMensaje("Mensaje enviado Mq " + trama);
			response = responseMq;
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}
	
	// Flujo 2
	// Permite generar la trama x12n
	@Override
	public ResponseMqBean generaTramaInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseMqBean response = new ResponseMqBean();
		try {
			// Fase 1
			// Insercion de afiliacion
			String codigoEnvioSuSalud = insertarAfiliacion(afiliadoBean);

			// Fase 2
			// Generacion de Trama
			// tipo afiliacion 0= Baja,1 =Alta
			String tipoAfiliacion = afiliadoBean.getTipoAfiliacion();
			System.out.println("*****COD:" + codigoEnvioSuSalud);
			//String trama = AfiliadosTramaX12Parm(codigoEnvioSuSalud, tipoAfiliacion);
			ReponseTramax12Parm responseX12n = AfiliadosTramaX12Parm(codigoEnvioSuSalud, tipoAfiliacion);
			String trama = responseX12n.getTrama();
			// Fase 3
			// Carga Trama
			//pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate cargaBean = cargarTramaMq(trama);
			System.out.println("TRAMA 0: "+trama);	
			// Envio de trama
			/*
			trama="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<sus:Online271RegafiUpdateRequest xmlns:sus=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd ../MsgSetProjOnline271RegafiUpdateRequest/importFiles/Online271RegafiUpdateRequest.xsd \">\r\n"
					+ "  <txNombre>271_REGAFI_UPDATE</txNombre>\r\n"
					+ "  <txPeticion>ISA*00*          *00*          *ZZ*30004          *ZZ*SUSALUD        *211011*2140*|*00501*100000001*0*T*:~GS*HS*30004          *SUSALUD        *20211011*214042  *059520516*X *00501       ~ST*271*11886769 *1#1                                ~BHT*0022*13~HL*1           *            *20*1~NM1*PR *2*                                                            *                                   *                         *          *          *PI*30004               *  *   *                                                            ~REF*8X *00                                                *                                                                                ~HL*2           *1           *21*1~NM1*IL *1*VERA                                                        *YSAIAS FERNANDO                    *                         *          *          *MI*                    *  *   *ZAMALLOA                                                    ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~REF*ACC*1                                                 *                                                                                ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~N2*                                                            *                                                            ~N4*                              *  *               *   *W *070101              *   ~DMG*D8 *19550706                           *1* *                    *  *PER~NM1*IL *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~DMG*D8 *                                   * * *                    *  *PER~DTP*ZZZ*D8 *20211011                           ~EB*W *   *                                   *   *                                                  *                                   *                  *          *  *   *   *                                   ~REF*ZZ *1                                                 *                                                                                ~NM1*IL *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~REF*ZZ *17                                                *                                                                                ~DMG*D8 *                                   * * *                    *  *PER~HL*3           *2           *22*0~NM1*07 *1*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*DD *1                                                 *                                                                                *4A :23987374            :   :                    :   :                    ~DMG*D8 *19550706                           * * *                    *  *PER~NM1*C9 *2*                                                            *                                   *                         *          *          *MI*                    *  *   *                                                            ~REF*DD *8                                                 *                                                                                *4A :20122794424         :   :                    :   :                    ~N2*                                                            *                                                            ~DMG*D8 *                                   * * *                    *  *PER~NM1*ACV*2*                                                            *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*18 *84033020004179                                    *                                                                                ~REF*CT *0264148006                                        *                                                                                ~REF*OF *                                                  *                                                                                ~REF*BLT*1                                                 *                                                                                ~REF*ACC*1                                                 *                                                                                ~REF*6P *7                                                 *PROGRAMA DE ATENCION MEDICA                                                     ~REF*OZ *01                                                *                                                                                ~REF*ACD*1                                                 *                                                                                ~REF*AEC*                                                  *                                                                                ~PRV*SU *   *PER123987374                                      ~DTP*W02*D8 *                                   ~DTP*276*D8 *19841031                           ~DTP*093*D8 *                                   ~DTP*150*D8 *                                   ~DTP*151*D8 *                                   ~DTP*636*D8 *20160622                           ~DTP*636*TS *000000                             ~EB*1 *   *2                                  *   *                                                  *                                   *                  *          *  *   *   *                                   ~REF*ZZ *000                                               *                                                                                ~SE*52        *11886769 ~GE*1     *059520516~IEA*1    *100000001~</txPeticion>\r\n"
					+ "</sus:Online271RegafiUpdateRequest>";
			*/
			
			//Convertir Trama 
			String xmlTrama = xmlTrama2(trama);
			RegistraTramaBean datosTrama=new RegistraTramaBean();
			datosTrama.setX12n(trama);
			datosTrama.setCodAfiliado(afiliadoBean.getCodAfiliado());
			datosTrama.setDni(afiliadoBean.getDni());
			datosTrama.setIdSolicitud(afiliadoBean.getIdSolicitud());
			registrarTramax12n(datosTrama);
			
			/*
			ResponseMqBean responseMq = enviarTramaMq(xmlTrama);
			
			responseMq.setDni(responseX12n.getDocumentoIdentidad());
			responseMq.setCodAfiliado(responseX12n.getCodAfiliacion());
			responseMq.setXmlEnvio(xmlTrama);
			responseMq.setIdSolicitud(afiliadoBean.getIdSolicitud());
			
			ResponseDataBean responseReg= registrarRespuestaMq(responseMq);
			generarTrama(codigoEnvioSuSalud);
			*/
			
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}


	// Flujo 3
	// Permite el envio de tramas x12n
	@Override
	public ResponseMqBean enviaTramaInfoAfiliado(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseMqBean response = new ResponseMqBean();
		try {
			// Fase 1
			
			//String trama = afiliadoBean.getTramaX12n();
			TramaBean tramBean=seleccionarTrama(afiliadoBean.getIdSolicitud());
			String trama = tramBean.getXmlTramax12();
			LOGGER.info("1.trama TB_GEN_TRAMA_AFIL_XML :"+trama);
			System.out.println("2.trama TB_GEN_TRAMA_AFIL_XML :"+trama);
			//Convertir Trama 
			String xmlTrama = xmlTrama2(trama);
			
			ResponseMqBean responseMq = enviarTramaMq(xmlTrama);
			
			//Adicion de campos adicionales
			responseMq.setDni(afiliadoBean.getDni());
			responseMq.setCodAfiliado(afiliadoBean.getCodAfiliado());
			responseMq.setXmlEnvio(xmlTrama);
			responseMq.setIdSolicitud(afiliadoBean.getIdSolicitud());
			
			//Registro de Trama xml obtenida de mq
			ResponseDataBean responseReg= registrarRespuestaMq(responseMq);
			
			response = responseMq;
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}
	
	
	
	@Override
	public ResponseBean sendMqInfoAfiliadoTest(AfiliadoRequestPayloadBean afiliadoBean) {
		ResponseBean response = new ResponseBean();
		try {
			String trama="";
			enviarTramaMq(trama);

			//response.setCodigo(codigoEnvioSuSalud);
			response.setMensaje("Mensaje enviado Mq " + trama);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}
	
	public TramaBean seleccionarTrama(String idSolicitud) {
		TramaBean response=null;
		try {
			response = envioMqDao.selectTramax12n(idSolicitud);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}

	public ResponseDataBean registrarRespuestaMq(ResponseMqBean responseMq) {
		ResponseDataBean responseData=null;
		try {
			System.out.println("1 fecha transaccion :"+responseMq.getFeTransaccion());
			System.out.println("2 hora transaccion  :"+responseMq.getHoTransaccion());
			System.out.println("3 id transaccion :"+responseMq.getIdTransaccion());
			System.out.println("4 id receptor    :"+responseMq.getIdReceptor());
			System.out.println("5 id remitente   :"+responseMq.getIdRemitente());
			System.out.println("6 id correlativo :"+responseMq.getIdCorrelativo());
			System.out.println("7 nu control     :"+responseMq.getNuControl());
			System.out.println("8 nu control ST  :"+responseMq.getNuControlST());
			
			System.out.println("9  codigo afiliado  :"+responseMq.getCodAfiliado());
			System.out.println("10 dni			    :"+responseMq.getDni());
			System.out.println("11 id solicitud     :"+responseMq.getIdSolicitud());
			System.out.println("12 xml envio	    :"+responseMq.getXmlEnvio());
			
			responseData = envioMqDao.registrarRespuesta(responseMq);
			
			
			
			//response = responseData.getCodigo();
			
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return responseData;

	}
	
	public ResponseDataBean registrarTramax12n(RegistraTramaBean trama) {
		ResponseDataBean responseData=null;
		try {
			
			responseData = envioMqDao.registrarTramax12n(trama);
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return responseData;		
	}
	

	public String insertarAfiliacion(AfiliadoRequestPayloadBean afiliadoBean) {
		String response = "";
		try {
			Integer tipo = 1;
			String codigoUsuario="";
			if (afiliadoBean.getTipoAfiliacion()!=null && !afiliadoBean.getTipoAfiliacion().equals("")) {
				tipo= Integer.parseInt(afiliadoBean.getTipoAfiliacion());
			} else {
				tipo = 1;
			}
			
			if (afiliadoBean.getCodUsuario()!=null && !afiliadoBean.getCodUsuario().equals("")) {
				codigoUsuario=afiliadoBean.getCodUsuario().trim();
			} else {
				codigoUsuario="00005";
			}
			
			
			AfiliacionData afiliacionData = new AfiliacionData();
			afiliacionData.setC_tipo(tipo);
			//afiliacionData.setC_c_usuario("00005");
			afiliacionData.setC_c_usuario(codigoUsuario);
			afiliacionData.setN_i_cantidad(1); // Es solo un registro

			ResponseDataBean responseData = envioMqDao.insertarTbEnvioSuSalud(afiliadoBean, afiliacionData);
			// Registro de codigo envio Susalud
			envioMqDao.insertarTbEnvioSuSaludDetalle(afiliadoBean, responseData.getCodigo());
			
			response = responseData.getCodigo();
			
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;

	}

	public pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate cargarTramaMq(String trama) {
		pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate dato = null;
		try {
			System.out.println("cargarTramaMq  INI");
			// TODO FIX BUSINESS
			Afiliacion271Worker worker = new Afiliacion271Worker(trama);
			worker.execute();
			Trama271Bean bean = worker.get(TIEMPO_ESPERA_MILISEC_271, TimeUnit.MILLISECONDS);
			// Trama271Bean bean = new Trama271Bean();
			System.out.println("cargarTramaMq ->"+bean.toString());
			if (bean != null) {
				dato = bean.getDato();
			}
			System.out.println("cargarTramaMq  FIN");
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return dato;
	}

	//public String enviarTramaMq(pe.gob.susalud.jr.transaccion.susalud.bean.In271RegafiUpdate trama, String trama) {
	public ResponseMqBean enviarTramaMq(String trama) {
		ResponseMqBean response = new ResponseMqBean();
		pe.gob.susalud.jr.transaccion.susalud.bean.In997RegafiUpdate dato = null;
		try {

			// TODO FIX BUSINESS

			 Afiliacion997Worker worker = new Afiliacion997Worker(trama);
			 worker.execute(); 
			 Trama997Bean bean = worker.get(TIEMPO_ESPERA_MILISEC_997,TimeUnit.MILLISECONDS);

			//Trama997Bean bean = new Trama997Bean();
			if (bean != null) {
				// 271
				dato = bean.getDato();
				bean.getFeTransaccion();
				bean.getHoTransaccion();
				bean.getIdCorrelativo();
				bean.getIdReceptor();
				bean.getIdRemitente();
				bean.getIdTransaccion();
				bean.getNuControlST();
				bean.getNuControl();
				
				ResponseMqBean responseMq=new ResponseMqBean();
				responseMq.setFeTransaccion(bean.getFeTransaccion());
				responseMq.setHoTransaccion(bean.getHoTransaccion());
				responseMq.setIdCorrelativo(bean.getIdCorrelativo());
				responseMq.setIdReceptor(bean.getIdReceptor());
				responseMq.setIdRemitente(bean.getIdRemitente());
				responseMq.setIdTransaccion(bean.getIdTransaccion());
				responseMq.setNuControlST(bean.getNuControlST());
				responseMq.setNuControl(bean.getNuControl());
				response = responseMq;

			}

		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return response;
	}

	public ReponseTramax12Parm AfiliadosTramaX12Parm(String cod, String tipoAfiliacion) {
		ReponseTramax12Parm response=new ReponseTramax12Parm();
		String resultadoTrama = "";
		int ll_noprocesados = 0;
		int ll_procesados = 0;
		int li_vector = 0;
		/** mensaje de error **/
		String coExcBD = null;
		String ls_respuesta = null;
		String ls_error = "";
		String xcoDescripError;
		String xcoCampoErr;

		String sError = "9999";
		ArrayList<Afiliacion> listaAfiliacion = new ArrayList<Afiliacion>();
		// List<String> listaResultado = new ArrayList();

		// FIX BUSINESS
		// listaAfiliacion = ListaAfiliadosParam(cod);

		// clsDatos ObjD = new clsDatos();
		// listaAfiliacion = ObjD.ListaAfiliadosParam(cod);
		
		// Si es una alta
		if (tipoAfiliacion.equals("1")) {
			listaAfiliacion = envioMqDao.listAfiliadosAlta(cod);
		}
		// Si es una baja
		if (tipoAfiliacion.equals("0")) {
			listaAfiliacion = envioMqDao.listAfiliadosBaja(cod);
		}

		String codAfiliado = "";
		String numeroDocumento = "";
		if (listaAfiliacion != null) {
			int numero = 0;
			for (int i = 0; i < listaAfiliacion.size(); i++) {
				numero++;
				In271RegafiUpdate rRegafi = new In271RegafiUpdate();
				In271RegafiUpdateAfiliado rAfiliado = new In271RegafiUpdateAfiliado();
				In271RegafiUpdateAfiliacion rAfiliacion = new In271RegafiUpdateAfiliacion();
				Afiliacion afilia = listaAfiliacion.get(i);
				// Datos de cabecera del formato X12N - Constantes
				rRegafi.setNoTransaccion("271_REGAFI_UPDATE");
				System.out.println("Transaccion: " + rRegafi.getNoTransaccion());
				rRegafi.setIdRemitente(afilia.getIdRemitente());
				System.out.println("ID del remitente: " + rRegafi.getIdRemitente());
				rRegafi.setIdReceptor("SUSALUD");
				System.out.println("ID del receptor: " + rRegafi.getIdReceptor());
				rRegafi.setFeTransaccion(afilia.getFeTransaccion());
				System.out.println("Fecha Transaccion: " + rRegafi.getFeTransaccion());
				rRegafi.setHoTransaccion(afilia.getHoTransaccion());
				System.out.println("Hora Transaccion: " + rRegafi.getHoTransaccion());
				rRegafi.setIdCorrelativo(String.valueOf((numero + 100000000)));
				System.out.println("Correlativo: " + rRegafi.getIdCorrelativo());
				rRegafi.setIdTransaccion("271");
				System.out.println("ID Transaccion: " + rRegafi.getIdTransaccion());
				rRegafi.setTiFinalidad("13");
				System.out.println("Finalidad: " + rRegafi.getTiFinalidad());
				rRegafi.setCaRemitente("2");
				System.out.println("Calificador: " + rRegafi.getCaRemitente());
				// Datos de cabecera del formato X12N - Variables
				rRegafi.setTiOperacion(afilia.getTiOperacion());
				System.out.println("Tipo Operacion: " + rRegafi.getTiOperacion());
				/***** Si existe informacion *****/
				// Datos del afiliado
				codAfiliado = afilia.getCodAfiliado();
				if (afilia.getApPaternoAfiliado() != null)
					rAfiliado.setApPaternoAfiliado(afilia.getApPaternoAfiliado());// else
																					// rAfiliado.setApPaternoAfiliado(afilia.getApPaternoAfiliado());
				System.out.println("Apellido Paterno: " + afilia.getApPaternoAfiliado());
				if (afilia.getNoAfiliado1() != null)
					rAfiliado.setNoAfiliado1(afilia.getNoAfiliado1());// else rAfiliado.setNoAfiliado1("");
				if (afilia.getNoAfiliado2() != null)
					rAfiliado.setNoAfiliado2(afilia.getNoAfiliado2()); // else rAfiliado.setNoAfiliado2("");
				if (afilia.getApMaternoAfiliado() != null)
					rAfiliado.setApMaternoAfiliado(afilia.getApMaternoAfiliado()); // else
																					// rAfiliado.setApMaternoAfiliado("");
				if (afilia.getTiDocumentoAfil() != null)
					rAfiliado.setTiDocumentoAfil(afilia.getTiDocumentoAfil()); // else rAfiliado.setTiDocumentoAfil("");
				System.out.println("Cod Tipo Documento: " + afilia.getTiDocumentoAfil());
				if (afilia.getNuDocumentoAfil() != null)
					rAfiliado.setNuDocumentoAfil(afilia.getNuDocumentoAfil()); // else rAfiliado.setNuDocumentoAfil("");
				System.out.println("Dni: " + afilia.getNuDocumentoAfil());
				if (afilia.getEstadoAfiliado() != null)
					rAfiliado.setEstadoAfiliado(afilia.getEstadoAfiliado()); // else rAfiliado.setEstadoAfiliado("");
				if (afilia.getTiDocumentoAct() != null)
					rAfiliado.setTiDocumentoAct(afilia.getTiDocumentoAct()); // else rAfiliado.setTiDocumentoAct("1") ;
				if (afilia.getNuDocumentoAct() != null)
					rAfiliado.setNuDocumentoAct(afilia.getNuDocumentoAct()); // else
																				// rAfiliado.setNuDocumentoAct("45722376")
																				// ;
				if (afilia.getApCasadaAfiliado() != null)
					rAfiliado.setApCasadaAfiliado(afilia.getApCasadaAfiliado()); // else
																					// rAfiliado.setApCasadaAfiliado("")
																					// ;
				if (afilia.getCoNacionalidad() != null)
					rAfiliado.setCoNacionalidad(afilia.getCoNacionalidad());// else rAfiliado.setCoNacionalidad("") ;
				if (afilia.getUbigeo() != null)
					rAfiliado.setUbigeo(afilia.getUbigeo()); // else rAfiliado.setUbigeo("0") ;
				if (afilia.getFeNacimiento() != null)
					rAfiliado.setFeNacimiento(afilia.getFeNacimiento()); // else rAfiliado.setFeNacimiento("19890304") ;
				if (afilia.getGenero() != null)
					rAfiliado.setGenero(afilia.getGenero()); // else rAfiliado.setGenero("1") ;
				if (afilia.getCoPaisDoc() != null)
					rAfiliado.setCoPaisDoc(afilia.getCoPaisDoc()); // else rAfiliado.setCoPaisDoc("PER") ;
				System.out.println("Cod. Pais Documento: " + afilia.getCoPaisDoc());
				if (afilia.getFeFallecimiento() != null)
					rAfiliado.setFefallecimiento(afilia.getFeFallecimiento()); // else
																				// rAfiliado.setFefallecimiento("19000101")
																				// ;
				if (afilia.getCoPaisEmisorDocAct() != null)
					rAfiliado.setCoPaisEmisorDocAct(afilia.getCoPaisEmisorDocAct()); // else
																						// rAfiliado.setCoPaisEmisorDocAct("")
																						// ;
				if (afilia.getFeActPersonaxIafas() != null)
					rAfiliado.setFeActPersonaxIafas(afilia.getFeActPersonaxIafas()); // else
																						// rAfiliado.setFeActPersonaxIafas("20150106")
																						// ;
				if (afilia.getIdAfiliadoNombre() != null)
					rAfiliado.setIdAfiliadoNombre(afilia.getIdAfiliadoNombre()); // else
																					// rAfiliado.setIdAfiliadoNombre("0")
																					// ;
				System.out.println("Identificador Nombre 2: " + afilia.getIdAfiliadoNombre());
				if (afilia.getTiDocTutor() != null)
					rAfiliado.setTiDocTutor(afilia.getTiDocTutor()); // else rAfiliado.setTiDocTutor("1") ;
				if (afilia.getNuDocTutor() != null)
					rAfiliado.setNuDocTutor(afilia.getNuDocTutor()); // else rAfiliado.setNuDocTutor("45722376") ;
				if (afilia.getTiVinculoTutor() != null)
					rAfiliado.setTiVinculoTutor(afilia.getTiVinculoTutor()); // else rAfiliado.setTiVinculoTutor("17") ;
				if (afilia.getFeValidacionReniec() != null)
					rAfiliado.setFeValidacionReniec(afilia.getFeValidacionReniec()); // else
																						// rAfiliado.setFeValidacionReniec("0");
				if (afilia.getCoPaisEmisorDocTutor() != null)
					rAfiliado.setCoPaisEmisorDocTutor(afilia.getCoPaisEmisorDocTutor()); // else
																							// rAfiliado.setCoPaisEmisorDocTutor("PER")
																							// ;

				if (afilia.getTiDocTitular() != null)
					rAfiliacion.setTiDocTitular(afilia.getTiDocTitular());
				System.out.println("Tipo de Doc. del Titular: " + afilia.getTiDocTitular());
				//Numero de documento de titular
				numeroDocumento = afilia.getNuDocTitular();
				
				if (afilia.getNuDocTitular() != null)
					rAfiliacion.setNuDocTitular(afilia.getNuDocTitular());
				System.out.println("Num. doc. de identidad del titular: " + afilia.getNuDocTitular());
				if (afilia.getFeNacimientoTitular() != null)
					rAfiliacion.setFeNacimientoTitular(afilia.getFeNacimientoTitular());
				System.out.println("Fecha de Nacimiento del titular: " + afilia.getFeNacimientoTitular());
				if (afilia.getCoPaisEmisorDocTitular() != null)
					rAfiliacion.setCoPaisEmisorDocTitular(afilia.getCoPaisEmisorDocTitular());
				System.out.println("Cod. Pais Emisor: " + afilia.getCoPaisEmisorDocTitular());
				if (afilia.getTiContratante() != null)
					rAfiliacion.setTiContratante(afilia.getTiContratante());
				else
					rAfiliacion.setTiContratante("2");
				System.out.println("Calificador del contratante: " + afilia.getTiContratante());
				if (afilia.getApPaternoContratante() != null)
					rAfiliacion.setApPaternoContratante(afilia.getApPaternoContratante());
				else
					rAfiliacion.setApPaternoContratante("");
				if (afilia.getNoContratante1() != null)
					rAfiliacion.setNoContratante1(afilia.getNoContratante1());
				else
					rAfiliacion.setNoContratante1("");
				if (afilia.getNoContratante2() != null)
					rAfiliacion.setNoContratante2(afilia.getNoContratante2());
				else
					rAfiliacion.setNoContratante2("");
				if (afilia.getNoContratante3() != null)
					rAfiliacion.setNoContratante3(afilia.getNoContratante3());
				else
					rAfiliacion.setNoContratante3("");
				if (afilia.getNoContratante4() != null)
					rAfiliacion.setNoContratante4(afilia.getNoContratante4());
				else
					rAfiliacion.setNoContratante4("");
				if (afilia.getApMaternoContratante() != null)
					rAfiliacion.setApMaternoContratante(afilia.getApMaternoContratante());
				else
					rAfiliacion.setApMaternoContratante("");
				if (afilia.getTiDocContratante() != null)
					rAfiliacion.setTiDocContratante(afilia.getTiDocContratante());
				else
					rAfiliacion.setTiDocContratante("1");
				System.out.println("Tipo Doc. contratante: " + afilia.getTiDocContratante());
				if (afilia.getNuDocContratante() != null)
					rAfiliacion.setNuDocContratante(afilia.getNuDocContratante());
				else
					rAfiliacion.setNuDocContratante("45722376");
				System.out.println("Num. Doc. contratante: " + afilia.getNuDocContratante());
				if (afilia.getApCasadaContratante() != null)
					rAfiliacion.setApCasadaContratante(afilia.getApCasadaContratante());
				else
					rAfiliacion.setApCasadaContratante("");
				if (afilia.getFeNacimientoContratante() != null)
					rAfiliacion.setFeNacimientoContratante(afilia.getFeNacimientoContratante());
				else
					rAfiliacion.setFeNacimientoContratante("");
				if (afilia.getCoPaisEmisorDocContratante() != null)
					rAfiliacion.setCoPaisEmisorDocContratante(afilia.getCoPaisEmisorDocContratante());
				else
					rAfiliacion.setCoPaisEmisorDocContratante("PER");
				System.out.println("Cod. Pais Emisor Contratante: " + afilia.getCoPaisEmisorDocContratante());
				if (afilia.getCoAfiliacion() != null)
					rAfiliacion.setCoAfiliacion(afilia.getCoAfiliacion());
				else
					rAfiliacion.setCoAfiliacion("40002");
				System.out.println("********Cod Afiliacion*******: " + afilia.getCoAfiliacion());
				if (afilia.getCoContrato() != null)
					rAfiliacion.setCoContrato(afilia.getCoContrato());
				else
					rAfiliacion.setCoContrato("163675");
				System.out.println("Cod Contratante: " + afilia.getCoContrato());
				if (afilia.getCoUnicoMultisectorial() != null)
					rAfiliacion.setCoUnicoMultisectorial(afilia.getCoUnicoMultisectorial());
				else
					rAfiliacion.setCoUnicoMultisectorial("");
				if (afilia.getTiRegimen() != null)
					rAfiliacion.setTiregimen(afilia.getTiRegimen());
				else
					rAfiliacion.setTiregimen("1");
				System.out.println("Tipo Regimen: " + afilia.getTiRegimen());
				if (afilia.getEsAfiliacion() != null)
					rAfiliacion.setEsAfiliacion(afilia.getEsAfiliacion());
				else
					rAfiliacion.setEsAfiliacion("5");
				System.out.println("Estado de Afiliacion: " + afilia.getEsAfiliacion());
				if (afilia.getCoCausalBaja() != null)
					rAfiliacion.setCoCausalBaja(afilia.getCoCausalBaja());
				else
					rAfiliacion.setCoCausalBaja(""); // CAMPO 56
				System.out.println("Cod. Causal Baja: " + afilia.getCoCausalBaja());
				if (afilia.getTiPlanSalud() != null)
					rAfiliacion.setTiPlanSalud(afilia.getTiPlanSalud());
				else
					rAfiliacion.setTiPlanSalud("7"); // CAMPO 57
				System.out.println("Tipo plan salud: " + afilia.getTiPlanSalud());
				if (afilia.getNoProductoSalud() != null)
					rAfiliacion.setNoProductoSalud(afilia.getNoProductoSalud());
				else
					rAfiliacion.setNoProductoSalud("AutoSeguro FEBAN"); // CAMPO 58
				System.out.println("Nombre interno producto: " + afilia.getNoProductoSalud());
				if (afilia.getCoProducto() != null)
					rAfiliacion.setCoProducto(afilia.getCoProducto());
				else
					rAfiliacion.setCoProducto("01"); // CAMPO 59
				System.out.println("Codigo interno producto: " + afilia.getCoProducto());
				if (afilia.getParentesco() != null)
					rAfiliacion.setParentesco(afilia.getParentesco());
				else
					rAfiliacion.setParentesco("1"); // CAMPO 60
				System.out.println("Tipo parentesco afiliado: " + afilia.getParentesco());
				if (afilia.getCoRenipress() != null)
					rAfiliacion.setCoRenipress(afilia.getCoRenipress());
				else
					rAfiliacion.setCoRenipress(""); // CAMPO 61
				if (afilia.getPkAfiliado() != null)
					rAfiliacion.setPkAfiliado(afilia.getPkAfiliado());
				else
					rAfiliacion.setPkAfiliado("PER145722376"); // CAMPO 62
				System.out.println("PK Afiliado: " + afilia.getPkAfiliado());

				if (afilia.getFeActEstado() != null)
					rAfiliacion.setFeActEstado(afilia.getFeActEstado());
				else
					rAfiliacion.setFeActEstado("20160328"); // CAMPO 63
				System.out.println("Fecha Act. Estado: " + afilia.getFeActEstado());
				if (afilia.getFeIniAfiliacion() != null)
					rAfiliacion.setFeIniAfiliacion(afilia.getFeIniAfiliacion());
				else
					rAfiliacion.setFeIniAfiliacion("20141217"); // CAMPO 64
				System.out.println("Fecha Inicio afiliacion: " + afilia.getFeIniAfiliacion());
				if (afilia.getFeFinAfiliacion() != null)
					rAfiliacion.setFeFinAfiliacion(afilia.getFeFinAfiliacion());
				else
					rAfiliacion.setFeFinAfiliacion("");// CAMPO 65
				System.out.println("Fecha fin afiliacion: " + afilia.getFeFinAfiliacion());
				if (afilia.getFeIniCobertura() != null)
					rAfiliacion.setFeIniCobertura(afilia.getFeIniCobertura());
				else
					rAfiliacion.setFeIniCobertura(""); // CAMPO 66
				System.out.println("Fecha Ini. Cobertura: " + afilia.getFeIniCobertura());
				if (afilia.getFeFinCobertura() != null)
					rAfiliacion.setFeFinCobertura(afilia.getFeFinCobertura());
				else
					rAfiliacion.setFeFinCobertura(""); // CAMPO 67
				System.out.println("Fecha Fin Cobertura: " + afilia.getFeFinCobertura());
				if (afilia.getFeActOperacion() != null)
					rAfiliacion.setFeActOperacion(afilia.getFeActOperacion());
				else
					rAfiliacion.setFeActOperacion(""); // CAMPO 68
				System.out.println("Fecha Act. Operacion: " + afilia.getFeActOperacion());
				if (afilia.getTiActOperacion() != null)
					rAfiliacion.setTiActOperacion(afilia.getTiActOperacion());
				else
					rAfiliacion.setTiActOperacion("120339"); // CAMPO 69
				if (afilia.getCoTiCobertura() != null)
					rAfiliacion.setCoTiCobertura(afilia.getCoTiCobertura());
				else
					rAfiliacion.setCoTiCobertura("4"); // CAMPO 70
				System.out.println("Codigo cobertura: " + afilia.getCoTiCobertura());
				if (afilia.getIdAfiliacionNombre() != null)
					rAfiliacion.setIdAfiliacionNombre(afilia.getIdAfiliacionNombre());
				else
					rAfiliacion.setIdAfiliacionNombre("000"); // CAMPO 71
				System.out.println("Identificador de Contratante: " + afilia.getIdAfiliacionNombre());

				rRegafi.addDetalle(rAfiliado);
				rRegafi.addDetalle(rAfiliacion);
				
				RegafiUpdate271ServiceImpl regafi = new RegafiUpdate271ServiceImpl();
				try {
					String sTrama = regafi.beanToX12N(rRegafi);
					sError = rRegafi.getCoError();
					System.out.println(" Tramas : " + sTrama);
					if (sError.equals("0000")) {
						// listaResultado.add(sTrama);
						ll_procesados++;
						// System.out.println(sTrama);

						// ls_respuesta=ObjD.Ins_tramaX12Param(sTrama, cod, codAfiliado);

						// TODO FIX DATABASE URGENTE!!
						// clsDatos ObjDB = new clsDatos();
						// ls_respuesta = ObjDB.Ins_tramaX12Param(sTrama, cod, codAfiliado);
						ls_respuesta = envioMqDao.Ins_tramaX12Param(sTrama, cod, codAfiliado);

						// FIX Se asigna la trama asignada;
						resultadoTrama = sTrama;

						if (ls_respuesta.equals("true")) {
							System.out.println("Se Grabo Correctamente y envia trama" + ll_procesados + " DNI:"
									+ afilia.getNuDocumentoAfil());
						} else {
							System.out.println("Se Enviado la trama pero no se actualizo la confirmación.");
						}

					} else {
						// listaResultado.add(sError);
						ll_noprocesados++;
					}
				} catch (Exception e) {

					// listaResultado.add("Error: Verificar especificaciones tecnicas");
				}
			}
		}
		System.out.println(" ll_procesados " + ll_procesados);
		System.out.println(" ll_noprocesados " + ll_noprocesados);

		// return "";
		//return resultadoTrama;
		
		response.setDocumentoIdentidad(numeroDocumento);
		response.setCodAfiliacion(codAfiliado);
		response.setTrama(resultadoTrama);
		return response;
	}
	
	public String xmlTrama(List<String> xmltrama) {
		String ls_xmltrama;
		ls_xmltrama = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<sus:Online271RegafiUpdateRequest xmlns:sus=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd ../MsgSetProjOnline271RegafiUpdateRequest/importFiles/Online271RegafiUpdateRequest.xsd \">\n"
				+ "  <txNombre>271_REGAFI_UPDATE</txNombre>\n" + "  <txPeticion> " + xmltrama + " </txPeticion>\n"
				+ "</sus:Online271RegafiUpdateRequest>";
		return ls_xmltrama;
	}

	public String xmlTrama2(String xmltrama) {
		String ls_xmltrama;
		ls_xmltrama = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<sus:Online271RegafiUpdateRequest xmlns:sus=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.susalud.gob.pe/Afiliacion/Online271RegafiUpdateRequest.xsd ../MsgSetProjOnline271RegafiUpdateRequest/importFiles/Online271RegafiUpdateRequest.xsd \">\n"
				+ "  <txNombre>271_REGAFI_UPDATE</txNombre>\n" + "  <txPeticion> " + xmltrama + " </txPeticion>\n"
				+ "</sus:Online271RegafiUpdateRequest>";
		return ls_xmltrama;
	}
	
	public String generarTrama(String codigoEnvioSalud) {
		
		try{
			ArrayList<AfiliacionDataDet> contenido = new ArrayList();
			String rutaFileContigencia = java.util.ResourceBundle.getBundle("application").getString("ruta.archivos.contigencia");
			
			String codiIafa = "30004";
		    FeTransaccion fecha = new FeTransaccion();
		    String fechaEnvio = fecha.getFeTransaccion();
		    String cantidad = null;
		    
		    contenido = envioMqDao.ListarTrama(codigoEnvioSalud);
		    String nombreArchivo = codiIafa + "_" + fechaEnvio + "_" + contenido.size() + ".txt";
		    List<String> lines = new ArrayList<>();
		    for(AfiliacionDataDet afiliacionTrama:contenido) {
		    	lines.add(afiliacionTrama.getX_trama());
		    }
		    
		    //Path file = Paths.get("C:/resources/"+nombreArchivo);
		    Path file = Paths.get(rutaFileContigencia+nombreArchivo);
		    
		    try {
	            // Write lines of text to a file.
	            Files.write(file, lines, StandardCharsets.UTF_8);
	            
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		    
		    
		    LOGGER.info("nombre archivo  "+nombreArchivo);
		}catch(Exception ex) {
			LOGGER.error(ex.getMessage(),ex);
		}
		return null;
	}
}
