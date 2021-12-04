package pe.com.susalud.service.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {
	
	private static final Logger LOGGER = LogManager.getLogger(AppInitializer.class);
	 

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		LOGGER.debug("onStartup INI");
		AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
		appContext.register(Application.class);
		

		container.addListener(new ContextLoaderListener(appContext));
		appContext.setServletContext(container);
		DispatcherServlet dispatcherServlet = new DispatcherServlet(appContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
		ServletRegistration.Dynamic dispatcher = container.addServlet("mvc", dispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/rest/*");
		LOGGER.debug("onStartup FIN");

	}
	
	

}
