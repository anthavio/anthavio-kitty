package com.anthavio.kitty.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.anthavio.NotSupportedException;
import com.anthavio.aspect.ApiPolicyOverride;
import com.anthavio.kitty.scenario.Step;

/**
 * @author vanek
 * 
 * Ceka vstup od uzivatele
 */
public class WaitForInputStep extends Step {

	@XmlTransient
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public enum InputType {
		STRING, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, DECIMAL, DATE;
	}

	@XmlAttribute
	private InputType type;

	private String dateFormat;

	@XmlAttribute
	private String ctxKey;

	private String message;

	protected WaitForInputStep() {
		//jaxb
	}

	public WaitForInputStep(String ctxKey, String message) {
		this(null, null, ctxKey, message);
	}

	public WaitForInputStep(InputType type, String ctxKey, String message) {
		this(type, null, ctxKey, message);
	}

	public WaitForInputStep(String dateFormat, String ctxKey, String message) {
		this(InputType.DATE, dateFormat, ctxKey, message);
	}

	public WaitForInputStep(InputType type, String dateFormat, String ctxKey, String message) {
		this.type = type;
		this.dateFormat = dateFormat;
		this.ctxKey = ctxKey;
		this.message = message;
	}

	@Override
	public void init() {
		if (dateFormat != null && type == null) {
			type = InputType.DATE;
		}
	}

	@ApiPolicyOverride
	public void execute() throws Exception {
		String message = buildMessage();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out.println(message);
				String line = reader.readLine();
				if (line == null) {
					return; //Ctrl+C - Ukonci se cela VM
				}
				if (isInputOk(line.trim())) {
					break;
				}
			} catch (IOException iox) {
				iox.printStackTrace();
			}
		}
	}

	private String buildMessage() {
		String message;
		if (this.message != null) {
			message = this.message;
		} else {
			message = "Enter value";
			if (type != null) {
				message += " of " + type + " type";
				if (type == InputType.DATE) {
					String dateFormat = value(this.dateFormat, DEFAULT_DATE_FORMAT);
					message += " (" + dateFormat + ")";
				}
			}
			if (ctxKey != null) {
				message += " for " + ctxKey + " context key";
			}
		}
		return message;
	}

	private boolean isInputOk(String line) {
		if (line.length() == 0) {
			return false;
		}

		InputType type = value(this.type, InputType.STRING);
		Object value;
		try {
			switch (type) {
			case STRING:
				value = line;
				break;
			case BYTE:
				value = Byte.parseByte(line);
				break;
			case SHORT:
				value = Short.parseShort(line);
				break;
			case INTEGER:
				value = Integer.parseInt(line);
				break;
			case LONG:
				value = Long.parseLong(line);
				break;
			case FLOAT:
				value = Float.parseFloat(line);
				break;
			case DOUBLE:
				value = Double.parseDouble(line);
				break;
			case DECIMAL:
				value = new BigDecimal(line);
				break;
			case DATE:
				String dateFormat = value(this.dateFormat, DEFAULT_DATE_FORMAT);
				value = new SimpleDateFormat(dateFormat).parse(line);
			default:
				throw new NotSupportedException(type);
			}
		} catch (NumberFormatException nfx) {
			return false;
		} catch (ParseException px) {
			return false;
		}
		contextPutX(ctxKey, value);
		return true;
	}

	private void contextPutX(String ctxKey, Object value) {
		if (ctxKey != null) {
			contextPut(ctxKey, value);
		} else {
			contextPut(value);
		}
	}

	@Override
	public String toString() {
		String ret = super.toString();
		if (ctxKey != null) {
			ret += " ctxKey: " + ctxKey;
		}
		if (type != null) {
			ret += " type: " + type;
		}
		return ret;
	}
}
