package tw.ispan.librarysystem.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class SearchCondition {
    private String field;
    private String operator; // AND, OR, NOT
    private JsonNode value;

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator;     }

    public JsonNode getValue() { return value; }
    public void setValue(JsonNode value) { this.value = value; }
} 