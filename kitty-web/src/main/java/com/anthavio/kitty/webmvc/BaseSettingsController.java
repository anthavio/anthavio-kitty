/**
 * 
 */
package com.anthavio.kitty.webmvc;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.anthavio.kitty.Kitty;
import com.anthavio.kitty.KittyOptions;

/**
 * @author vanek
 *
 */
@Controller("SettingsController")
public class BaseSettingsController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Inject
	private Kitty kitty;

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public void settingGet(Model model) {
		model.addAttribute("KittyOptions", kitty.getOptions());
	}

	@RequestMapping(value = "/setting", method = RequestMethod.POST)
	public void settingPost(@ModelAttribute("KittyOptions") KittyOptions options, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			List<ObjectError> allErrors = result.getAllErrors();
			log.warn("Errors: " + allErrors);
			return;
		}
		kitty.getOptions().setScenarioPrefix(options.getScenarioPrefix());

		kitty.getOptions().setUsePassedDir(options.getUsePassedDir());
		kitty.getOptions().setPassedDir(options.getPassedDir());
		kitty.getOptions().setPassedPause(options.getPassedPause());

		kitty.getOptions().setUseFailedDir(options.getUseFailedDir());
		kitty.getOptions().setFailedDir(options.getFailedDir());
		kitty.getOptions().setFailedPause(options.getFailedPause());

		kitty.getOptions().setSaveExecs(options.getSaveExecs());
	}

	//@RequestMapping(value = "/setting", method = RequestMethod.POST)
	public void settingPostX(//
			@RequestParam(value = "usePassedDir", required = false) Boolean usePassedDir,//
			@RequestParam(value = "useFailedDir", required = false) Boolean useFailedDir, //
			HttpServletRequest req) {

		if (usePassedDir != null) {
			this.kitty.getOptions().setUsePassedDir(true);
		} else {
			this.kitty.getOptions().setUsePassedDir(false);
		}
		if (useFailedDir != null) {
			this.kitty.getOptions().setUseFailedDir(true);
		} else {
			this.kitty.getOptions().setUseFailedDir(false);
		}
	}

}
