package com.sohlman.liferay.hook.organization.redirect;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.expando.kernel.model.ExpandoBridge;

@Component(
	immediate = true, property = {"key=login.events.post"},
	service = LifecycleAction.class
)
public class OrganizationRedirectPostLoginAction extends Action {

	@Override
	public void run(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ActionException {
		
		try {
			long userId = _portal.getUserId(httpServletRequest);
			
			List<Organization> organizations = _organizationLocalService.getUserOrganizations(userId);
			
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
			
		} catch (SystemException e) {
			_log.error(e);
		}
	}
	
	@Reference
	private OrganizationLocalService _organizationLocalService;
	
	@Reference
	private Portal _portal;
	
	private static Log _log = LogFactoryUtil
			.getLog(OrganizationRedirectPostLoginAction.class);
}
