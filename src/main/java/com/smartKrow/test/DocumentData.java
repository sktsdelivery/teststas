package com.smartKrow.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DocumentData {

	public Map<String, List<Map<String, Object>>> getAllDocument() {
		Map<String, Object> document1 = new HashMap<>();
		document1.put("displayname", "ZAPGFFUP");
		document1.put("entityName", "ZAPGFFUP");
		document1.put("entityId", 1);
		document1.put("type", "FCI");
		document1.put("searchFields", Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));
		/*
		 * ZAPNPCRMEM ZAPPOINV
		 */

		Map<String, Object> document3 = new HashMap<>();
		document3.put("displayname", "ZAPNPCRMEM");
		document3.put("entityName", "ZAPNPCRMEM");
		document3.put("entityId", 3);
		document3.put("searchFields", Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));

		Map<String, Object> document4 = new HashMap<>();
		document4.put("displayname", "ZAPNPCRMEM");
		document4.put("entityName", "ZAPNPCRMEM");
		document4.put("entityId", 4);
		document4.put("searchFields", Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));

		Map<String, Object> document2 = new HashMap<>();
		document2.put("displayname", "ZAPEMPEXP");
		document2.put("entityName", "ZAPEMPEXP");
		document2.put("entityId", 2);
		document2.put("searchFields", Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));

		Map<String, Object> document5 = new HashMap<>();
		document5.put("displayname", "FCI");
		document5.put("entityName", "FCI");
		document5.put("entityId", 2);
		document5.put("searchFields", Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));

		Map<String, List<Map<String, Object>>> documentType = new HashMap<>();
		documentType.put("FCI", Arrays.asList(document5));
		documentType.put("FNCF", Arrays.asList(document2, document2, document3, document4));

		return documentType;
	}

	public Map<Long, List<Map<String, Object>>> getAllFields() {
		Map<Long, List<Map<String, Object>>> fields = new HashMap<>();
		fields.put(1L, Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9()));
		fields.put(3L, Arrays.asList(fieldDetails3()));
		return fields;
	}
	
	public List<Map<String, Object>> getAllFieldsAndTheirDocDetails(){
		return Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3(), fieldDetails4(),
				fieldDetails5(), fieldDetails6(), fieldDetails7(), fieldDetails8(), fieldDetails9());
	}

	public Map<String, Object> fieldDetails1() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 1);
		values.put("fieldName", "COCD");
		values.put("displayName", "COCD");
		values.put("minValue", 4);
		values.put("maxValue", 11);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 11);
		values.put("dataType", "number");
		values.put("isMandatory", true);
		values.put("type", "FCI");
		values.put("displayName", "ZAPGFFUP");
		values.put("entityName", "ZAPGFFUP");
		values.put("entityId", 1);
		return values;
	}

	public Map<String, Object> fieldDetails2() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 2);
		values.put("fieldName", "DOC_NUMBER");
		values.put("displayName", "DOCUMENT NUMBER");
		values.put("minValue", 2);
		values.put("maxValue", 5.5);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 2);
		values.put("dataType", "number");
		values.put("isMandatory", true);
		values.put("type", "FCI");
		values.put("displayName", "ZAPNPCRMEM");
		values.put("entityName", "ZAPNPCRMEM");
		values.put("entityId", 3);

		return values;

	}

	public Map<String, Object> fieldDetails3() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 3);
		values.put("fieldName", "YEAR");
		values.put("displayName", "YEAR");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 2024);
		values.put("dataType", "number");
		values.put("isMandatory", true);
		values.put("type", "FNI");
		values.put("displayName", "ZAPNPCRMEM");
		values.put("entityName", "ZAPNPCRMEM");
		values.put("entityId", 3);

		return values;

	}

	public Map<String, Object> fieldDetails4() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 4);
		values.put("fieldName", "CREATION_DATE");
		values.put("displayName", "CREATION DATE");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", "2024-06-24 12:00:00");
		values.put("dataType", "date");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPNPCRMEM");
		values.put("entityName", "ZAPNPCRMEM");
		values.put("entityId", 4);

		return values;

	}

	public Map<String, Object> fieldDetails5() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 5);
		values.put("fieldName", "ARCHIVE_DOC_ID");
		values.put("displayName", "ARCHIVE DOC ID");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 1);
		values.put("dataType", "number");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPNPCRMEM");
		values.put("entityName", "ZAPNPCRMEM");
		values.put("entityId", 4);

		return values;

	}

	public Map<String, Object> fieldDetails6() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 6);
		values.put("fieldName", "FNCA_DOC_TYPE");
		values.put("displayName", "FNCA DOC TYPE");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", "Boy");
		values.put("dataType", "text");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPEMPEXP");
		values.put("entityName", "ZAPEMPEXP");
		values.put("entityId", 2);

		return values;

	}

	public Map<String, Object> fieldDetails7() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 7);
		values.put("fieldName", "COCD_DESCRIPTION");
		values.put("displayName", "COCD DESCRIPTION");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", "Boy");
		values.put("dataType", "text");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPEMPEXP");
		values.put("entityName", "ZAPEMPEXP");
		values.put("entityId", 2);

		return values;

	}

	public Map<String, Object> fieldDetails8() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 8);
		values.put("fieldName", "VENDOR_ID");
		values.put("displayName", "VENDOR ID");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 7);
		values.put("dataType", "number");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPEMPEXP");
		values.put("entityName", "ZAPEMPEXP");
		values.put("entityId", 2);

		return values;

	}

	public Map<String, Object> fieldDetails9() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 9);
		values.put("fieldName", "VENDOR_NAME");
		values.put("displayName", "VENDOR_NAME");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", "name");
		values.put("dataType", "text");
		values.put("isMandatory", false);
		values.put("type", "FNCF");
		values.put("displayName", "ZAPEMPEXP");
		values.put("entityName", "ZAPEMPEXP");
		values.put("entityId", 2);

		return values;

	}
}
