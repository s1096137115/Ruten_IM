package com.avengers.publicim.data.third_party;

import com.google.gson.annotations.SerializedName;

/**
 * Created by D-IT-MAX2 on 2017/1/4.
 */

public class AppInfo {
	@SerializedName("AppID")
	private String applicationId;

	@SerializedName("ServiceName")
	private String serviceNmae;

	@SerializedName("Domain")
	private String domain;

	@SerializedName("CreateUrl")
	private String createUrl;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getServiceNmae() {
		return serviceNmae;
	}

	public void setServiceNmae(String serviceNmae) {
		this.serviceNmae = serviceNmae;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCreateUrl() {
		return createUrl;
	}

	public void setCreateUrl(String createUrl) {
		this.createUrl = createUrl;
	}
}
