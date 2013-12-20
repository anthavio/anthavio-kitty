/**
 * 
 */
package net.anthavio.kitty.util;

/**
 * @author vanek
 *
 */

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.anthavio.kitty.scenario.Scenario;
import net.anthavio.kitty.scenario.ScenarioException;
import net.anthavio.kitty.scenario.ScenarioExecution;
import net.anthavio.kitty.scenario.ScenarioInitException;
import net.anthavio.kitty.scenario.ScenarioStepException;
import net.anthavio.kitty.scenario.Step;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * @author vanek
 *
 * @deprecated
 */
@Deprecated
public class ExcelScenarioListener extends LoggingScenarioListener {

	public static final int CIDX_NAME = 0;

	public static final int CIDX_START = 1;

	public static final int CIDX_END = 2;

	public static final int CIDX_ISOK = 3;

	public static final int CIDX_MSG = 4;

	private FileChannel channel;

	private HSSFWorkbook workBook;

	private HSSFSheet sheet;

	private HSSFRow row;

	private int i = 0;

	public ExcelScenarioListener(File directory, File[] fileList) {
		String strStartDate = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		workBook = initWorkBook(directory, fileList);
		sheet = workBook.getSheetAt(0);
		try {
			File fileXls = new File(directory, "!" + directory.getName() + "_" + strStartDate + ".xls");
			RandomAccessFile rafXsl = new RandomAccessFile(fileXls, "rws");
			channel = rafXsl.getChannel();
			OutputStream outXls = Channels.newOutputStream(rafXsl.getChannel());
			workBook.write(outXls);
			//channel.position(0);
		} catch (IOException iox) {
			throw new ScenarioException(iox);
		}
	}

	//public ExcelScenarioListener(DirectoryModel directory) {
	//	this(directory.getPath(), directory.getSelectedFiles());
	//}

	@Override
	public void initStarted(ScenarioExecution execution) {
		super.initStarted(execution);
		row = sheet.getRow(++i);
		if (row == null) {
			throw new IllegalArgumentException("Row " + i + " does not exist");
		}
		row.getCell(CIDX_START).setCellValue(new Date());
		save(workBook);
	}

	@Override
	public void initFailed(ScenarioExecution execution, ScenarioInitException exception) {
		scenarioFailed(exception);
		super.initFailed(execution, exception);
	}

	@Override
	public void executionPassed(ScenarioExecution execution) {
		row.getCell(CIDX_END).setCellValue(new Date());
		row.getCell(CIDX_ISOK).setCellValue(true);
		save(workBook);
		super.executionPassed(execution);
	}

	@Override
	public void executionFailed(ScenarioExecution execution, ScenarioStepException exception) {
		scenarioFailed(exception);
		super.executionFailed(execution, exception);
	}

	private void scenarioFailed(ScenarioStepException exception) {
		row.getCell(CIDX_END).setCellValue(new Date());
		row.getCell(CIDX_ISOK).setCellValue(false);
		row.createCell(CIDX_MSG).setCellValue(new HSSFRichTextString(String.valueOf(exception)));
		save(workBook);
	}

	private void save(HSSFWorkbook wb) {
		try {
			//file.seek(0);
			channel.position(0);
			OutputStream outXls = Channels.newOutputStream(channel);
			wb.write(outXls);
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}

	/*
	private HSSFWorkbook load(FileChannel channel) {
		try {
			channel.position(0);
			//file.seek(0);
			// FileInputStream inXls = new FileInputStream(file);
			InputStream inXls = Channels.newInputStream(channel);
			//Tohle uzavre ten stream
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inXls));
			// inXls.close();
			return wb;
		} catch (IOException iox) {
			throw new RuntimeException(iox);
		}
	}
	*/
	public void close() {
		if (channel.isOpen()) {
			try {
				channel.close();
			} catch (IOException iox) {
				throw new RuntimeException(iox);
			}
		}
	}

	private HSSFWorkbook initWorkBook(File directory, File[] files) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(directory.getName());
		HSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue(new HSSFRichTextString(directory.getAbsolutePath()));
		sheet.setColumnWidth(0, 16000);
		row0.createCell(1).setCellValue(new HSSFRichTextString(files.length + " scenarios"));
		sheet.setColumnWidth(CIDX_START, 5000);
		sheet.setColumnWidth(CIDX_END, 5000);
		sheet.setColumnWidth(CIDX_ISOK, 3000);

		HSSFDataFormat format = wb.createDataFormat();
		HSSFCellStyle dateTimeStyle = wb.createCellStyle();
		dateTimeStyle.setDataFormat(format.getFormat("yyyy.m.d hh:mm:ss"));

		for (int i = 0; i < files.length; ++i) {
			HSSFRow row = sheet.createRow(i + 1);
			row.createCell(CIDX_NAME).setCellValue(new HSSFRichTextString(files[i].getName())); // konfig

			HSSFCell cellStartDate = row.createCell(CIDX_START); // datum startu
			cellStartDate.setCellStyle(dateTimeStyle);

			HSSFCell cellEndDate = row.createCell(CIDX_END); // datum konce
			cellEndDate.setCellStyle(dateTimeStyle);

			HSSFCell cellIsOk = row.createCell(CIDX_ISOK); // is ok
			cellIsOk.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);

			row.createCell(CIDX_MSG); // vysledek
		}
		return wb;
	}

	public static void main(String[] args) {
		try {
			File directory = new File("c:/apps");
			File[] files = directory.listFiles();
			XyzScenario scenario = new XyzScenario("Test", null);
			ExcelScenarioListener listener = new ExcelScenarioListener(directory, files);
			for (int i = 0; i < files.length; ++i) {
				listener.initStarted(null);
				Thread.sleep(100);
				listener.scenarioFailed(null);
			}
			listener.close();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	static class XyzScenario extends Scenario {

		public XyzScenario(String string, List<Step> steps) {
			super(string, steps);
		}

		@Override
		public List<Step> getSteps() {
			return null;
		}

		@Override
		public void setSteps(List<Step> steps) {
		}

	}

}
