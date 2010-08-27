/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trim;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION;
@SuppressWarnings("PMD")
public class QuestionGroupForm extends ScreenObject {
    private static final long serialVersionUID = -7545625058942409636L;

    private QuestionGroupDetail questionGroupDetail;
    private SectionDetailForm currentSection = new SectionDetailForm();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<String> selectedQuestionIds = new ArrayList<String>();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<SectionQuestionDetail> questionPool = new ArrayList<SectionQuestionDetail>();
    @javax.validation.Valid
    private Question currentQuestion = new Question(new QuestionDetail());
    private boolean addOrSelectFlag;

    public QuestionGroupForm() {
        this(new QuestionGroupDetail());
    }

    public QuestionGroupForm(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
    }

    public QuestionGroupDetail getQuestionGroupDetail() {
        return questionGroupDetail;
    }

    public String getTitle() {
        return questionGroupDetail.getTitle();
    }

    public void setTitle(String title) {
        this.questionGroupDetail.setTitle(trim(title));
    }
    
    public String getEventSourceId() {
        EventSourceDto eventSourceDto = this.questionGroupDetail.getEventSource();
        if (eventSourceDto == null || isEmpty(eventSourceDto.getEvent()) || isEmpty(eventSourceDto.getSource())) return null;
        return format("%s.%s", eventSourceDto.getEvent(), eventSourceDto.getSource());
    }

    public String getId() {
        return questionGroupDetail.getId().toString();
    }

    public void setEventSourceId(String eventSourceId) {
        if (StringUtils.isNotEmpty(eventSourceId) && !StringUtils.equals(DEFAULT_APPLIES_TO_OPTION, eventSourceId)) {
            String[] parts = eventSourceId.split("\\.");
            this.questionGroupDetail.setEventSource(new EventSourceDto(parts[0], parts[1], eventSourceId));
        }else{
            this.questionGroupDetail.setEventSource(new EventSourceDto(null, null, null));
        }
    }

    public EventSourceDto getEventSource() {
        return questionGroupDetail.getEventSource();
    }

    public void setEventSource(EventSourceDto eventSourceDto) {
        questionGroupDetail.setEventSource(eventSourceDto);
    }

