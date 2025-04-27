package com.webApplication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormEntity {
	
	private String formUrl = Env.domainName + "/reserve/inputForm?form=<STAGE_URL_TITLE>&sys=<SYS_FORM_ID>&ma=<SYS_USER_ID>";
	private String sysUserId;
	private String userName;
	private String sysStageId;
	private String stageName;
	
	private String sysFormId;
	private String formName;

}
