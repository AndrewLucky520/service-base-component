package com.talkweb.basecomp.address.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.talkweb.basecomp.address.service.AddressBookService;
import com.talkweb.basecomp.common.util.JSONUtil;


/**
 * @Description 通讯录Action层
 * @author xixi1979
 * @date 2018/8/1
 */
@Controller
@RequestMapping(value = "/addressBook/")
public class AddressBookController {
    private static final Logger log = LoggerFactory.getLogger(AddressBookService.class);
	
	@Autowired
	private AddressBookService addressBookService;

	@RequestMapping(value = "/getTeacherMenus", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getTeacherMenus(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getTeacherMenus(param);			
	}

	@RequestMapping(value = "/getStudentMenus", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getStudentMenus(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getStudentMenus(param);			
	}

	@RequestMapping(value = "/getSubMenus", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSubMenus(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getSubMenus(param);			
	}

	@RequestMapping(value = "/getTeacherTree", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getTeacherTree(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getTeacherTree(param);			
	}

	@RequestMapping(value = "/getStudentTree", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getStudentTree(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getStudentTree(param);			
	}
	
	@RequestMapping(value = "/getMemberList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getMemberList(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getMemberList(param);			
	}

	@RequestMapping(value = "/getParentList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getParentList(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getParentList(param);			
	}

	@RequestMapping(value = "/findMemberList", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject findMemberList(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.findMemberList(param);			
	}

	@RequestMapping(value = "/getMyAddressBook", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getMyAddressBook(HttpServletRequest req, @RequestBody JSONObject param) {
		return addressBookService.getMyAddressBook(param);			
	}

	@RequestMapping(value = "/storeMyAddressBook", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject storeMyAddressBook(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return addressBookService.insertMyAddressBook(param);	
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "收藏用户异常。", ex, log);
		}		
	}

	@RequestMapping(value = "/deleteMyAddressBook", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject deleteMyAddressBook(HttpServletRequest req, @RequestBody JSONObject param) {
		try {
			return addressBookService.deleteMyAddressBook(param);	
		} catch(Exception ex) {
			return JSONUtil.getResponse(param, "取消收藏异常。", ex, log);
		}		
	}

}
