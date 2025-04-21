package com.webApplication.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.webApplication.entity.DataEntity;
import com.webApplication.functions.Pub;
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
		model.addAttribute("placeList", getPlaceList(""));
		model.addAttribute("mode", "inputPlaceData");
		return "inputPlaceData";
	}

	public String inputPlaceData(Model model, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress, String keyword) {
		setStageIdData(model, sysGroupId, stageId);
		setBaseData(model, stagePass, stageName, stageAttractCustomers, stageUrlTitle);
		setPlaceData(model, stagePlaceName, stagePlaceAddress, keyword);
		if(back != null) {
			if(back.equals("back")) {
				model.addAttribute("mode", "inputBaseData");
				return "inputBaseData";
			}
		}
		if(keyword != null && !keyword.equals("")) {
			model.addAttribute("placeList", getPlaceList(keyword));
			model.addAttribute("mode", "inputPlaceData");
			return "inputPlaceData";
		}
		if (stagePlaceAddress.equals("selected")) {
			// 正規表現で「末尾の括弧内住所」を抽出
			Pattern pattern = Pattern.compile("^(.*)[\\(（](.+)[\\)）]$");
			Matcher matcher = pattern.matcher(stagePlaceName);

			if (matcher.matches()) {
				// 会場名と住所を振り分ける
				stagePlaceName = matcher.group(1).trim();
				stagePlaceAddress = matcher.group(2).trim();
				setPlaceData(model, stagePlaceName, stagePlaceAddress, keyword);
			}
		}
		model.addAttribute("mode", "uploadImages");
		return "uploadImages";
	}

	public String uploadImages(Model model, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress, String keyword, MultipartFile file1, MultipartFile file2) throws IOException {
		setStageIdData(model, sysGroupId, stageId);
		setBaseData(model, stagePass, stageName, stageAttractCustomers, stageUrlTitle);
		setPlaceData(model, stagePlaceName, stagePlaceAddress, keyword);
		setImagesSession(model, file1, file2);
		if(back != null && back.equals("back")) {
			model.addAttribute("mode", "inputPlaceData");
			model.addAttribute("placeList", getPlaceList(""));
			return "inputPlaceData";
		}
		model.addAttribute("groupName", getGroup(sysGroupId).getGroup_name());
		if(file1 != null) {
			model.addAttribute("file1Name", file1.getOriginalFilename());
		}
		if(file2 != null) {
			model.addAttribute("file2Name", file2.getOriginalFilename());
		}
		model.addAttribute("mode", "confiResult");
		return "confiResult";

	}
	
	public String confiResult(Model model, String back, String sysGroupId, String stageId, String stagePass, String rePass, String stageName, 
			Integer stageAttractCustomers, String stageUrlTitle, String stagePlaceName, String stagePlaceAddress, String keyword) {
		String file1 = (String)session.getAttribute("file1Name");
		String file2 = (String)session.getAttribute("file2Name");
		setStageIdData(model, sysGroupId, stageId);
		setBaseData(model, stagePass, stageName, stageAttractCustomers, stageUrlTitle);
		setPlaceData(model, stagePlaceName, stagePlaceAddress, keyword);
		if(back != null && back.equals("back")) {
			model.addAttribute("mode", "uploadImages");
			return "uploadImages";
		}
		try {
			addImages(file1, file2);
			int num = 1;
			String file1Id = getSysImageId(file1, num);
			if(file1.equals(file2)) {
				num = 2;
			}
			String file2Id = getSysImageId(file2, num);
			createStage(sysGroupId, stageId, stagePass, stageName, stageAttractCustomers, stageUrlTitle, stagePlaceName, stagePlaceAddress, file1Id, file2Id);
			DataEntity stageData = setSession(model, stageId);
			DataEntity userData = updateUserDefStage(model, stageData.getSys_stage_id());
			createCastTable(model, stageData.getSys_stage_id());
			createStaffTable(model, stageData.getSys_stage_id());
			addStageLoginList(model, stageData.getSys_stage_id(), userData.getSys_user_id());
		} catch (Exception e) {
			model.addAttribute("message", "公演登録時にエラーが発生しました");
		}
		return "index";
	}

	private ArrayList<DataEntity> getGroupList(){
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		List<String> columns = Stream.concat(mr.getGroupeLoginListTableColumns().stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		String where = " gll left outer join groupes g on g.sys_group_id = gll.sys_group_id where sys_user_id = '" + userData.getSys_user_id() + "'";
		return mr.getDataList("group_login_list", columns, where);
	}

	private ArrayList<DataEntity> getPlaceList(String keyword){
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"stage_place_name",
				"stage_place_address"
				));
		String where = " where stage_place_name like '%" + keyword + "%' group by stage_place_name, stage_place_address";
		return mr.getDataListBySelectColumn("stages", columns, where);
	}
	
	private DataEntity getGroup(String sysGroupId) {
		List<String> columns = mr.getGroupesTableColumns();
		String where = " where sys_group_id = '" + sysGroupId + "'";
		return mr.getData("groupes", columns, where);
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

	private void setPlaceData(Model model, String stagePlaceName, String stagePlaceAddress, String keyword) {
		model.addAttribute("stagePlaceName", stagePlaceName);
		model.addAttribute("stagePlaceAddress", stagePlaceAddress);
		model.addAttribute("keyword", keyword);
	}

	private void setImagesSession(Model model, MultipartFile file1, MultipartFile file2) throws IOException {
		session.setAttribute("file1Name", file1.getOriginalFilename());
		session.setAttribute("file1Type", file1.getContentType());
		session.setAttribute("file1Bytes", file1.getBytes());
		session.setAttribute("file2Name", file2.getOriginalFilename());
		session.setAttribute("file2Type", file2.getContentType());
		session.setAttribute("file2Bytes", file2.getBytes());
	}
	
	private void addImages(String file1, String file2) throws IOException {
		if(file1 != null) {
			String file1Name = (String)session.getAttribute("file1Name");
			String file1Type = (String)session.getAttribute("file1Type");
			byte[] file1Bytes = (byte[])session.getAttribute("file1Bytes");
			addImage(file1Name, file1Type, file1Bytes);
		}
		if(file2 != null) {
			String file2Name = (String)session.getAttribute("file2Name");
			String file2Type = (String)session.getAttribute("file2Type");
			byte[] file2Bytes = (byte[])session.getAttribute("file2Bytes");
			addImage(file2Name, file2Type, file2Bytes);
		}
	}
	
	private void addImage(String fileName, String contentType, byte[] bytes) throws IOException {
	    mr.insertImage(Pub.createUuid(), fileName, contentType, bytes);
	}
	
	private String getSysImageId(String fileName, int num) {
		try {
			DataEntity image = mr.getData("images", mr.getImagesTableColumns(), " where file_name = '" + fileName + "' limit 1 offset " + num);
			return image.getSys_image_id();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private void createStage(String sysGroupId, String stageId, String stagePass, String stageName, Integer stageAttractCustomers, String stageUrlTitle,
			String stagePlaceName, String stagePlaceAddress, String file1Id, String file2Id) {
		try {
			List<String> columns = mr.getStagesTableColumns();
			List<String> values = new ArrayList<String>(Arrays.asList(
					Pub.createUuid(),//sys_stage_id
					sysGroupId,//sys_group_id
					stageId,//stage_id
					stagePass,//stage_pass
					stageName,//stage_name
					stageAttractCustomers + "",//stage_attract_customers
					stageUrlTitle,//stage_url_title
					stagePlaceName,//stage_place_name
					null,//`stage_open_minutes` varchar(64) DEFAULT NULL,
					null,//`stage_runtime` varchar(20) DEFAULT NULL,
					null,//`stage_story` varchar(400) DEFAULT NULL,
					Pub.getCurrentDate() + "",//stage_cre_date
					0 + "",//stage_opener
					file1Id,//stage_flyer_1
					file2Id,//stage_flyer_2
					stagePlaceAddress//stage_place_address
					));
			mr.insertData("stages", columns, values);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private DataEntity setSession(Model model, String stageId) {
		List<String> columns = Stream.concat(mr.getStagesTableColumns().stream(), mr.getImagesTableColumns().stream()).collect(Collectors.toList());
		String where = " left outer join images i1 on stage_flyer_1 = i1.sys_image_id"
				+ " left outer join images i2 on stage_flyer_2 = i2.sys_image_id"
				+ " where stage_id =  '" + stageId + "'";
		DataEntity stageData = mr.getData("stages", columns, where);
		session.setAttribute("defStSession", stageData);
		return stageData;
	}
	
	private DataEntity updateUserDefStage(Model model, String sysStageId) {
		DataEntity userData = (DataEntity)session.getAttribute("userSession");
		mr.updateData("users", "user_def_stage", sysStageId, " where sys_user_id = '" + userData.getSys_user_id() + "'");
		List<String> columns = Stream.concat(mr.getUsersTableColumns().stream(), mr.getGroupeLoginListTableColumns().stream()).collect(Collectors.toList());
		columns = Stream.concat(columns.stream(), mr.getGroupesTableColumns().stream()).collect(Collectors.toList());
		String where = "left outer join groupes on sys_group_id = user_def_group where sys_user_id =  '" + userData.getSys_user_id() + "'";
		userData = mr.getData("users u", columns, where);
		session.setAttribute("userSession", userData);
		return userData;
	}
	
	private void addStageLoginList(Model model, String sysStageId, String sysUserId) {
		List<String> columns = mr.getStageLoginListTableColumns();
		List<String> values = new ArrayList<String>(Arrays.asList(
				sysStageId,		//sys_stage_id
				sysUserId,		//sys_user_id
				1 + ""			//stage_authority
				));
		mr.insertData("stage_login_list", columns, values);
	}
	
	private void createCastTable(Model model, String sysStageId) {
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"`sys_user_id` varchar(20) DEFAULT NULL",
				"`cast_chara_name` varchar(64) DEFAULT NULL",
				"`cast_sort_num` int DEFAULT NULL"
				));
		mr.createTable("cast_" + sysStageId, columns, "");
	}
	
	private void createStaffTable(Model model, String sysStageId) {
		List<String> columns = new ArrayList<String>(Arrays.asList(
				"`sys_user_id` varchar(20) DEFAULT NULL",
				"`staff_dep_name` varchar(64) DEFAULT NULL",
				"`staff_sort_num` int DEFAULT NULL"
				));
		mr.createTable("staff_" + sysStageId, columns, "");
	}
}
