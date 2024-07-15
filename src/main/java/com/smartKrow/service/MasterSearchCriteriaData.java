package com.smartKrow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.smartKrow.constants.MasterDataConstant;
import com.smartKrow.dto.DocumentResponse;

@Service
public class MasterSearchCriteriaData {

	public DocumentResponse getMasterRecords() throws JSONException {
		DocumentResponse data = new DocumentResponse();
		List<HashMap<String, Object>> entities = new ArrayList<>();
		JSONObject json = new JSONObject(MasterDataConstant.masterData);
		JSONArray array = json.getJSONArray("resultData");
		for (int i = 0; i < array.length(); i++) {
			HashMap<String, Object> entity = new HashMap<>();
			JSONObject object = array.getJSONObject(i);
			entity.put("entity_id", object.getString("entity_id"));
			entity.put("displayName", object.getString("displayName"));
			entity.put("entityName", object.getString("entityName"));
			entity.put("type", object.getString("type"));
			List<HashMap<String, String>> serachEntities = new ArrayList<>();
			JSONArray searchData = object.getJSONArray("searchFields");
			for (int j = 0; j < searchData.length(); j++) {
				JSONObject fields = searchData.getJSONObject(j);
				HashMap<String, String> searchFields = new HashMap<>();
				searchFields.put("fieldName", fields.getString("fieldName"));
				searchFields.put("displayName", fields.getString("displayName"));
				searchFields.put("dataType", fields.getString("dataType"));
				searchFields.put("isMandatory", fields.getString("isMandatory"));
				searchFields.put("status", fields.getString("status"));
				serachEntities.add(searchFields);
			}
			entity.put("searchFields", serachEntities);
			entities.add(entity);
		}
		System.out.println(json);

		data.setIsSuccess("Success");
		data.setResultData(entities);
		return data;
	}

}
