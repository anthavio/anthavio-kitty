package com.anthavio.kitty.webmvc;

/**
 * 
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.anthavio.kitty.model.DirectoryItem;

/**
 * @author vanek
 *
 */
@Controller
@SessionAttributes(BaseScenarioController.BROWSE_DIR_KEY)
public class BaseRootController {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@RequestMapping("/denied")
	public void denied() {

	}

	@RequestMapping("/welcome")
	public String welcome(Model model) {
		return "redirect:scenario/browse";
	}

	@RequestMapping("/login")
	public void login() {

	}

	@RequestMapping("/logout")
	public void logout() {

	}

	@RequestMapping("/user")
	public void user() {

	}

	@RequestMapping("/home")
	public void home() {

	}

	//@RequestMapping("/report")
	public void report(@ModelAttribute(BaseScenarioController.BROWSE_DIR_KEY) File browseDir, HttpServletResponse response)
			throws IOException {
		//TODO report delat z databaze spusteni
		Collection<File> files = FileUtils.listFiles(browseDir, new String[] { "xls" }, true);
		List<DirectoryItem> items = buildReportInput(files);
		HSSFWorkbook wb = buildReportOutput(items);
		response.setHeader("Pragma", "private");
		response.setHeader("Cache-Control", "private, must-revalidate");
		response.setContentType("application/vnd.ms-excel");
		//response.setContentLength(content.length);
		response.setHeader("Content-Disposition", "attachment; filename=\"ctt_report.xls\"");
		wb.write(response.getOutputStream());
	}

	/*
		@RequestMapping("/stopjms")
		public String jmsStop(Model model) {
			log.info("Stopping JMS listener containers");
			orderedContainer.stop();
			notificationContainer.stop();
			broadcastContainer.stop();
			jmsRunning = false;
			return "redirect:scenario/browse";
		}

		@RequestMapping("/startjms")
		public String jmsStart(Model model) {
			log.info("Starting JMS listener containers");
			orderedContainer.start();
			notificationContainer.start();
			broadcastContainer.start();
			jmsRunning = true;
			return "redirect:scenario/browse";
		}

		public boolean getJmsRunning() {
			return jmsRunning;
		}
	*/
	private HSSFWorkbook buildReportOutput(List<DirectoryItem> items) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("ctt_report");
		HSSFDataFormat format = wb.createDataFormat();
		HSSFCellStyle dateTimeStyle = wb.createCellStyle();
		dateTimeStyle.setDataFormat(format.getFormat("yyyy.m.d hh:mm:ss"));
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 12000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 20000);
		for (int i = 0; i < items.size(); ++i) {
			DirectoryItem item = items.get(i);
			HSSFRow row = sheet.createRow(i);
			row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(item.getFile().getParentFile().getName());
			String scenarioName = item.getFile().getName().substring(4, item.getFile().getName().length() - 4);
			row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(scenarioName);
			HSSFCell cellStartDate = row.createCell(2);
			cellStartDate.setCellStyle(dateTimeStyle);
			/*
			cellStartDate.setCellValue(item.getStarted());
			HSSFCell cellEndDate = row.createCell(3);
			cellEndDate.setCellStyle(dateTimeStyle);
			if (item.getFinished() != null) { //pokud scenar uz nebezi
				cellEndDate.setCellValue(item.getFinished());
				row.createCell(4, Cell.CELL_TYPE_BOOLEAN).setCellValue(item.getMessage() == null);
				row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(item.getMessage());
			}
			*/
		}
		return wb;
	}

	private List<DirectoryItem> buildReportInput(Collection<File> files) throws IOException {
		List<DirectoryItem> items = new ArrayList<DirectoryItem>();
		/*
		for (File xlsFile : files) {
			log.debug("Adding report data from " + xlsFile.getAbsolutePath());
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(xlsFile));
			HSSFSheet sheet = wb.getSheetAt(0);
			for (int i = 1; i <= sheet.getLastRowNum(); ++i) {
				HSSFRow row = sheet.getRow(i);
				Date dtStart = row.getCell(ExcelScenarioListener.CIDX_START).getDateCellValue();
				if (dtStart != null) {
					String name = row.getCell(ExcelScenarioListener.CIDX_NAME).getStringCellValue();
					Date dtEnd = row.getCell(ExcelScenarioListener.CIDX_END).getDateCellValue();
					boolean success = row.getCell(ExcelScenarioListener.CIDX_ISOK).getBooleanCellValue();
					String message = success ? null : row.getCell(ExcelScenarioListener.CIDX_MSG).getStringCellValue();
					DirectoryItem item = new DirectoryItem();
					item.setFile(new File(xlsFile.getParentFile(), name));
					item.setStarted(dtStart);
					item.setFinished(dtEnd);
					item.setMessage(message);
					items.add(item);
				}
			}
		}
		//sort by start date
		Collections.sort(items, new Comparator<DirectoryItem>() {

			@Override
			public int compare(DirectoryItem o1, DirectoryItem o2) {
				if (o1.getStarted().after(o2.getStarted())) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		*/
		return items;
	}
}
