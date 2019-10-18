package com.talkweb.basecomp.banner.entity;

import java.util.Date;

public class BannerInfo {

	private Integer id;
	
	private String imgSource;
	
	private String schoolId;
	
	private String urlPath;
	
	private Integer	status;
	
	private String publishUser;
	
	private Date publishTime;
	
	private String updateUser;
	
	private Date updateTime;
	
	private String type;
	
	private Integer pathType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getImgSource() {
		return imgSource;
	}

	public void setImgSource(String imgSource) {
		this.imgSource = imgSource;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPublishUser() {
		return publishUser;
	}

	public void setPublishUser(String publishUser) {
		this.publishUser = publishUser;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getPathType() {
		return pathType;
	}

	public void setPathType(Integer pathType) {
		this.pathType = pathType;
	}
	
}
