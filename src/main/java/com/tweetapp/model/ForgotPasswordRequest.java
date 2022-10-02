package com.tweetapp.model;

import javax.validation.constraints.NotNull;

public class ForgotPasswordRequest {

	@NotNull(message = "Old Password Required")
	private String oldPassword;
	
	@NotNull(message = "New Password Required")
	private String newPassword;
	
	@NotNull(message = "Re-Enter New Password")
	private String confirmPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
