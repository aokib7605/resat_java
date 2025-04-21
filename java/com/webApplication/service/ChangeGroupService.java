package com.webApplication.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.webApplication.entity.DataEntity;
import com.webApplication.repository.MainRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangeGroupService {
	private final HttpSession session;
	private final MainRepository mr;

	public void setPageInfo(Model model, Integer offset, String page) {
		model.addAttribute("title2", "団体一覧・参加");
		setGroupList(model, offset, page);

	}

	public Integer setGroupList(Model model, Integer offset, String page) {
		if(offset == null) {
			offset = 0;
		}
		if(page == null) {
			page = "";
		}
		if(page.equals("next")) {
			offset++;
		} else if(page.equals("prev")){
			offset--;
		}
		setViewData(model, offset);
		model.addAttribute("groupList", getGroupList(model, offset, page));
		return offset;
	}

	public Integer setSearchGroupList(Model model, String mode, String keyword, Integer offset, String page) {
		if(offset == null) {
			offset = 0;
		}
		if(page == null) {
			page = "";
		}
		if(page.equals("searchNext")) {
			offset++;
		} else if(page.equals("searchPrev")){
			offset--;
		}
		setSearchData(model, mode, keyword, offset);
		model.addAttribute("searchGroupList", searchGroup(model, mode, keyword, offset));
		return offset;
	}

	private ArrayList<DataEntity> getGroupList(Model model, Integer offset, String page){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		List<String> columns = Stream.concat(mr.getGroupesTableColumns().stream(), mr.getGroupAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		String joinTable = " left outer join group_login_list gll on g.sys_group_id = gll.sys_group_id left outer join group_authorities ga on  ga.authority_id = gll.group_authority";
		String limitWhere = joinTable + " where sys_user_id = '" + userData.getSys_user_id() + "' order by ga.authority_id desc limit 10 offset " + offset;
		String where = joinTable + " where sys_user_id = '" + userData.getSys_user_id() + "'";

		if(mr.getDataList("groupes g", columns, where).size() <= 0) {
			model.addAttribute("listMessage", "現在、団体に参加していません");
			return null;
		}

		if(mr.getDataList("groupes g", columns, where).size() - mr.getDataList("groupes g", columns, limitWhere).size() > 0) {
			model.addAttribute("next", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("prev", "前へ");
		}
		return mr.getDataList("groupes g", columns, limitWhere);
	}

	private ArrayList<DataEntity> searchGroup(Model model, String mode, String keyword, Integer offset) {
		String column = "";
		if(mode.equals("idSearch")) {
			column = "group_id";
		} else {
			column = "group_name";
		}

		List<String> columns = Stream.concat(mr.getGroupesTableColumns().stream(), mr.getGroupAuthorityTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		String joinTable = " left outer join group_login_list gll on g.sys_group_id = gll.sys_group_id left outer join group_authorities ga on  ga.authority_id = gll.group_authority";
		String limitWhere = joinTable + " where " + column + " like '%" + keyword + "%' order by ga.authority_id desc limit 5 offset " + offset;
		String where = joinTable + " where " + column + " like '%" + keyword + "%'";
		if(mr.getDataList("groupes g", columns, where).size() <= 0) {
			model.addAttribute("message", "検索条件に一致する団体は見つかりませんでした");
			return null;
		}

		if(mr.getDataList("groupes g", columns, where).size() - mr.getDataList("groupes g", columns, limitWhere).size() > 0) {
			model.addAttribute("searchNext", "次へ");
		}
		if(offset > 0) {
			model.addAttribute("searchPrev", "前へ");
		}
		return mr.getDataList("groupes g", columns, limitWhere);
	}

	private void setSearchData(Model model, String mode, String keyword, Integer offset) {
		model.addAttribute("mode", mode);
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchOffset", offset);
	}

	private void setViewData(Model model, Integer offset) {
		model.addAttribute("listOffset", offset);
	}
}
