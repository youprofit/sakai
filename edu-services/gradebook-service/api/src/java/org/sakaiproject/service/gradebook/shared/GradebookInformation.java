/**********************************************************************************
*
* $Id$
*
***********************************************************************************
*
 * Copyright (c) 2014 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.service.gradebook.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GradebookInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String selectedGradingScaleUid;
	private Map<String, Double> selectedGradingScaleBottomPercents;
	private Collection<Assignment> assignments;
	private boolean displayReleasedGradeItemsToStudents;
	
	private int gradeType;
	private int categoryType;
	private List category;
	private String gradeScale;
	
	
	public String getSelectedGradingScaleUid() {
		return selectedGradingScaleUid;
	}
	public void setSelectedGradingScaleUid(String selectedGradingScaleUid) {
		this.selectedGradingScaleUid = selectedGradingScaleUid;
	}
	public Map<String, Double> getSelectedGradingScaleBottomPercents() {
		return selectedGradingScaleBottomPercents;
	}
	public void setSelectedGradingScaleBottomPercents(
			Map<String, Double> selectedGradingScaleBottomPercents) {
		this.selectedGradingScaleBottomPercents = selectedGradingScaleBottomPercents;
	}
	public Collection<Assignment> getAssignments() {
		return assignments;
	}
	public void setAssignments(Collection<Assignment> assignments) {
		this.assignments = assignments;
	}
	public boolean isDisplayReleasedGradeItemsToStudents() {
		return displayReleasedGradeItemsToStudents;
	}
	public void setDisplayReleasedGradeItemsToStudents(
			boolean displayReleasedGradeItemsToStudents) {
		this.displayReleasedGradeItemsToStudents = displayReleasedGradeItemsToStudents;
	}
	public int getGradeType() {
		return gradeType;
	}
	public void setGradeType(int gradeType) {
		this.gradeType = gradeType;
	}
	public int getCategoryType() {
		return categoryType;
	}
	public void setCategoryType(int categoryType) {
		this.categoryType = categoryType;
	}
	public List getCategory() {
		return category;
	}
	public void setCategory(List category) {
		this.category = category;
	}
	public String getGradeScale() {
		return gradeScale;
	}
	public void setGradeScale(String gradeScale) {
		this.gradeScale = gradeScale;
	}

}
