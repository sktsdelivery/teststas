package com.smartKrow.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.smartKrow.dto.Document;
import com.smartKrow.dto.DocumentResponse;
import com.smartKrow.dto.DocumentSearchRequest;
import com.smartKrow.dto.ErrorList;
import com.smartKrow.dto.SearchFields;
import com.smartKrow.model.CommonFields;
import com.smartKrow.model.FCI;
import com.smartKrow.model.FieldsDetails;
import com.smartKrow.model.MasterSearchDataTable;
import com.smartKrow.model.ZAPADVDOO;
import com.smartKrow.model.ZAPEMPEXP;
import com.smartKrow.model.ZAPGFFUP;
import com.smartKrow.model.ZAPLARINV;
import com.smartKrow.model.ZAPNPCRMEM;
import com.smartKrow.model.ZAPPHC;
import com.smartKrow.model.ZAPPOINV;
import com.smartKrow.model.ZAPPVTO;
import com.smartKrow.model.ZAPUPDOO;
import com.smartKrow.model.ZFADOCO;
import com.smartKrow.model.ZFRJDOCO;
import com.smartKrow.model.ZGLACCR;
import com.smartKrow.model.ZPCIMDOCT;
import com.smartKrow.model.ZPCPODOCO;
import com.smartKrow.model.ZPCVNDEVP;
import com.smartKrow.model.ZTSFTDOCO;
import com.smartKrow.model.ZVMVENDORH;
import com.smartKrow.model.ZVMVENDORP;
import com.smartKrow.model.ZVMVENDORT;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentService {

	/** The mapper. */
	@Autowired
	private DynamoDBMapper mapper;

	/** The Constant tableModelMap. */
	private static final Map<String, Class<?>> tableModelMap = new HashMap<>();

	static {
		tableModelMap.put("ZAPGFFUP", ZAPGFFUP.class);
		tableModelMap.put("ZAPLARINV", ZAPLARINV.class);
		tableModelMap.put("ZAPNPCRMEM", ZAPNPCRMEM.class);
		tableModelMap.put("ZAPPHC", ZAPPHC.class);
		tableModelMap.put("ZAPPOINV", ZAPPOINV.class);
		tableModelMap.put("ZAPPVTO", ZAPPVTO.class);
		tableModelMap.put("ZAPUPDOO", ZAPUPDOO.class);
		tableModelMap.put("ZAP-ADVDOO", ZAPADVDOO.class);
		tableModelMap.put("ZFADOCO", ZFADOCO.class);
		tableModelMap.put("ZFRJDOCO", ZFRJDOCO.class);
		tableModelMap.put("ZGLACCR", ZGLACCR.class);
		tableModelMap.put("ZPCVNDEVP", ZPCVNDEVP.class);
		tableModelMap.put("ZTSFTDOCO", ZTSFTDOCO.class);
		tableModelMap.put("ZVMVENDORH", ZVMVENDORH.class);
		tableModelMap.put("ZVMVENDORP", ZVMVENDORP.class);
		tableModelMap.put("ZVMVENDORT", ZVMVENDORT.class);
		tableModelMap.put("ZAPEMPEXP", ZAPEMPEXP.class);
		tableModelMap.put("ZPCIMDOCT", ZPCIMDOCT.class);
		tableModelMap.put("ZPCPODOCO", ZPCPODOCO.class);
		tableModelMap.put("FCI", FCI.class);

	}

	/**
	 * Gets the all entities.
	 *
	 * @return the all entities
	 */
	public DocumentResponse getAllEntities() {
		log.info("starting the fetch record of all entities");

		List<Document> response = new ArrayList<>();

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<MasterSearchDataTable> data = mapper.scan(MasterSearchDataTable.class, scanExpression);
		data.forEach(value -> {
			log.info("record:-" + value);
			try {
				response.add(getPayload(value));
			} catch (IllegalArgumentException e) {
				log.error("issue while create object of Document in get all entities", e);
			}
		});

		log.info("get all Entity data with data:- " + data);

		if (response == null || response.isEmpty()) {
			return getResponse(null, "Error", Arrays.asList("No Data found"), 404);
		} else {
			return getResponse(response, "Success", null, null);
		}
	}

	/**
	 * Gets the payload.
	 *
	 * @param value the value
	 * @return the payload
	 */
	private Document getPayload(MasterSearchDataTable value) {
		Document doc = new Document();
		doc.setDisplayName(value.getDisplayName());
		doc.setEntityName(value.getEntityName());
		doc.setType(value.getType());
		doc.setSearchFields(getFields(value.getSearchFields()));
		doc.setEntityId(value.getEntityId());

		log.info(doc.getEntityName());

		return doc;
	}

	/**
	 * Gets the fields.
	 *
	 * @param searchFields the search fields
	 * @return the fields
	 */
	private List<SearchFields> getFields(List<FieldsDetails> searchFields) {

		return searchFields.stream().map(field -> {

			log.info("field level records" + field);

			SearchFields data = new SearchFields();

			data.setFieldName(field.getFieldName());
			data.setDisplayName(field.getDisplayName());
			data.setStatus(field.getStatus());
			data.setDataType(field.getDataType());
			data.setIsMandatory(field.getIsMandatory());
			data.setIsForSearch(field.getIsForSearch());
			data.setIsForSearchGrid(field.getIsForSearchGrid());
			data.setIsForSort(field.getIsForSort());

			return data;

		}).collect(Collectors.toList());
	}

	/**
	 * Gets the payload.
	 *
	 * @param value the value
	 * @return the payload
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException   the illegal access exception
	 */

	private Map<String, Object> getSearchFieldData(CommonFields value)
			throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> data = new HashMap<>();
		Class<?> clazz = value.getClass();

		// Iterate through fields declared in the class
		while (clazz != null) {
			Field[] fields = clazz.getDeclaredFields();

			for (Field field : fields) {
				field.setAccessible(true); // To access private fields

				DynamoDBAttribute attribute = field.getAnnotation(DynamoDBAttribute.class);
				String attributeName = attribute.attributeName();

				data.put(attributeName, field.get(value));

				log.info("Field name: " + field.getName() + " value: " + field.get(value));

			}

			clazz = null;
		}
		return data;
	}

	/**
	 * Gets the search field.
	 *
	 * @param name       the name
	 * @param type       the type
	 * @param documentId the document id
	 * @param value      the value
	 * @return the search field
	 */
	private SearchFields getSearchField(String name, String type, String documentId, Object value) {
		SearchFields field = new SearchFields();
		field.setDataType(type);
		field.setDisplayName(name);
		field.setDocumentId(documentId);
		field.setFieldName(getAttributeName(name));
		field.setIsMandatory(true);
		field.setStatus("active");
		field.setValue(value);
		;
		return field;
	}

	/**
	 * Gets the response.
	 *
	 * @param list         the list
	 * @param report       the report
	 * @param errorMessage the error message
	 * @param errorCode    the error code
	 * @return the response
	 */
	public DocumentResponse getResponse(Object list, String report, List<String> errorMessage, Integer errorCode) {
		DocumentResponse response = new DocumentResponse();

		if ("Success".equals(report)) {
			response.setIsSuccess("Success");
			response.setResultData(list);
		} else {
			response.setIsSuccess("Error");
			ErrorList errorList = new ErrorList();
			errorList.setErrorCode(errorCode);
			errorList.setErrorMessage(errorMessage);
			response.setErrorList(errorList);
		}
		return response;
	}

	/**
	 * Gets the data from search.
	 *
	 * @param entityName the entity name
	 * @param payload    the payload
	 * @param page       the page
	 * @param size       the size
	 * @param sortBy     the sort by
	 * @param sortOrder  the sort order
	 * @return the data from search
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException   the illegal access exception
	 */
	public Page<Map<String, Object>> getDataFromSearch(String entityName, DocumentSearchRequest payload, int page,
			int size, String sortBy, String sortOrder) throws IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = tableModelMap.get(entityName);
		if (clazz == null) {
			throw new IllegalArgumentException("No model class found for table: " + entityName);
		}

		log.info("starting the search record of required field" + payload + "for entityId :- " + entityName);

		Sort.Direction sortDirection = StringUtils.hasText(sortOrder) && sortOrder.equalsIgnoreCase("desc")
				? Sort.Direction.DESC
				: Sort.Direction.ASC;

		Sort sort = Sort.by(sortDirection, getAttributeName(sortBy));

		Pageable pageable = PageRequest.of(page, size, sort);

		List<?> list = new ArrayList<>();

		if (payload.getSearchCriteria() == null) {
			throw new ValidationException("SearchCriteria can not be empty");
		}

		List<String> errorList = new ArrayList<>();

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withLimit(500);

		if (payload.getSearchCriteria() != null && !payload.getSearchCriteria().isEmpty()) {

			Map<String, Condition> attributeConditions = new HashMap<>();

			payload.getSearchCriteria().forEach((key, value) -> {

				if (value == null || value == "") {
					errorList.add(key + " value cannot be null or empty");
				} else if (errorList.isEmpty() && (!key.equals("documentId"))) {

					String attributeKey = getAttributeName(key);

					if (attributeKey == null) {
						log.error("key :-" + key + " not mapped with attribute");
					}
					if (key.equalsIgnoreCase("START DATE FROM") || key.equalsIgnoreCase("START DATE TO")) {

						Object high = payload.getSearchCriteria().getOrDefault("START DATE FROM", null);

						Object low = payload.getSearchCriteria().getOrDefault("START DATE TO", null).toString();

						if (high != null && low != null) {

							Condition betweenCondition = new Condition()
									.withComparisonOperator(ComparisonOperator.BETWEEN)
									.withAttributeValueList(new AttributeValue().withS(high.toString()),

											new AttributeValue().withS(low.toString()));

							attributeConditions.put(attributeKey, betweenCondition);

						} else if (high != null) {

							Condition betweenCondition = new Condition().withComparisonOperator(ComparisonOperator.GE)

									.withAttributeValueList(new AttributeValue().withS(high.toString()));

							attributeConditions.put(attributeKey, betweenCondition);

						} else if (low != null) {

							Condition betweenCondition = new Condition().withComparisonOperator(ComparisonOperator.LE)

									.withAttributeValueList(new AttributeValue().withS(low.toString()));

							attributeConditions.put(attributeKey, betweenCondition);

						}
						System.out.println("Testing the condition: " + attributeKey + " value are " + high + " " + low);

					} else if (key.matches(".*\\b(From|To)$") || key.matches(".*\\b(FROM|TO)$")) {

						String lowerBound = null;
						String upperBound = null;

						if (key.matches(".*\\b(To)$") || key.matches(".*\\b(TO)$")) {
							upperBound = (String) payload.getSearchCriteria().get(key);
							String[] splitValue = key.split("To");
							String lowerBoundKey = splitValue[0].concat("From");

							if ("Is Create Date From".equals(lowerBoundKey)) {
								lowerBound = (String) payload.getSearchCriteria().get("Is Create Date From");
							} else if ("Year From".equals(lowerBoundKey)) {
								lowerBound = (String) payload.getSearchCriteria().get("Year From");
							} else {
								lowerBound = (String) payload.getSearchCriteria().get(lowerBoundKey);
							}

						}

						if (key.matches(".*\\b(From)$") || key.matches(".*\\b(FROM)$")) {
							lowerBound = (String) payload.getSearchCriteria().get(key);
							String[] splitValue = key.split("From");
							String upperBoundKey = splitValue[0].concat("To");

							if ("Is Create Date To".equals(upperBoundKey)) {
								upperBound = (String) payload.getSearchCriteria().get("Is Create Date To");
							} else if ("Year To".equals(upperBoundKey)) {
								lowerBound = (String) payload.getSearchCriteria().get("Year To");
							} else {
								upperBound = (String) payload.getSearchCriteria().get(upperBoundKey);
							}

						}
						if (lowerBound != null && upperBound != null) {
							Condition betweenCondition = new Condition()
									.withComparisonOperator(ComparisonOperator.BETWEEN)
									.withAttributeValueList(new AttributeValue().withS(lowerBound),
											new AttributeValue().withS(upperBound));
							attributeConditions.put(attributeKey, betweenCondition);
						} else if (lowerBound != null) {
							Condition betweenCondition = new Condition().withComparisonOperator(ComparisonOperator.GE)
									.withAttributeValueList(new AttributeValue().withS(lowerBound));
							attributeConditions.put(attributeKey, betweenCondition);
						} else if (upperBound != null) {
							Condition betweenCondition = new Condition().withComparisonOperator(ComparisonOperator.LE)
									.withAttributeValueList(new AttributeValue().withS(upperBound));
							attributeConditions.put(attributeKey, betweenCondition);
						}
					} else {
						if (value != null && !((String) value).isBlank()) {
							Condition condition = new Condition()
									.withComparisonOperator(ComparisonOperator.EQ.toString())
									.withAttributeValueList(new AttributeValue().withS(value.toString()));
							attributeConditions.put(attributeKey, condition);
						}
					}

				}
			});

			if (!errorList.isEmpty()) {
				throw new ValidationException("errors :- " + errorList);
			}

			scanExpression.setScanFilter(attributeConditions);

			log.info("query:- " + attributeConditions);

			list = mapper.scan(clazz, scanExpression);
		} else {

			list = mapper.scan(clazz, scanExpression);
		}

		if (list.isEmpty()) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		} else {

			List<Map<String, Object>> data = new ArrayList<>();

			list.forEach(value -> {
				log.info(" record:-" + value);

				try {
					data.add(getSearchFieldData((CommonFields) value));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});

			log.info("data from search:-" + data);
			log.info("data size :- " + data.size());

			if (data.isEmpty()) {
				return new PageImpl<>(new ArrayList<>(), pageable, 0);
			} else {

				int start = (int) pageable.getOffset();
				int end = Math.min(start + pageable.getPageSize(), data.size());

				PageImpl<Map<String, Object>> response = new PageImpl<>(data.subList(start, end), pageable,
						data.size());

				return response;
			}
		}
	}

	/**
	 * Gets the attribute name.
	 *
	 * @param name the name
	 * @return the attribute name
	 */
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
