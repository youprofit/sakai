/**
 * Copyright (c) 2008-2010 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakaiproject.profile2.tool.pages.panels;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.profile2.logic.ProfileLogic;
import org.sakaiproject.profile2.logic.SakaiProxy;
import org.sakaiproject.profile2.model.GalleryImage;
import org.sakaiproject.profile2.service.ProfileImageService;
import org.sakaiproject.profile2.tool.components.FocusOnLoadBehaviour;
import org.sakaiproject.profile2.tool.components.GalleryImageRenderer;
import org.sakaiproject.profile2.tool.pages.MyPictures;
import org.sakaiproject.profile2.tool.pages.MyProfile;
import org.sakaiproject.profile2.util.ProfileConstants;

/**
 * Gallery component for viewing a gallery image alongside options, including
 * removing the image and setting the image as the new profile image.
 */
public class GalleryImageEdit extends Panel {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GalleryImageEdit.class);

	private final WebMarkupContainer imageOptionsContainer;
	private final WebMarkupContainer removeConfirmContainer;
	
	@SpringBean(name="org.sakaiproject.profile2.logic.SakaiProxy")
	private SakaiProxy sakaiProxy;
	
	@SpringBean(name="org.sakaiproject.profile2.logic.ProfileLogic")
	private ProfileLogic profileLogic;
	
	@SpringBean(name="org.sakaiproject.profile2.service.ProfileImageService")
	private ProfileImageService profileImageService;

	public GalleryImageEdit(String id, final ModalWindow mainImageWindow,
			final String userId, final GalleryImage image,
			final int galleryPageIndex) {

		super(id);

		log.debug("GalleryImageEdit()");

		// feedback label for user alert in event remove/set actions fail
		final Label formFeedback = new Label("formFeedback");
		formFeedback.setOutputMarkupPlaceholderTag(true);
		add(formFeedback);

		Form imageEditForm = new Form("galleryImageEditForm");

		imageEditForm.setOutputMarkupId(true);
		add(imageEditForm);

		imageOptionsContainer = new WebMarkupContainer("galleryImageOptionsContainer");
		imageOptionsContainer.setOutputMarkupId(true);
		imageOptionsContainer.setOutputMarkupPlaceholderTag(true);

		imageOptionsContainer.add(new Label("removePictureLabel",
				new ResourceModel("pictures.removepicture")));

		AjaxFallbackButton removePictureButton = createRemovePictureButton(imageEditForm);
		imageOptionsContainer.add(removePictureButton);

		imageOptionsContainer.add(new Label("setProfileImageLabel",
				new ResourceModel("pictures.setprofileimage")));

		AjaxFallbackButton setProfileImageButton = createSetProfileImageButton(
				mainImageWindow, userId, image, galleryPageIndex,
				imageEditForm, formFeedback);

		imageOptionsContainer.add(setProfileImageButton);

		imageEditForm.add(imageOptionsContainer);

		removeConfirmContainer = new WebMarkupContainer("galleryRemoveImageConfirmContainer");
		removeConfirmContainer.setOutputMarkupId(true);
		removeConfirmContainer.setOutputMarkupPlaceholderTag(true);

		Label removeConfirmLabel = new Label("removePictureConfirmLabel",
				new ResourceModel("pictures.removepicture.confirm"));
		removeConfirmContainer.add(removeConfirmLabel);

		AjaxFallbackButton removeConfirmButton = createRemoveConfirmButton(
				mainImageWindow, userId, image, galleryPageIndex, formFeedback,
				imageEditForm);
		removeConfirmButton.add(new FocusOnLoadBehaviour());
		removeConfirmContainer.add(removeConfirmButton);

		AjaxFallbackButton removeCancelButton = createRemoveCancelButton(imageEditForm);
		removeConfirmContainer.add(removeCancelButton);

		removeConfirmContainer.setVisible(false);
		imageEditForm.add(removeConfirmContainer);

		add(new GalleryImageRenderer("galleryImageMainRenderer", true, image
				.getMainResource()));
		
		// make sure remove confirm container not displayed when reopening
		mainImageWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {

			public void onClose(AjaxRequestTarget target) {

				imageOptionsContainer.setVisible(true);
				removeConfirmContainer.setVisible(false);
				target.addComponent(imageOptionsContainer);
			}
			
		});
	}

	private AjaxFallbackButton createRemoveCancelButton(Form imageEditForm) {
		AjaxFallbackButton removeCancelButton = new AjaxFallbackButton(
				"galleryRemoveImageCancelButton", new ResourceModel(
						"button.cancel"), imageEditForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.prependJavascript("$('#"
						+ removeConfirmContainer.getMarkupId() + "').hide();");

				imageOptionsContainer.setVisible(true);
				target.addComponent(imageOptionsContainer);

				target.appendJavascript("setMainFrameHeight(window.name);");
			}

		};
		return removeCancelButton;
	}

	private AjaxFallbackButton createRemoveConfirmButton(
			final ModalWindow mainImageWindow, final String userId,
			final GalleryImage image, final int galleryPageIndex,
			final Label formFeedback, Form imageEditForm) {
		AjaxFallbackButton removeConfirmButton = new AjaxFallbackButton(
				"galleryRemoveImageConfirmButton", new ResourceModel(
						"button.gallery.remove.confirm"), imageEditForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (profileImageService.removeProfileGalleryImage(
						userId, image)) {

					// close modal window
					mainImageWindow.close(target);

					if (sakaiProxy.isSuperUserAndProxiedToUser(
							userId)) {
						setResponsePage(new MyPictures(galleryPageIndex, userId));
					} else {
						setResponsePage(new MyPictures(galleryPageIndex));
					}
				} else {
					// user alert
					formFeedback.setDefaultModel(new ResourceModel(
							"error.gallery.remove.failed"));
					formFeedback.add(new AttributeModifier("class", true,
							new Model("alertMessage")));

					target.addComponent(formFeedback);
				}
			}
		};
		return removeConfirmButton;
	}

	private AjaxFallbackButton createRemovePictureButton(Form imageEditForm) {
		AjaxFallbackButton removePictureButton = new AjaxFallbackButton(
				"galleryImageRemoveButton", new ResourceModel(
						"button.gallery.remove"), imageEditForm) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {

				imageOptionsContainer.setVisible(false);

				target.prependJavascript("$('#"
						+ imageOptionsContainer.getMarkupId() + "').hide();");

				removeConfirmContainer.setVisible(true);
				target.addComponent(removeConfirmContainer);
				target.prependJavascript("setMainFrameHeight(window.name);");
			}

		};
		return removePictureButton;
	}

	private AjaxFallbackButton createSetProfileImageButton(
			final ModalWindow mainImageWindow, final String userId,
			final GalleryImage image, final int galleryPageIndex,
			Form imageOptionsForm, final Label formFeedback) {

		AjaxFallbackButton setProfileImageButton = new AjaxFallbackButton(
				"galleryImageSetProfileButton", new ResourceModel(
						"button.gallery.setprofile"), imageOptionsForm) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form form) {

				if (profileImageService.setProfileImage(
						userId,
						sakaiProxy.getResource(
								image.getMainResource()), "", "")) {

					sakaiProxy.postEvent(
							ProfileConstants.EVENT_PROFILE_IMAGE_CHANGE_UPLOAD,
							"/profile/" + userId, true);

					// close modal window
					mainImageWindow.close(target);

					if (sakaiProxy.isSuperUserAndProxiedToUser(
							userId)) {
						setResponsePage(new MyProfile(userId));
					} else {
						setResponsePage(new MyProfile());
					}

				} else {
					// user alert
					formFeedback.setDefaultModel(new ResourceModel(
							"error.gallery.setprofile.failed"));
					formFeedback.add(new AttributeModifier("class", true,
							new Model("alertMessage")));

					target.addComponent(formFeedback);
				}
			}
		};
		return setProfileImageButton;
	}

}
