package com.kadirdurmazz.gymy;

public class Item {

    private String fieldDate;
    private String id;
    private String field1;
    private String field2;
    private String field3;
    private String field4;
    private String field5;

    public Item(String fieldDate, String id, String field1, String field2, String field3, String field4, String field5) {
        this.fieldDate = fieldDate;
        this.id = id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
    }

    public Item(){

    }

    //Is this an override?
    public Item(String fieldDate, String field1, String field2, String field3, String field4, String field5) {
    }

    public String getFieldDate() {
        return fieldDate;
    }

    public void setFieldDate(String fieldDate) {
        this.fieldDate = fieldDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }

    public String getField5() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5 = field5;
    }
}
