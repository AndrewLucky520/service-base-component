package com.talkweb.basecomp.banner.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.banner.entity.BannerInfo;
import com.talkweb.basecomp.banner.entity.BannerRelation;

public interface BannerDao {
	int insertRelationBatch(List<BannerRelation> list);

	int insertBannerInfo(BannerInfo bannerInfo);

	List<JSONObject> getBannerInfo(JSONObject param);

	List<JSONObject> getBannerByPublish(JSONObject param);

	List<JSONObject> getBannerRelation(JSONObject param);

	int updateBanner(BannerInfo frontPageBanner);

	int deleteBanner(JSONObject param);

	int deleteBannerRelation(JSONObject param);

	List<JSONObject> getSchoolList(JSONObject param);
	
	List<JSONObject> queryGradeList(JSONObject param);
}
