package ro.stancalau.pong.control;


import ro.stancalau.pong.model.LanguageModel;
import ro.stancalau.pong.model.LanguageModel.Language;

public class LanguageController {

	private LanguageModel model;
	
	public LanguageController(LanguageModel model){
		this.model = model;
		toEnglish();
	}
	
	public void toEnglish(){
		model.setBundle(Language.EN);
	}
	
	public Language getLanguage() {
		return model.getLanguage();
	}
	
}
