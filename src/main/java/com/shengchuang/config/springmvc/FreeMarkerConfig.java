package com.shengchuang.config.springmvc; /**
 * 本项目不用FreeMarker
 */

//package config.springmvc;
//
//import java.io.IOException;
//import java.io.Writer;
//import java.util.Properties;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
//import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
//
//// import com.jagregory.shiro.freemarker.ShiroTags;
//
//import freemarker.common.Environment;
//import freemarker.template.TemplateException;
//import freemarker.template.TemplateExceptionHandler;

//@Configuration
//public class FreeMarkerConfig {
//
//	public static final String TEMPLATE_LOADER_PATH = "/views";
//
//	public static final String PROJECT_DEFAULT_CHARSET_ENCODING = "UTF-8";
//
//	public static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
//
//	public static final String HTML_EXTENSION = SpringMvcConfig.HTML_EXTENSION;
//
//	public static final String REQUEST_CONTEXT_ATTRIBUTE = "request";
//
//	protected static final Log logger = LogFactory.getLog(FreeMarkerConfig.class);
//
//	@Bean
//	public FreeMarkerViewResolver getFreeMarkerViewResolver() {
//		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
//		resolver.setOrder(0);
//		resolver.setSuffix(HTML_EXTENSION);
//		resolver.setContentType(HTML_CONTENT_TYPE);
//		resolver.setCache(true);
//		resolver.setRequestContextAttribute(REQUEST_CONTEXT_ATTRIBUTE);
//		resolver.setExposeRequestAttributes(true);
//		resolver.setExposeSessionAttributes(true);
//		resolver.setExposeSpringMacroHelpers(true);
//		return resolver;
//	}
//
//	@Bean
//	public FreeMarkerConfigurer getFreeMarkerConfigurer() {
//		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer() /*{
//			@Override
//			public void afterPropertiesSet() throws IOException, TemplateException {
//				super.afterPropertiesSet();
//				this.getConfiguration().setSharedVariable("shiro", new ShiroTags());
//			}
//		}*/;
//		configurer.setTemplateLoaderPath(TEMPLATE_LOADER_PATH);
//		configurer.setDefaultEncoding(PROJECT_DEFAULT_CHARSET_ENCODING);
//		Properties bonusSettings = new Properties();
//		bonusSettings.setProperty("locale", "zh_CN");
//		// 异常处理
//		bonusSettings.setProperty("template_exception_handler", TemplateExceptionHandlerImpl.class.getDisplayType());
//		bonusSettings.setProperty("number_format", "0.00");
//		configurer.setFreemarkerSettings(bonusSettings);
//		return configurer;
//	}
//
//	@Bean
//	public MappingJackson2JsonView getMappingJackson2JsonView() {
//		MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
//		return jsonView;
//	}
//
//	/**
//	 * freemarker异常处理
//	 *
//	 * @author HuangChengwei
//	 *
//	 */
//	public static class TemplateExceptionHandlerImpl implements TemplateExceptionHandler {
//		@Override
//		public void handleTemplateException(TemplateException te, Environment env, Writer out)
//				throws TemplateException
//		{
//            FreeMarkerConfig.logger.error("exception message:" + te.getLocalizedMessage());
//			try {
//				out.write("");
//			} catch (Exception e) {
//				// 没有异常
//			}
//		}
//	}
//
//}
