/**
 * 
 */
package net.anthavio.kitty.webmvc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import net.anthavio.kitty.model.ExecutionStats;
import net.anthavio.kitty.scenario.ScenarioExecution;
import net.anthavio.kitty.state.ExecutionDao;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;


/**
 * @author vanek
 *
 */
@Controller("ReportController")
@SessionAttributes(BaseReportController.CRITERIA_KEY)
@RequestMapping("/report")
public class BaseReportController {

	static final String CRITERIA_KEY = "ReportCriteria";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ModelAttribute(CRITERIA_KEY)
	public ReportCriteria defaultReportCriteria() {
		return new ReportCriteria();
	}

	@Inject
	private ExecutionDao dao;

	@RequestMapping(method = RequestMethod.GET)
	public void reportGet(@ModelAttribute(CRITERIA_KEY) ReportCriteria criteria, ModelMap model) {
		List<ScenarioExecution> list = dao.list(criteria);
		common(criteria, model, list);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void htmlReport(@ModelAttribute(CRITERIA_KEY) ReportCriteria criteria, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			List<ObjectError> allErrors = result.getAllErrors();
			log.warn("Errors: " + allErrors);
			return;
		}
		List<ScenarioExecution> list = dao.list(criteria);
		common(criteria, model, list);
	}

	@RequestMapping(params = "Excel", method = RequestMethod.POST)
	public void excelReport(@ModelAttribute(CRITERIA_KEY) ReportCriteria criteria, BindingResult result, ModelMap model,
			HttpServletResponse response) throws IOException {
		if (result.hasErrors()) {
			List<ObjectError> allErrors = result.getAllErrors();
			log.warn("Errors: " + allErrors);
			return;
		}

		List<ScenarioExecution> list = dao.list(criteria);

		HSSFWorkbook wb = buildExcelOutput(list);
		response.setHeader("Pragma", "private");
		response.setHeader("Cache-Control", "private, must-revalidate");
		response.setContentType("application/vnd.ms-excel");
		//response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"kitty-report.xls\"");
		wb.write(response.getOutputStream());
	}

	@RequestMapping(params = "Delete", method = RequestMethod.POST)
	public String delete(@ModelAttribute(CRITERIA_KEY) ReportCriteria criteria, BindingResult result, ModelMap model,
			HttpServletResponse response) throws IOException {
		if (result.hasErrors()) {
			List<ObjectError> allErrors = result.getAllErrors();
			log.warn("Errors: " + allErrors);
			return null;
		}
		dao.delete(criteria);
		return "redirect:report";
	}

	private void common(ReportCriteria criteria, ModelMap model, List<ScenarioExecution> list) {
		model.addAttribute(CRITERIA_KEY, criteria);
		model.addAttribute("Executions", list);
		ExecutionStats stats = dao.getStats();
		model.addAttribute("Stats", stats);
	}

	private HSSFWorkbook buildExcelOutput(List<ScenarioExecution> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("kitty-report");
		HSSFDataFormat format = wb.createDataFormat();
		HSSFCellStyle dateTimeStyle = wb.createCellStyle();
		dateTimeStyle.setDataFormat(format.getFormat("yyyy-m-d hh:mm:ss"));
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 12000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 20000);
		for (int i = 0; i < list.size(); ++i) {
			ScenarioExecution item = list.get(i);
			HSSFRow row = sheet.createRow(i);
			String dirPath = item.getFile().getParentFile().getAbsolutePath();
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(dirPath);
			String fileName = item.getFile().getName();
			//String scenarioName = item.getFile().getName().substring(4, item.getFile().getName().length() - 4);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(fileName);
			HSSFCell cellStartDate = row.createCell(2);
			cellStartDate.setCellStyle(dateTimeStyle);
			cellStartDate.setCellValue(item.getStarted());
			HSSFCell cellEndDate = row.createCell(3);
			cellEndDate.setCellStyle(dateTimeStyle);
			if (item.getEnded() != null) {
				//POI will throw Exception if null is passed in...
				cellEndDate.setCellValue(item.getEnded());
			}
			row.createCell(4, Cell.CELL_TYPE_BOOLEAN).setCellValue(item.getErrorMessage() == null);
			row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(item.getErrorMessage());
		}
		return wb;
	}
}
