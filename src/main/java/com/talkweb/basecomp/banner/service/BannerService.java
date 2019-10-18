package com.talkweb.basecomp.banner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.banner.dao.BannerDao;
import com.talkweb.basecomp.banner.entity.BannerInfo;
import com.talkweb.basecomp.banner.entity.BannerRelation;

@Service
public class BannerService{
	
	@Autowired
	private BannerDao bannerDao;
	
	public int insertRelationBatch(List<BannerRelation> list) {
		return bannerDao.insertRelationBatch(list);
	}

	public int insertBannerInfo(BannerInfo bannerInfo) {
		return bannerDao.insertBannerInfo(bannerInfo);
	}

	public List<JSONObject> getBannerInfo(JSONObject param) {
		return bannerDao.getBannerInfo(param);
	}

	public List<JSONObject> getBannerByPublish(JSONObject param) {
		return bannerDao.getBannerByPublish(param);
	}

	public List<JSONObject> getBannerRelation(JSONObject param) {
		return bannerDao.getBannerRelation(param);
	}

	public int updateBanner(BannerInfo frontPageBanner) {
		return bannerDao.updateBanner(frontPageBanner);
	}

	public int deleteBanner(JSONObject param) {
		return bannerDao.deleteBanner(param);
	}

	public int deleteBannerRelation(JSONObject param) {
		return bannerDao.deleteBannerRelation(param);
	}

	public List<JSONObject> getSchoolList(JSONObject param) {
		return bannerDao.getSchoolList(param);
	}
	
	public List<JSONObject> queryGradeList(JSONObject param) {
		return bannerDao.queryGradeList(param);
	}
}
