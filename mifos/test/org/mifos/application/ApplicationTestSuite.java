/**

 * ApplicationTestSuite.java    version: xxx



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.accounts.AccountTestSuite;
import org.mifos.application.accounts.financial.FinancialTestSuite;
import org.mifos.application.accounts.loan.LoanTestSuite;
import org.mifos.application.accounts.savings.SavingsTestSuite;
import org.mifos.application.bulkentry.BulkEntryTestSuite;
import org.mifos.application.collectionsheet.CollectionSheetTestSuite;
import org.mifos.application.configuration.LabelConfigurationTestSuite;
import org.mifos.application.customer.CustomerTestSuite;
import org.mifos.application.fees.FeeTestSuite;
import org.mifos.application.office.OfficeTestSuite;
import org.mifos.application.productdefinition.ProductDefinitionTestSuite;
import org.mifos.application.reports.ReportsTestSuite;
import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.components.ComponentsTestSuite;
import org.mifos.framework.components.configuration.ConfigurationTestSuite;
import org.mifos.framework.components.cronjob.CronjobTestSuite;
import org.mifos.framework.components.fieldConfiguration.FieldConfigurationTestSuite;
import org.mifos.framework.struts.plugin.InitializerPluginTest;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.util.helpers.MoneyTest;
import org.mifos.framework.util.helpers.StringToMoneyConverterTest;

public class ApplicationTestSuite extends MifosTestSuite {

	public ApplicationTestSuite() throws Exception {
	}

	public static void main(String[] args) throws Exception {
		Test testSuite = suite();
		TestRunner.run(testSuite);
	}

	public static Test suite() throws Exception {
		TestSuite suite = new ApplicationTestSuite();
		suite.addTest(CollectionSheetTestSuite.suite());
		suite.addTest(CustomerTestSuite.suite());
		suite.addTest(BulkEntryTestSuite.suite());
		suite.addTest(AccountTestSuite.suite());
		suite.addTest(FinancialTestSuite.suite());
		suite.addTestSuite(MoneyTest.class);
		suite.addTestSuite(StringToMoneyConverterTest.class);
		suite.addTestSuite(InitializerPluginTest.class);
		suite.addTest(ConfigurationTestSuite.suite());
		suite.addTest(CronjobTestSuite.suite());
		suite.addTest(LabelConfigurationTestSuite.suite());
		suite.addTest(LoanTestSuite.suite());
		suite.addTest(SavingsTestSuite.suite());
		suite.addTest(ProductDefinitionTestSuite.suite());
		suite.addTest(ReportsTestSuite.suite());
		suite.addTestSuite(TestConstPlugin.class);
		suite.addTest(FeeTestSuite.suite());
		suite.addTest(FieldConfigurationTestSuite.suite());
		suite.addTest(OfficeTestSuite.suite());
		suite.addTest(ComponentsTestSuite.suite());
		return suite;
	}

}
