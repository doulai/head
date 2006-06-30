package org.mifos.application.customer.util.helpers;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.business.View;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class CustomerRecentActivityView extends View {

	public CustomerRecentActivityView(java.util.Date activityDate,String description,Money amount,String postedBy){
		this.activityDate = new java.sql.Date(activityDate.getTime());
		this.description = description;
		this.amount = amount;
		this.postedBy=postedBy;
	}

	public CustomerRecentActivityView(){
	}
	
	private Date activityDate;
	
	private String description;
	
	private Money amount;
	
	private String postedBy="-";
	
	private Locale locale=null;
	
	private String userPrefferedDate=null;
	
	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	public Money getAmount() {
		return removeSign(amount);
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public String getUserPrefferedDate() {
		return DateHelper.getUserLocaleDate(getLocale(),getActivityDate().toString()); 
	}


	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
}
