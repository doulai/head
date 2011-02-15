/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.reports.business.validator;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.platform.validations.Errors;
import org.mifos.reports.business.BranchReportParameterForm;
import org.mifos.reports.business.service.IBranchReportService;
import org.mifos.reports.util.helpers.ReportValidationConstants;

public class BranchReportParamValidator extends AbstractReportParameterValidator<BranchReportParameterForm> {
    private final IBranchReportService branchReportService;

    public BranchReportParamValidator(List<String> applicableReportFilePaths, IBranchReportService branchReportService) {
        super(applicableReportFilePaths);
        this.branchReportService = branchReportService;
    }

    @Override
    public void validate(BranchReportParameterForm form, Errors errors) {
        super.validate(form, errors);
        if (errors.hasErrors()) {
            return;
        }
        if (!branchReportService.isReportDataPresentForRundateAndBranchId(form.getBranchId(), form.getRunDate())) {
            errors.addError(ReportValidationConstants.RUN_DATE_PARAM,
                    ReportValidationConstants.BRANCH_REPORT_NO_DATA_FOUND_MSG);
        }
    }

    public BranchReportParameterForm buildReportParameterForm(HttpServletRequest request) {
        return BranchReportParameterForm.build(request);
    }
}