    public List<SectionDetailForm> getSections() {
        List<SectionDetailForm> sectionDetails = new ArrayList<SectionDetailForm>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            sectionDetails.add(new SectionDetailForm(sectionDetail));
        }
        return sectionDetails;
    }

    public void setSections(List<SectionDetailForm> sections) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (SectionDetailForm sectionDetailForm : sections) {
            sectionDetails.add(sectionDetailForm.getSectionDetail());
        }
        questionGroupDetail.setSectionDetails(sectionDetails);
    }

    public void addCurrentSection() {
        currentSection.trimName();
        String sectionName = getSectionName();
        addCurrentSectionToSections();
        if (addOrSelectFlag) {
            addNewQuestion();
        } else {
            addSelectedQuestionsToCurrentSection();
        }
        currentSection = new SectionDetailForm();
        currentSection.setName(sectionName);
        selectedQuestionIds = new ArrayList<String>();
    }

    private void addSelectedQuestionsToCurrentSection() {
        List<SectionQuestionDetail> addedQuestions = new ArrayList<SectionQuestionDetail>();
        for (SectionQuestionDetail questionDetail : questionPool) {
            if (selectedQuestionIds.contains(String.valueOf(questionDetail.getQuestionId()))) {
                currentSection.addSectionQuestion(questionDetail);
                addedQuestions.add(questionDetail);
            }
        }
        questionPool.removeAll(addedQuestions);
    }

    private void addNewQuestion() {
        currentQuestion.trimTitleAndSetChoices();
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(currentQuestion.getQuestionDetail(), false);
        currentSection.addSectionQuestion(sectionQuestionDetail);
        currentQuestion = new Question(new QuestionDetail());
    }

    public boolean isDuplicateTitle(String questionTitle) {
        boolean result = false;
        if(StringUtils.isNotEmpty(questionTitle)){
            for (SectionQuestionDetail sectionQuestionDetail : getAllQuestionsInAllSections()) {
                if(StringUtils.equalsIgnoreCase(questionTitle, sectionQuestionDetail.getTitle())){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private List<SectionQuestionDetail> getAllQuestionsInAllSections() {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            sectionQuestionDetails.addAll(sectionDetail.getQuestions());
        }
        return sectionQuestionDetails;
    }

    private void addCurrentSectionToSections() {
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        for (SectionDetail sectionDetail : sectionDetails) {
            if (StringUtils.equalsIgnoreCase(sectionDetail.getName(), currentSection.getName())) {
                currentSection = new SectionDetailForm(sectionDetail);
                return;
            }
        }
        sectionDetails.add(currentSection.getSectionDetail());
    }

    public String getSectionName() {
        if (StringUtils.isEmpty(currentSection.getName())) {
            currentSection.setName(QuestionnaireConstants.DEFAULT_SECTION_NAME);
        }
        return currentSection.getName();
    }

    public void setSectionName(String sectionName) {
        currentSection.setName(sectionName);
    }

    public void removeSection(String sectionName) {
        SectionDetail sectionToDelete = null;
        List<SectionDetail> sectionDetails = questionGroupDetail.getSectionDetails();
        for (SectionDetail sectionDetail : sectionDetails) {
            if (StringUtils.equalsIgnoreCase(sectionName, sectionDetail.getName())) {
                sectionToDelete = sectionDetail;
                break;
            }
        }
        if (sectionToDelete != null) {
            markQuestionsOptionalAndReturnToPool(sectionToDelete);
            sectionDetails.remove(sectionToDelete);
        }
    }

    private void markQuestionsOptionalAndReturnToPool(SectionDetail sectionDetail) {
        List<SectionQuestionDetail> sectionQuestionDetails = sectionDetail.getQuestions();
        for(SectionQuestionDetail sectionQuestionDetail: sectionQuestionDetails){
            sectionQuestionDetail.setMandatory(false);
            questionPool.add(sectionQuestionDetail);
        }
    }

    public List<SectionQuestionDetail> getQuestionPool() {
        return questionPool;
    }

    public void setQuestionPool(List<SectionQuestionDetail> questionPool) {
        this.questionPool.clear();
        this.questionPool.addAll(questionPool);
    }

    public List<String> getSelectedQuestionIds() {
        return selectedQuestionIds;
    }

    public void setSelectedQuestionIds(List<String> selectedQuestionIds) {
        this.selectedQuestionIds = selectedQuestionIds;
    }

    public void removeQuestion(String sectionName, String questionId) {
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            if (StringUtils.equalsIgnoreCase(sectionName, sectionDetail.getName())) {
                removeQuestionFromSection(questionId, sectionDetail);
                if (sectionHasNoQuestions(sectionDetail)) {
                    removeSection(sectionName);
                }
                break;
            }
        }
    }

    public boolean hasQuestionsInCurrentSection() {
        return selectedQuestionIds.size()==0;
    }

    private boolean sectionHasNoQuestions(SectionDetail section) {
        return section.getQuestions().size() == 0;
    }

    private void removeQuestionFromSection(String questionId, SectionDetail section) {
        SectionQuestionDetail questionToRemove = null;
        List<SectionQuestionDetail> questions = section.getQuestions();
        for (SectionQuestionDetail question : questions) {
            if (StringUtils.equals(questionId, String.valueOf(question.getQuestionId()))) {
                questionToRemove = question;
                break;
            }
        }
        if (questionToRemove != null) {
            questionToRemove.setMandatory(false);
            questions.remove(questionToRemove);
            questionPool.add(questionToRemove);
        }
    }

    public boolean isEditable() {
        return questionGroupDetail.isEditable();
    }

    public void setEditable(boolean editable) {
       questionGroupDetail.setEditable(editable); 
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isAddOrSelectFlag() {
        return addOrSelectFlag;
    }

    public void setAddOrSelectFlag(boolean addOrSelectFlag) {
        this.addOrSelectFlag = addOrSelectFlag;
    }
}