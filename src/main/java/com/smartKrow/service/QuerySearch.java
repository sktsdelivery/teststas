package com.smartKrow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.smartKrow.dto.DocumentSearchRequest;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Service
public class QuerySearch {

	@Autowired
	private DocumentService documentService;

	private static final HashMap<String, String> partionKey = new HashMap<>();;
	static {
		partionKey.put("ZAPADVDOO", "COCD");
		partionKey.put("ZAPEMPEXP", "COCD");
		partionKey.put("ZAPGFFUP", "COCD");
		partionKey.put("ZAPLARINV", "COCD");
		partionKey.put("ZAPNPCRMEM", "COCD");
		partionKey.put("ZAPPHC", "COCD");
		partionKey.put("ZAPPOINV", "COCD");
		partionKey.put("ZAPPVTO", "COCD");
		partionKey.put("ZFADOCO", "ASSET NUM");
		partionKey.put("ZFRJDOCO", "COCD");
		partionKey.put("ZGLACCR", "COCD");
		partionKey.put("ZPCIMDOCT", "MAT_DOC");
		partionKey.put("ZPCPODOCO", "VENDOR_ID");
		partionKey.put("ZPCVNDEVP", "VENDOR_ID");
		partionKey.put("ZTSFTDOCO", "TRANSAC ID");
		partionKey.put("ZVMVENDORH", "VENDOR_ID");
		partionKey.put("ZVMVENDORP", "VENDOR_ID");
		partionKey.put("ZVMVENDORT", "VENDOR_ID");
		partionKey.put("FCI", "Scan Batch ID");
	}

