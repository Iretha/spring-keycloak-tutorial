package com.smdev.demo.frontend;

import com.smdev.demo.frontend.controller.LogoutServlet;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import javax.servlet.http.HttpServlet;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	@Bean
	public ServletRegistrationBean logoutServlet() {
		ServletRegistrationBean<HttpServlet> regBean = new ServletRegistrationBean<>();
		regBean.setServlet(new LogoutServlet());
		regBean.addUrlMappings("/logout");
		regBean.setLoadOnStartup(1);
		return regBean;
	}

	/**
	 * https://stackoverflow.com/questions/43264890/after-upgrade-from-spring-boot-1-2-to-1-5-2-filenotfoundexception-during-tomcat#43280452
	 * @return
	 */
	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
		return new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				((StandardJarScanner) context.getJarScanner()).setScanManifest(false);
			}
		};
	}

}
