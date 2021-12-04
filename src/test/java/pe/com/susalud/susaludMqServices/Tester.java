package pe.com.susalud.susaludMqServices;

import java.util.HashMap;

import pe.com.susalud.demoafl.ClsConnectionMQ;



public class Tester {
	public void testing() {

		try {

			ClsConnectionMQ mq = new ClsConnectionMQ();

			String messageString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "  <sus:BusqDatosAseguradoRequest xmlns:sus=\"http://www.susalud.gob.pe/acreditacion/BusqDatosAseguradoRequest.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.susalud.gob.pe/acreditacion/BusqDatosAseguradoRequest.xsd ../MsgSetProjBusqDatosAseguradoRequest/importFiles/pe/gob/susalud/www/acreditacion/busqdatosaseguradorequestxsd/BusqDatosAseguradoRequest.xsd \">"
					+ "  <txNombre>270_REGAFI ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789—'-.¥`_¡…Õ”⁄ƒÀœ÷‹¿»Ã“Ÿ¬ Œ‘€√’› </txNombre>"
					+ "  <coRemitente>15869</coRemitente>"
					+ "  <txPeticion>ISA*00*          *00*          *ZZ*15869          *ZZ*SUSALUD        *150311*1522*|*00501*756496608*0*T*:~GS*HS*15869          *SUSALUD        *20150311*152200  *094476747*X *00501       ~ST*270*10196620 *                                   ~BHT*0022*13~HL*1           *            *20*1~NM1*PR *2*                                                            *                                   *                         *          *          *PI*20100054184         *  *   *                                                            ~HL*2           *1           *21*1~NM1*RGA*2*SUSALUD                                                     *                                   *                         *          *          *  *                    *  *   *                                                            ~HL*3           *2           *22*0~NM1*IL *1*MU—OZ                                                    *                                   *                         *          *          *  *                    *  *   *                                                            ~REF*DD *                                                                                *                                                                                *4A :                    :   :                    :   :                    ~SE*11        *10196620 ~GE*1     *094476747~IEA*1    *756496608~</txPeticion>"
					+ "  </sus:BusqDatosAseguradoRequest>";

			HashMap<String, String> hp = mq.SendMessageSyn(messageString);

			if (hp != null) {

				String msgid = hp.get("MsgId");
				String msg = hp.get("Msg");

			} else {
				System.out.println("No Se encontro mensajes.");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
}
