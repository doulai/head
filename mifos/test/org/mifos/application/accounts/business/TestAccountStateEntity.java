package org.mifos.application.accounts.business;

import java.util.Set;

import org.mifos.framework.MifosTestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestAccountStateEntity extends MifosTestCase {
	
	private AccountStateEntity accountStateEntity;
	private Session session;

	protected void setUp() throws Exception {
		session = HibernateUtil.getSessionTL();
	}
	
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		session=null;		
	}
	
	public void testGetNameFailure() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
		String name = accountStateEntity.getName(Short.valueOf("1"));
		assertFalse("This should fail, name is Partial Application", !("Partial Application".equals(name)));
	}
	
	public void testGetNameSuccess() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
		String name = accountStateEntity.getName(Short.valueOf("1"));
		assertEquals("Partial Application",name);
	}
	
	public void testGetNamesSuccess() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
		Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = accountStateEntity.getNames();
		int size = lookUpValueLocaleEntitySet.size();
		assertEquals(1,size);
	}
	
	public void testGetNamesFailure() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("1"));
		Set<LookUpValueLocaleEntity> lookUpValueLocaleEntitySet = accountStateEntity.getNames();
		int size = lookUpValueLocaleEntitySet.size();
		assertFalse("This should fail, the size is 1", !(size == 1));
	}
	
	public void testGetNameWithLocaleSuccess() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("3"));
		String name = accountStateEntity.getName(Short.valueOf("1"));
		assertEquals(TestConstants.APPROVED,name);
	}
	
	public void testGetNameWithLocaleFailure() {
		accountStateEntity = getAccountStateEntityObject(Short.valueOf("3"));
		String name = accountStateEntity.getName(Short.valueOf("1"));
		assertFalse("This should fail, name is Approved",!(TestConstants.APPROVED.equals(name)));
	}
	
	private AccountStateEntity getAccountStateEntityObject(Short id) {
		Query query = session.createQuery("from org.mifos.application.accounts.business.AccountStateEntity ac_state where ac_state.id=?");
		query.setString(0,id.toString());
		AccountStateEntity accStateEntity = (AccountStateEntity) query.uniqueResult();
		return accStateEntity;
	}

}
