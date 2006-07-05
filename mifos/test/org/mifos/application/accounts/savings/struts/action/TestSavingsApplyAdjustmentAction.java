package org.mifos.application.accounts.savings.struts.action;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsApplyAdjustmentAction extends MifosMockStrutsTestCase {
	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private SavingsTestHelper helper = new SavingsTestHelper();

	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);

	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		savings = null;
		TestObjectFactory.cleanUp(group);
		group = null;
		TestObjectFactory.cleanUp(center);
		center = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfullLoad_WithValidLastPaymentDeposit()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money depositAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				depositAmount, depositAmount, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(depositAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		assertNotNull(savings.getLastPmnt());
		assertNotNull(request.getSession().getAttribute(
				SavingsConstants.ACCOUNT_ACTION));
		AccountActionEntity accountAction = (AccountActionEntity) request
				.getSession().getAttribute(SavingsConstants.ACCOUNT_ACTION);
		assertEquals(AccountConstants.ACTION_SAVINGS_DEPOSIT, accountAction
				.getId().shortValue());
		assertEquals(Short.valueOf("1"), (Short) request.getSession()
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID));
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testSuccessfullLoad_WithValidLastPaymentWithdrawal()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money withdrawalAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000.0");
		Money balance = new Money(Configuration.getInstance().getSystemConfig()
				.getCurrency(), "2000.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				withdrawalAmount, balance, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balance);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		assertNotNull(savings.getLastPmnt());
		assertNotNull(request.getSession().getAttribute(
				SavingsConstants.ACCOUNT_ACTION));
		AccountActionEntity accountAction = (AccountActionEntity) request
				.getSession().getAttribute(SavingsConstants.ACCOUNT_ACTION);
		assertEquals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL, accountAction
				.getId().shortValue());
		assertEquals(Short.valueOf("1"), (Short) request.getSession()
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID));
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testSuccessfullLoad_WithoutValidLastPayment() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		assertNull(savings.getLastPmnt());
		assertNull(request.getSession().getAttribute(
				SavingsConstants.ACCOUNT_ACTION));
		assertNull(request.getSession().getAttribute(
				SavingsConstants.CLIENT_NAME));
		assertEquals(Short.valueOf("0"), (Short) request.getSession()
				.getAttribute(SavingsConstants.IS_LAST_PAYMENT_VALID));
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testSuccessfullPreviewSuccess() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "1000.0"), new Money(Configuration
						.getInstance().getSystemConfig().getCurrency(),
						"1000.0"), helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		addRequestParameter("note", "adjustmentComment");
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionMessages();
		verifyNoActionErrors();
		savings = (SavingsBO) request.getSession().getAttribute(
				Constants.BUSINESS_KEY);
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testSuccessfullPreviewFailure_NoLastPayment() {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		HibernateUtil.closeSession();
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] { "errors.invalidlastpayment" });
	}

	public void testSuccessfullPrevious() {
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
	}

	public void testSuccessfullCancel() {
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "cancel");
		actionPerform();
		verifyForward("account_detail_page");
		assertNull(request.getSession().getAttribute(
				SavingsConstants.ACCOUNT_ACTION));
		assertNull(request.getSession().getAttribute(
				SavingsConstants.CLIENT_NAME));
	}

	public void testSuccessfullAdjustUserPayment_AmountNullified()
			throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		savings = createSavingsAccount("000X00000000017", savingsOffering,
				group, AccountStates.SAVINGS_ACC_APPROVED);
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		Money depositAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), "1000.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				depositAmount, depositAmount, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(depositAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());
		assertEquals(Integer.valueOf(1).intValue(), savings.getLastPmnt()
				.getAccountTrxns().size());
		request.getSession().setAttribute(Constants.BUSINESS_KEY, savings);
		setRequestPathInfo("/savingsApplyAdjustmentAction.do");
		addRequestParameter("method", "adjustLastUserAction");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward("account_detail_page");
		HibernateUtil.closeSession();
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());
		assertEquals(Integer.valueOf(2).intValue(), savings.getLastPmnt()
				.getAccountTrxns().size());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private SavingsOfferingBO createSavingsOffering() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("SavingPrd1", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, CustomerBO group,
			short accountStateId) {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				accountStateId, new Date(), savingsOffering, userContext);
	}
}
