package pe.com.susalud.service.impl;

import org.springframework.stereotype.Service;

import pe.com.susalud.service.Afiliacion217Services;
import pe.com.susalud.util.AfiliacionUtil;

@Service
public class Afiliacion217ServicesImpl implements Afiliacion217Services {
	public void procesarTrama(String trama) throws Exception {
		AfiliacionUtil.procesarTramaX12(trama);
	}

}
