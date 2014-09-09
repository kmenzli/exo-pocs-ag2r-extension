package org.exoplatform.addons.navigation;

import org.apache.commons.lang.ArrayUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.navigation.component.utils.DashboardUtils;
import org.exoplatform.platform.webui.NavigationURLUtils;
import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.user.UserNode;
import org.exoplatform.portal.mop.user.UserNodeFilterConfig;
import org.exoplatform.portal.webui.portal.UIPortal;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.service.LinkProvider;
import org.exoplatform.social.webui.Utils;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.core.UIPortletApplication;
import org.exoplatform.webui.core.lifecycle.UIApplicationLifecycle;

/**
 * Created by kmenzli on 09/09/2014.
 */
@ComponentConfig(lifecycle = UIApplicationLifecycle.class,

        template = "app:/groovy/exoplatform/addons/ag2r/AG2RUserNavigationPortlet.gtmpl"
)
public class UIAG2RUserNavigationPortlet extends UIPortletApplication {

    private static final Log LOG = ExoLogger.getLogger(UIAG2RUserNavigationPortlet.class);
    public static String DEFAULT_PORTAL_OWNER = "classic";
    public static final String ROUTE_DELIMITER = "/";
    public static final String ACTIVITIES_URI= "activities";
    public static final String PROFILE_URI= "profile";
    public static final String CONNEXIONS_URI= "connections";
    public static final String WIKI_URI= "wiki";
    public static final String DASHBOARD_URI= "dashboard";
    private UserNodeFilterConfig toolbarFilterConfig;
    private static final String POPUP_AVATAR_UPLOADER = "UIAvatarUploaderPopup";
    public static String DEFAULT_TAB_NAME = "Tab_Default";
    private static final String USER ="/user/"  ;
    private static final String WIKI_HOME = "/WikiHome";
    private static final String WIKI_REF ="wiki" ;
    private static final String MYSPACES_URI = "spaces";

    public UIAG2RUserNavigationPortlet() throws Exception {
        UserNodeFilterConfig.Builder builder = UserNodeFilterConfig.builder();
        builder.withReadWriteCheck().withVisibility(Visibility.DISPLAYED, Visibility.TEMPORAL).withTemporalCheck();
        toolbarFilterConfig = builder.build();
    }

    public boolean isSelectedUserNavigation(String nav) throws Exception {
        UIPortal uiPortal = Util.getUIPortal();
        UserNode selectedNode = uiPortal.getSelectedUserNode();
        if (selectedNode.getURI().contains(nav)) return true;
        if (MYSPACES_URI.equals(nav) && "notifications".equals(selectedNode.getURI())) return true;
        else if(Util.getPortalRequestContext().getRequest().getRequestURL().toString().contains(nav))   return true;
        else return false;
    }

    public boolean isProfileOwner() {
        return Utils.getViewerRemoteId().equals(getOwnerRemoteId());
    }

    public static String getOwnerRemoteId() {
        String currentUserName = org.exoplatform.platform.navigation.component.utils.NavigationUtils.getCurrentUser();
        if (currentUserName == null || currentUserName.equals("")) {
            return Utils.getViewerRemoteId();
        }
        return currentUserName;
    }


    //////////////////////////////////////////////////////////
    /**/                                                  /**/
    /**/         //utils METHOD//                         /**/
    /**/                                                  /**/
    //////////////////////////////////////////////////////////

    public String[] getUserNodesAsList() {
        String[] userNodeList=(String[]) ArrayUtils.add(null, PROFILE_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, ACTIVITIES_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, CONNEXIONS_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, WIKI_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, MYSPACES_URI);
        userNodeList=(String[])ArrayUtils.add(userNodeList, DASHBOARD_URI);
        return userNodeList;
    }

    public String[] getURLAsList() throws Exception {
        String[] urlList=(String[])ArrayUtils.add(null, getProfileLink());
        urlList=(String[])ArrayUtils.add(urlList, getactivitesURL());
        urlList=(String[])ArrayUtils.add(urlList, getrelationURL());
        urlList=(String[])ArrayUtils.add(urlList, getWikiURL());
        urlList=(String[])ArrayUtils.add(urlList, getMySpacesURL());
        urlList=(String[])ArrayUtils.add(urlList, DashboardUtils.getDashboardURL());

        return urlList;
    }

    //////////////////////////////////////////////////////////
    /**/                                                  /**/
    /**/         //GET URL METHOD//                       /**/
    /**/                                                  /**/
    //////////////////////////////////////////////////////////

    public String getMySpacesURL() {
        return getMySpacesUri(getOwnerRemoteId());
    }

    public String getactivitesURL() {
        return LinkProvider.getUserActivityUri(getOwnerRemoteId());
    }

    public String getrelationURL() {
        return LinkProvider.getUserConnectionsYoursUri(getOwnerRemoteId());
    }

    public String getWikiURL() {
        return NavigationURLUtils.getURLInCurrentPortal(WIKI_REF)+USER +getOwnerRemoteId()+WIKI_HOME;
    }

    public String getProfileLink() {
        return LinkProvider.getUserProfileUri(getOwnerRemoteId());
    }
    public static String getMySpacesUri(final String remoteId) {
        return getBaseUri(null, null) + "/spaces";
    }
    private static String getBaseUri(final String portalName, String portalOwner) {
        return "/" + getPortalName(portalName) + "/" + getPortalOwner(portalOwner);
    }
    private static String getPortalName(String portalName) {
        if (portalName == null || portalName.trim().length() == 0) {
            return PortalContainer.getCurrentPortalContainerName();
        }
        return portalName;
    }
    private static String getPortalOwner(String portalOwner) {
        if (portalOwner == null || portalOwner.trim().length() == 0) {
            try {
                return Util.getPortalRequestContext().getPortalOwner();
            } catch (Exception e) {
                return DEFAULT_PORTAL_OWNER;
            }
        }
        return portalOwner;
    }
}
