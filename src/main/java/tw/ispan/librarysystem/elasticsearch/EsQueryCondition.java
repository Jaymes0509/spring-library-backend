package tw.ispan.librarysystem.elasticsearch;

public class EsQueryCondition {
    private String field;
    private String operator; // AND, OR, NOT
    private Object value;
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
} 