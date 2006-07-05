package org.mifos.framework.components.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.meeting.util.resources.MeetingConstants;

public class TestScheduler extends MifosTestCase{
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private SavingsTestHelper helper = new SavingsTestHelper();
	
	public void testGetNextScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		SchedulerIntf scheduler = helper.getScheduler(helper.getMeeting(MeetingConstants.MONTHLY,Short.valueOf("2"),Short.valueOf("2")),1);
		Date resultDate = scheduler.getNextScheduleDateAfterRecurrence(df.parse("01/01/2006"));
		assertEquals(df.parse("01/03/2006"),resultDate);
	}

	public void testGetNextScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		SchedulerIntf scheduler = helper.getScheduler(helper.getMeeting(MeetingConstants.MONTHLY,Short.valueOf("2"),Short.valueOf("2")),31);
		Date resultDate = scheduler.getNextScheduleDateAfterRecurrence(df.parse("01/01/2006"));
		assertEquals(df.parse("31/03/2006"),resultDate);
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		SchedulerIntf scheduler = helper.getScheduler(helper.getMeeting(MeetingConstants.MONTHLY,Short.valueOf("2"),Short.valueOf("2")),1);
		Date resultDate = scheduler.getPrevScheduleDateAfterRecurrence(df.parse("01/05/2006"));
		assertEquals(df.parse("01/03/2006"),resultDate);
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		SchedulerIntf scheduler = helper.getScheduler(helper.getMeeting(MeetingConstants.MONTHLY,Short.valueOf("2"),Short.valueOf("2")),31);
		Date resultDate = scheduler.getPrevScheduleDateAfterRecurrence(df.parse("31/05/2006"));
		assertEquals(df.parse("31/03/2006"),resultDate);
	}
}
