package com.iflytek.dto;

import lombok.Data;

@Data
public class ApiResultDto {

	private int ok;
	private int err_no;
	private String failed;
	private String data;

}