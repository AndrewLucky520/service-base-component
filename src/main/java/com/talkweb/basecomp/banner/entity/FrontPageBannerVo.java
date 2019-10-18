package com.talkweb.basecomp.banner.entity;

import java.util.Map;

public class FrontPageBannerVo {

	private String title;
	
	private String name;
	
	private Boolean showFlag;
	
	private Integer id;
	
	private Map form;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(Boolean showFlag) {
		this.showFlag = showFlag;
	}

	public Map getForm() {
		return form;
	}

	public void setForm(Map form) {
		this.form = form;
	}
	
}

