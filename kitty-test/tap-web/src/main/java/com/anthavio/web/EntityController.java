package com.anthavio.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.anthavio.aspect.Logged;
import com.anthavio.dao.search.PagedResult;
import com.anthavio.tap.db.ExampleEntity;
import com.anthavio.tap.svc.ExampleSearchCriteria;
import com.anthavio.tap.svc.TapService;

/**
 * @author vanek
 *
 */
@Controller
@Logged
@RequestMapping(value = "/entity")
public class EntityController {

	private final int pageSize = 10;

	@Inject
	private TapService service;

	/*
		@RequestMapping(value = "/list")
		public String list(Model model) {
			PagedResult<ExampleEntity> result = service.search(new ExampleSearchCriteria(0, pageSize));
			model.addAttribute("result", result);
			return "entity/list";
		}

		@RequestMapping(value = "/list/{page}")
		public String listPage(@PathVariable("page") Integer page, Model model) {
			int offset = page * pageSize;
			PagedResult<ExampleEntity> result = service.search(new ExampleSearchCriteria(offset, pageSize));
			model.addAttribute("result", result);
			return "entity/list";
		}
	*/

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		//na teto strance budeme pracovat pouze s datumem bez casu
		DateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/search")
	public String search(@RequestParam(value = "pager.offset", required = false) Integer offset,
			@ModelAttribute("criteria") @Valid ExampleSearchCriteria criteria, BindingResult result, Model model) {

		criteria.setOffset(offset);
		criteria.setLimit(pageSize);

		PagedResult<ExampleEntity> searchResult = service.search(criteria);

		model.addAttribute("result", searchResult);
		return "entity/list";
	}

	@RequestMapping(value = "/list")
	public String list(@RequestParam(value = "pager.offset", required = false) Integer offset, Model model) {

		createEntitiesIfNotExist(60);

		PagedResult<ExampleEntity> result = service.search(new ExampleSearchCriteria(offset, pageSize));
		model.addAttribute("result", result);

		model.addAttribute("criteria", new ExampleSearchCriteria());

		return "entity/list";
	}

	@RequestMapping(value = "/{id}")
	public String detail(@PathVariable("id") Integer id, Model model) {
		ExampleEntity entity = service.findById(id);
		model.addAttribute("entity", entity);
		return "entity/detail";
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") Integer id, Model model) {
		ExampleEntity entity = service.findById(id);
		model.addAttribute("entity", entity);
		return "entity/edit";
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST, params = "ulozit")
	public String updatePost(@PathVariable("id") Integer id, @ModelAttribute("entity") @Valid ExampleEntity entity,
			BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "entity/edit";
		}
		service.update(entity);
		return detail(entity.getId(), model);
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST, params = "smazat")
	public String deletePost(@PathVariable("id") Integer id, Model model) {
		service.delete(id);
		return list(null, model);
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model) {
		service.delete(id);
		return list(null, model);
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createGet(Model model) {
		model.addAttribute("entity", new ExampleEntity());
		return "entity/edit";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createPost(@ModelAttribute("entity") @Valid ExampleEntity entity, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "entity/edit";
		}

		service.create(entity);
		return detail(entity.getId(), model);

	}

	private void createEntitiesIfNotExist(int count) {
		List<ExampleEntity> list = service.findAll();
		if (list.isEmpty()) {
			Date date = new Date();
			for (int i = 0; i < count; ++i) {
				ExampleEntity entity = new ExampleEntity("Bla bla\n" + System.currentTimeMillis(), date);
				service.create(entity);
				date = DateUtils.addDays(date, -1);
			}
		}
	}
}
