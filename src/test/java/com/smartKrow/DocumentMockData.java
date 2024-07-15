package com.smartKrow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class DocumentMockData {

	public Map<Integer, String> getAllDocument() {
		Map<Integer, String> documents = new HashMap<>();
		documents.put(1, "CricketData");
		documents.put(2, "DOc2");
		documents.put(3, "Doc3");

		return documents;
	}

	public Map<Integer, List<Map<String, Object>>> getAllFields() {
		Map<Integer, List<Map<String, Object>>> fields = new HashMap<>();
		fields.put(1, Arrays.asList(fieldDetails1(), fieldDetails2(), fieldDetails3()));
		fields.put(3, Arrays.asList(fieldDetails3()));
		return fields;
	}

	public Map<String, Object> fieldDetails1() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 1);
		values.put("fieldName", "count");
		values.put("displayName", "players count");
		values.put("minValue", 4);
		values.put("maxValue", 11);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 11);
		values.put("dataType", "Integer");
		values.put("isMandatory", true);

		return values;

	}

	public Map<String, Object> fieldDetails2() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 1);
		values.put("fieldName", "runRate");
		values.put("displayName", "Run Rate");
		values.put("minValue", 2);
		values.put("maxValue", 5.5);
		values.put("status", "ACTIVE");
		values.put("defaultValue", 2);
		values.put("dataType", "Double");
		values.put("isMandatory", true);

		return values;

	}

	public Map<String, Object> fieldDetails3() {
		Map<String, Object> values = new HashMap<>();
		values.put("fieldId", 1);
		values.put("fieldName", "gameType");
		values.put("displayName", "Game For");
		values.put("minValue", 0);
		values.put("maxValue", 0);
		values.put("status", "ACTIVE");
		values.put("defaultValue", "Boy");
		values.put("dataType", "String");
		values.put("isMandatory", true);

		return values;

	}
}