	private static final HashMap<String, HashMap<String, Boolean>> sortKeys = new HashMap<>();;
	static {

		HashMap<String, Boolean> sortkey = new HashMap<>();
		sortkey.put("YEAR", true);
		sortkey.put("DOC_NUMBER", false);
		sortKeys.put("ZAPADVDOO", sortkey);
		sortKeys.put("ZAPEMPEXP", sortkey);
		sortKeys.put("ZAPGFFUP", sortkey);
		sortKeys.put("ZAPLARINV", sortkey);
		sortKeys.put("ZAPNPCRMEM", sortkey);
		sortKeys.put("ZAPPHC", sortkey);
		sortKeys.put("ZAPPOINV", sortkey);
		sortKeys.put("ZAPPVTO", sortkey);
		sortKeys.put("ZAPUPDOO", sortkey);
		sortKeys.put("ZFRJDOCO", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("DOC_NUMBER", false);
		sortKeys.put("ZGLACCR", sortkey);
		sortkey = new HashMap<>();
		sortkey.put("YEAR", false);
		sortKeys.put("ZPCIMDOCT", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("PO_NUMBER", false);
		sortkey.put("POCREATION", false);
		sortKeys.put("ZPCPODOCO", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("NAME2", false);
		sortkey.put("CREATION_DATE", false);
		sortKeys.put("ZPCVNDEVP", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("START DATE", false);
		sortkey.put("CREATION_DATE", false);
		sortKeys.put("ZTSFTDOCO", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("CREATION_DATE", false);
		sortKeys.put("ZFADOCO", sortkey);

		sortKeys.put("ZVMVENDORH", sortkey);
		sortKeys.put("ZVMVENDORP", sortkey);
		sortKeys.put("ZVMVENDORT", sortkey);

		sortkey = new HashMap<>();
		sortkey.put("f_entrydate", false);
		sortKeys.put("FCI", sortkey);
	}

	@Autowired
	private DynamoDbClient dynamoDbClient;

	public Page<Map<String, Object>> getSerahcResultTest(DocumentSearchRequest request, String entityName, int page,
			int size, String sortBy, String sortOrder) throws IllegalArgumentException, IllegalAccessException {
		Sort.Direction sortDirection = StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("desc")
				? Sort.Direction.DESC
				: Sort.Direction.ASC;
		Sort sort = Sort.by(sortDirection, getAttributeName(sortBy));
		Pageable pageable = PageRequest.of(page, size, sort);
		String partitionValue = null;
		// String sortKey = "25";

		String partitionKeyName = partionKey.get(entityName);
		HashMap<String, Boolean> sortleys = sortKeys.get(entityName);
		Map<String, AttributeValue> attributes = new HashMap<>();
		Map<String, String> name = new HashMap<>();
		String pk = "#pk";
		String pk1 = ":pk";
		int i = 1;
		String query = "#pk = :pk";
		StringBuilder query2 = new StringBuilder("");
		String filterScan = null;
		for (Map.Entry<String, Object> entry : request.getSearchCriteria().entrySet()) {
			String attributeName = getAttributeName(entry.getKey());
			String value = (String) entry.getValue();
			if (partitionKeyName.equals(attributes)) {
				partitionValue = value;
				name.put(pk, "PARTITION_KEY");
				attributes.put(pk1, AttributeValue.builder().s(partitionValue).build());
			} else if (entry.getKey().equalsIgnoreCase("Start Date From")
					|| entry.getKey().equalsIgnoreCase("Year From")
					|| entry.getKey().equalsIgnoreCase("Is Create Date From")) {
				String sortValue = value;
				name.put(pk + i, attributeName);
				attributes.put(pk1 + i, AttributeValue.builder().s(sortValue).build());
				query2.append(" AND " + pk + i + " <= :" + pk1 + i);
				i++;

			} else if (entry.getKey().equalsIgnoreCase("Start Date To") || entry.getKey().equalsIgnoreCase("Year To")
					|| entry.getKey().equalsIgnoreCase("Is Create Date To")) {
				String sortValue = value;
				name.put(pk + i, attributeName);
				attributes.put(pk1 + i, AttributeValue.builder().s(sortValue).build());
				query2.append(" AND " + pk + i + " >= :" + pk1 + i);
				i++;

			} else if (sortleys.containsKey(attributeName)) {
				String sortValue = value;
				name.put(pk + i, attributeName);
				attributes.put(pk1 + i, AttributeValue.builder().s(sortValue).build());
				query2.append(" AND " + pk + i + " = :" + pk1 + i);
				i++;
			} else {
				String sortValue = value;
				name.put(pk + i, sortValue);
				attributes.put(pk1 + i, AttributeValue.builder().s(partitionValue).build());
				if (filterScan == null) {
					filterScan = pk + i + " = :" + pk1 + i;
					i++;
				}
			}

		}
		if (partitionValue != null) {
			QueryRequest queryRequest = null;
			if (filterScan != null) {
				queryRequest = QueryRequest.builder().tableName(entityName).keyConditionExpression(query + query2)
						.expressionAttributeNames(name).filterExpression(filterScan)
						.expressionAttributeValues(attributes).build();
			} else {
				queryRequest = QueryRequest.builder().tableName(entityName).keyConditionExpression(query + query2)
						.expressionAttributeNames(name).expressionAttributeValues(attributes).build();
			}

			try {
				QueryResponse response = dynamoDbClient.query(queryRequest);
				List<Map<String, Object>> data = new ArrayList<>();
				for (Map<String, AttributeValue> item : response.items()) {
					Map<String, Object> object = new HashMap<>();
					for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
						String key = entry.getKey();
						object.put(entry.getKey(), entry.getValue().s());
					}
					data.add(object);
				}
				if (data.isEmpty()) {
					return new PageImpl<>(new ArrayList<>(), pageable, 0);
				} else {

					int start = (int) pageable.getOffset();
					int end = Math.min(start + pageable.getPageSize(), data.size());

					PageImpl<Map<String, Object>> response2 = new PageImpl<>(data.subList(start, end), pageable,
							data.size());

					return response2;
				}
			} catch (DynamoDbException e) {
				e.printStackTrace();
			}
		}
		return documentService.getDataFromSearch(entityName, request, page, size, sortBy, sortOrder);
	}

	private String getAttributeName(String name) {

		switch (name) {
		case "documentId":
			return "document_id";

		case "applicationType":
			return "application_type";

		case "cocd", "COCD":
			return "COCD";

		case "docNumber", "Document Number":
			return "DOC_NUMBER";

		case "year", "Year From", "Year To", "Year":
			return "YEAR";

		case "creationDate", "Creation Date", "CREATION DATE":
			return "CREATION_DATE";

		case "creationTime":
			return "CREATION_TIME";

		case "archiveDocId":
			return "ARCHIVE_DOC_ID";

		case "fncaDocType":
			return "FNCA_DOC_TYPE";

		case "cocdDescription", "COCD DESCRIPTION":
			return "COCD_DESCRIPTION";

		case "VENDOR ID", "vendorId", "Vendor Id":
			return "VENDOR_ID";

		case "vendorName", "Vendor Name":
			return "VENDOR_NAME";

		case "Vendor Name1", "VENDOR NAME1":
			return "VENDOR_NAME1";

		case "docTypeDescription":
			return "DOC_TYPE_DESCRIPTION";

		case "postingDate", "Posting Date":
			return "POSTING_DATE";

		case "documentDate", "Document Date":
			return "DOCUMENT_DATE";

		case "amountLc", "Amount LC":
			return "AMOUNT_LC";

		case "amtGrpCurrenc":
			return "AMT_GRP_CURRENC";

		case "paymentReferen":
			return "PAYMENT_REFEREN";

		case "assignment":
			return "ASSIGNMENT";

		case "clearingDoc":
			return "CLEARING_DOC";

		case "clearDate":
			return "CLEAR_DATE";

		case "enteredBy":
			return "ENTERED_BY";

		case "entryDate":
			return "ENTRY_DATE";

		case "curr", "CURR":
			return "CURR";

		case "docHeaderText":
			return "DOC_HEADER_TEXT";

		case "reference", "Reference":
			return "REFERENCE";

		case "fileName":
			return "FILENAME";

		case "contentRepTyp":
			return "CONTENT_REP_TYP";

		case "groupCurr":
			return "GROUP_CURR";

		case "documentLink":
			return "Document_Link";

		case "docChecksum":
			return "Doc_Checksum";

		case "docAnnotContent":
			return "Doc_AnnotContent";

		//
		case "NAME2":
			return "NAME2";

		case "POSTAL CODE":
			return "POSTAL CODE";

		case "CITY":
			return "CITY";

		case "STREET/HOUSE NO":
			return "STREET/HOUSE NO";

		case "COUNTRY":
			return "COUNTRY";

		case "ASSET_NUM":
			return "ASSET NUM";

		case "ASSET CLASS":
			return "ASSET CLASS";

		case "ASSET DESCRIP1":
			return "ASSET DESCRIP1";

		case "ASSET DESCRIP2":
			return "ASSET DESCRIP2";

		case "ACC DETERMINATI":
			return "ACC DETERMINATI";

		case "PO NUMBER":
			return "PO_NUMBER";

		case "VENDOR_ID":
			return "VENDOR_ID";

		case "POCREATION":
			return "POCREATION";

		case "FNCA PO Type":
			return "FNCA_PO_TYPE";

		case "Company Code":
			return "COMPANY_CODE";

		case "Net Amount":
			return "NET_AMT";

		case "Purchasing Org":
			return "PURCHASING_ORG";

		case "Phone Number":
			return "PHC_NUMBER";

		case "Req Type":
			return "REQ_TYPE";

		case "SCAN BATCH ID":
			return "Scan Batch ID";

		case "Is Create Date From":
			return "f_entrydate";

		case "MAT DOC", "MAT_DOC":
			return "MAT_DOC";

		case "Enty By":
			return "ENTERED_BY";

		case "Entry Date":
			return "ENTRY_DATE";

		case "TRANSAC_ID", "TRANSAC ID":
			return "TRANSAC ID";

		case "START DATE FROM":
			return "START DATE";

		case "START DATE TO":
			return "START DATE";

		case "Is Create Date To":
			return "f_entrydate";

		case "Year To":
			return "YEAR";
		default:
			return null;
		}

	}

}
