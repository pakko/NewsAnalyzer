package com.ml.model;

import org.springframework.data.annotation.Id;

public class Cluster {
	@Id
	private String clusterId;
	private String categoryId;
	private int clusterNum;
	
	public Cluster() {}
	
	public Cluster(String clusterId, String categoryId, int clusterNum) {
		super();
		this.clusterId = clusterId;
		this.categoryId = categoryId;
		this.clusterNum = clusterNum;
	}
	
	@Override
	public String toString() {
		return "Cluster [clusterId=" + clusterId + ", categoryId=" + categoryId
				+ ", clusterNum=" + clusterNum + "]";
	}

	public String getClusterId() {
		return clusterId;
	}
	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public int getClusterNum() {
		return clusterNum;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}
	
	
}
