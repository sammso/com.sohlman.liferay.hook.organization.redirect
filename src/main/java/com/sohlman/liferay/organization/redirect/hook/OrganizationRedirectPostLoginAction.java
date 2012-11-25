package com.sohlman.liferay.organization.redirect.hook;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;

public class OrganizationRedirectPostLoginAction extends Action {

	@Override
	public void run(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ActionException {
		
		long userId = PortalUtil.getUserId(httpServletRequest);
		
		try {
			List<Organization> organizations = OrganizationLocalServiceUtil.getUserOrganizations(userId);
			
			for(Organization organization : organizations ) {
				ExpandoBridge expandoBridge = organization.getExpandoBridge();
				Serializable attribute = expandoBridge.getAttribute("redirectAddress",false);
				if(attribute!=null) {
					
					String path = String.valueOf(attribute);
					
					LastPath lastPath = new LastPath(StringPool.BLANK, path);

					HttpSession session = httpServletRequest.getSession();

					session.setAttribute(WebKeys.LAST_PATH, lastPath);
					return;
				}
				
			}
			
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
	}
	private static Log _log = LogFactoryUtil
			.getLog(OrganizationRedirectPostLoginAction.class);
}
