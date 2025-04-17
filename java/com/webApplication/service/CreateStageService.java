package com.webApplication.service;

import java.util.ArrayList;
import java.util.Arrays;
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
public class CreateStageService {
	private final HttpSession session;
	private final MainRepository mr;
	
	public void setPageInfo(Model model) {
		model.addAttribute("title2", "公演の新規作成");
		model.addAttribute("groupList", getGroupList());
	}
	
	public String inputSysGroupId(Model model, String sysGroupId) {
		model.addAttribute("sysGroupId", sysGroupId);
		model.addAttribute("mode", "inputStageId");
		return "inputStageId";
	}
	
	public String inputStageId(Model model, String back, String sysGroupId, String stageId) {
		setStageIdData(model, sysGroupId, stageId);
		if(back != null) {
			if(back.equals("back")) {
				model.addAttribute("mode", "inputSysGroupId");
				return "inputSysGroupId";
			}
		}
		List<String> columns = mr.getStagesTableColumns();
		String where = " where stage_id = '" + stageId + "'";
		if(mr.getData("stages", columns, where) != null) {
			model.addAttribute("message", "その公演IDは既に使用されています");
			model.addAttribute("mode", "inputStageId");
		} else {
			model.addAttribute("mode", "inputBaseData");
		}
		return "inputBaseData";
	}
	
	public String inputBaseData(Model model, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle) {
		setStageIdData(model, sysGroupId, stageId);
		setBaseData(model, stagePass, stageName, stageAttractCustomers, stageUrlTitle);
		if(back != null) {
			if(back.equals("back")) {
				model.addAttribute("mode", "inputStageId");
				return "inputSysGroupId";
			}
		}
		if(!stagePass.equals(rePass)) {
			model.addAttribute("message", "パスワードが一致していません");
			model.addAttribute("mode", "inputBaseData");
			return "inputBaseData";
		}
		model.addAttribute("placeList", getPlaceList());
		model.addAttribute("mode", "inputPlaceData");
		return "inputPlaceData";
	}
	
	public String inputPlaceData(Model model, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress) {
		return "uploadImages";
	}
	
	private ArrayList<DataEntity> getGroupList(){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		List<String> columns = Stream.concat(mr.getGroupe_login_listTableColumns().stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		String where = " gll left outer join groupes g on g.sys_group_id = gll.sys_group_id where sys_user_id = '" + userData.getSys_user_id() + "'";
		return mr.getDataList("group_login_list", columns, where);
	}
	
	private ArrayList<DataEntity> getPlaceList(){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"stage_place_name",
				"stage_place_address"
				));
		String where = " group by stage_place_name, stage_place_address";
		return mr.getDataListBySelectColumn("stages", columns, where);
	}
	
	private void setStageIdData(Model model, String sysGroupId, String stageId) {
		model.addAttribute("sysGroupId", sysGroupId);
		model.addAttribute("stageId", stageId);
	}
	
	private void setBaseData(Model model, String stagePass, String stageName, Integer stageAttractCustomers, String stageUrlTitle) {
		model.addAttribute("stagePass", stagePass);
		model.addAttribute("stageName", stageName);
		model.addAttribute("stageAttractCustomers", stageAttractCustomers);
		model.addAttribute("stageUrlTitle", stageUrlTitle);
	}
}
