package net.jplugin.ext.dict.api;

public class Dictionary {
	String value;//ֵ
	String label;//�ı�
	
	public Dictionary(String v,String l){
		this.value = v;
		this.label = l;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	
}
