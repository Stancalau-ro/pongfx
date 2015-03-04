package ro.stancalau.pong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ro.stancalau.pong.control.LanguageController;
import ro.stancalau.pong.gui.ScreensConfig;
import ro.stancalau.pong.model.LanguageModel;

@Configuration
@Import(ScreensConfig.class)
public class AppConfig {

	
	@Bean
	LanguageModel languageModel() {
		return new LanguageModel();
	}
	
	@Bean
	LanguageController languageController() {
		return new LanguageController(languageModel());
	}
	
}
